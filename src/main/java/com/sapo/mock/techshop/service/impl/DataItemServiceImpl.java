package com.sapo.mock.techshop.service.impl;

import com.sapo.mock.techshop.common.Utils.DataUtils;
import com.sapo.mock.techshop.common.constant.DataType;
import com.sapo.mock.techshop.common.constant.HttpStatusConstant;
import com.sapo.mock.techshop.dto.response.GeneralResponse;
import com.sapo.mock.techshop.service.ConnectionService;
import com.sapo.mock.techshop.service.DataItemService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class DataItemServiceImpl implements DataItemService {

    private final ConnectionService connectionService;

    @Override
    public GeneralResponse<?> createDataItem(Map<String, Object> dataItemRequest) {

        Connection connection = connectionService.getConnection();
        Statement statement;
        ResultSet resultSet = null;
        try {
            statement = connection.createStatement();

            List<Map<String, Object>> properties = this.getProperties(connection);

            if (Objects.isNull(dataItemRequest.get("item_id")) || StringUtils.isBlank((String) dataItemRequest.get("item_id"))) {
                return GeneralResponse.error(HttpStatus.BAD_REQUEST.value(), "The item_id field is required");
            }

            if (((String) dataItemRequest.get("item_id")).length() > 128) {
                return GeneralResponse.error(HttpStatus.BAD_REQUEST.value(), "The item_id exceeds the max length of 128 characters");
            }

            String item_id = (String) dataItemRequest.get("item_id");

            List<String> propDb = new ArrayList<>();

            properties.forEach(property -> propDb.add((String) property.get("property_name")));
            Map<String, String> mapPropertyType = properties.stream().collect(Collectors.toMap(property -> (String) property.get("property_name"), property -> (String) property.get("data_type")));

            boolean nonProp = dataItemRequest.keySet().stream().anyMatch(key -> !propDb.contains(key));

            if (nonProp) {
                return GeneralResponse.error(HttpStatus.BAD_REQUEST.value(), "Property of the given name is not present in the database.");
            }
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM data_item WHERE item_id = ?");
            preparedStatement.setString(1, item_id);

            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return GeneralResponse.error(HttpStatus.BAD_REQUEST.value(), "The item_id is already present in the item catalog.");
            } else {

                StringBuffer stringBuffer = new StringBuffer();
                stringBuffer.append("INSERT INTO data_item (");
                dataItemRequest.keySet().forEach(key -> stringBuffer.append(key).append(", "));
                stringBuffer.deleteCharAt(stringBuffer.lastIndexOf(","));
                stringBuffer.append(") VALUES (");
                dataItemRequest.forEach((key, value) -> stringBuffer.append(DataUtils.getValue(value, mapPropertyType.get(key))).append(","));
                stringBuffer.deleteCharAt(stringBuffer.lastIndexOf(","));
                stringBuffer.append(");");
                statement.executeUpdate(stringBuffer.toString());
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            // Đóng tất cả các resource sau khi sử dụng xong
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return GeneralResponse.ok(HttpStatus.CREATED.value(), HttpStatusConstant.CREATE_SUCCESS_MESSAGE);
    }

    @Override
    public GeneralResponse<?> update(Map<String, Object> dataItemRequest, String id) {

        Connection connection = connectionService.getConnection();
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            statement = connection.createStatement();
            List<Map<String, Object>> properties = this.getProperties(connection);

            if (Objects.isNull(id) || StringUtils.isBlank(id)) {
                return GeneralResponse.error(HttpStatus.BAD_REQUEST.value(), "The item_id field is required");
            }

            dataItemRequest.remove("item_id");

            List<String> propDb = new ArrayList<>();

            properties.forEach(property -> propDb.add((String) property.get("property_name")));
            Map<String, String> mapPropertyType = properties.stream().collect(Collectors.toMap(property -> (String) property.get("property_name"), property -> (String) property.get("data_type")));

            boolean nonProp = dataItemRequest.keySet().stream().anyMatch(key -> !propDb.contains(key));

            if (nonProp) {
                return GeneralResponse.error(HttpStatus.BAD_REQUEST.value(), "Property of the given name is not present in the database.");
            }

            PreparedStatement preparedStatement = connection.prepareStatement("SELECT COUNT(*) FROM data_item WHERE item_id = ?");
            preparedStatement.setString(1, id);

            resultSet = preparedStatement.executeQuery();
            resultSet.next();
            int count = resultSet.getInt(1);
            if (count < 1) {
                return GeneralResponse.error(HttpStatus.BAD_REQUEST.value(), "The item_id is not present in the item catalog.");
            } else {
                StringBuffer stringBuffer = new StringBuffer();
                stringBuffer.append("UPDATE data_item SET ");
                dataItemRequest.forEach((key, value) -> stringBuffer.append(key).append(" =  ").append(DataUtils.getValue(value, mapPropertyType.get(key))).append(","));
                stringBuffer.deleteCharAt(stringBuffer.lastIndexOf(","));
                stringBuffer.append(" WHERE item_id = ").append("'").append(id).append("';");
                statement.executeUpdate(stringBuffer.toString());
            }
        } catch (SQLException e) {
            return GeneralResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), HttpStatusConstant.SQL_ERROR_CODE);
        } finally {
            // Đóng tất cả các resource sau khi sử dụng xong
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return GeneralResponse.ok(HttpStatus.OK.value(), HttpStatusConstant.SUCCESS_MESSAGE);
    }

    @Override
    public GeneralResponse<?> getById(String id) {
        Connection connection = connectionService.getConnection();
        ResultSet resultSet = null;
        PreparedStatement preparedStatement = null;
        try {
            if (!id.matches("^[a-zA-Z0-9_\\-:@.]+$")) {
                return GeneralResponse.error(HttpStatus.BAD_REQUEST.value(), "The item_id does not match ^[a-zA-Z0-9_-:@.]+$");
            }
            preparedStatement = connection.prepareStatement("SELECT * FROM data_item WHERE item_id = ?");
            preparedStatement.setString(1, id);

            resultSet = preparedStatement.executeQuery();
            int columnCount = resultSet.getMetaData().getColumnCount();
            if (!resultSet.next()) {
                return GeneralResponse.error(HttpStatus.BAD_REQUEST.value(), "Item of the given item_id is not present in the catalog.");
            } else {
                Map<String, Object> objectMap = new HashMap<>();
                for (int i = 1; i <= columnCount; i++) {
                    String columnName = resultSet.getMetaData().getColumnName(i);
                    Object columnValue = resultSet.getObject(i);
                    objectMap.put(columnName, columnValue);
                }
                return GeneralResponse.ok(objectMap, HttpStatusConstant.SUCCESS_MESSAGE);
            }
        } catch (SQLException e) {
            return GeneralResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), HttpStatusConstant.SQL_ERROR_CODE);
        } finally {
            // Đóng tất cả các resource sau khi sử dụng xong
            try {
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
                if (resultSet != null) {
                    resultSet.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public GeneralResponse<?> bulkInsert(List<Map<String, Object>> data) {
        Connection connection = connectionService.getConnection();
        Statement statement = null;

        int countError = 0;
        if (data.size() > 50000) {
            return GeneralResponse.error(HttpStatus.BAD_REQUEST.value(), "The request size exceeds the limit of 50000 records");
        }
        try {
            statement = connection.createStatement();
            List<Map<String, Object>> properties = this.getProperties(connection);

            Map<String, String> mapPropertyType = properties.stream().collect(Collectors.toMap(property -> (String) property.get("property_name"), property -> (String) property.get("data_type")));
            List<String> propDb = new ArrayList<>();
            properties.forEach(property -> propDb.add((String) property.get("property_name")));
            boolean badReq = false;
            for (Map<String, Object> item : data) {
                boolean nonProp = false;
                for (String key : item.keySet()) {
                    if (!propDb.contains(key)) {
                        nonProp = true;
                        break;
                    }
                }
                if (nonProp) {
                    badReq = true;
                    break;
                }
            }

            if (badReq) {
                return GeneralResponse.error(HttpStatus.BAD_REQUEST.value(), "Property of the given name is not present in the database.");
            }

            ResultSet resultSet1 = statement.executeQuery("SELECT item_id FROM data_item");

            List<String> idList = new ArrayList<>();
            while (resultSet1.next()) {
                String id = resultSet1.getString("item_id");
                idList.add(id);
            }

            List<Map<String, Object>> obj = new ArrayList<>();
            for (Map<String, Object> item : data) {
                if (!item.containsKey("item_id")) {
                    countError++;
                    continue;
                }
                String id = (String) item.get("item_id");
                if (StringUtils.isBlank(id) || idList.contains(id) || id.length() > 128) {
                    countError++;
                    continue;
                }
                obj.add(item);
            }

            if (obj.isEmpty()) {
                return GeneralResponse.ok(HttpStatus.OK.value(), String.format("Successfully imported %s records. Failed imported records might occur due to the item_id field is not present, or item_id exceeds the max length of 128 characters", data.size() - countError));
            }

            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append("INSERT INTO data_item (");

            propDb.forEach(prop -> stringBuffer.append(prop).append(","));
            stringBuffer.deleteCharAt(stringBuffer.length() - 1);
            stringBuffer.append(") VALUES ");
            obj.forEach(item -> {
                stringBuffer.append("(");
                propDb.forEach(prop -> {
                    if (item.containsKey(prop) && Objects.nonNull(item.get(prop))) {
                        stringBuffer.append(DataUtils.getValue(item.get(prop), mapPropertyType.get(prop))).append(",");
                    } else {
                        stringBuffer.append("null").append(",");
                    }
                });
                stringBuffer.deleteCharAt(stringBuffer.length() - 1);
                stringBuffer.append("),");
            });
            stringBuffer.deleteCharAt(stringBuffer.length() - 1);
            statement.executeUpdate(stringBuffer.toString());
        } catch (SQLException e) {
            return GeneralResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), HttpStatusConstant.SQL_ERROR_CODE);
        } finally {
            // Đóng tất cả các resource sau khi sử dụng xong
            try {
                if (statement != null) {
                    statement.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return GeneralResponse.ok(HttpStatus.OK.value(), String.format("Successfully imported %s records. Failed imported records might occur due to the item_id field is not present, or item_id exceeds the max length of 128 characters", data.size() - countError));
    }

    @Override
    public GeneralResponse<?> createItemProperty(Map<String, Object> propertyReq) {
        Connection connection = connectionService.getConnection();
        Statement statement = null;

        String property_name = (String) propertyReq.get("property-name");

        if (!propertyReq.containsKey("property-name") || Objects.isNull(propertyReq.get("property-name")) || StringUtils.isBlank(property_name) ||
                !property_name.matches("^[a-zA-Z_][0-9a-zA-Z_]*$") || property_name.length() > 50 || property_name.equals("id") || property_name.equals("item_id")) {
            return GeneralResponse.error(HttpStatus.BAD_REQUEST.value(), HttpStatusConstant.INVALID_PROPERTY_ITEM);
        }
        try {
            List<Map<String, Object>> properties = this.getProperties(connection);

            List<String> propDb = new ArrayList<>();
            properties.forEach(property -> propDb.add((String) property.get("property_name")));

            if (propDb.contains(property_name)) {
                return GeneralResponse.error(HttpStatus.CONFLICT.value(), HttpStatusConstant.CONFLICT_PROPERTY_ITEM);
            }

            if (!DataType.validType((String) propertyReq.get("type")) || StringUtils.isBlank((String) propertyReq.get("type"))) {
                return GeneralResponse.error(HttpStatus.BAD_REQUEST.value(), HttpStatusConstant.TYPE_NOT_DEFINE);
            }

            String typeData = DataType.getValueOf((String) propertyReq.get("type"));

            statement = connection.createStatement();
            statement.executeUpdate(String.format("INSERT INTO properties (property_name, data_type, type_data) VALUES ('%s', '%s', '%s')", property_name, typeData, "item"));
            statement.executeUpdate("ALTER TABLE data_item ADD COLUMN " + property_name + " " + typeData);
            return GeneralResponse.ok(HttpStatus.CREATED.value(), HttpStatusConstant.CREATE_SUCCESS_MESSAGE, Map.of("property-name", property_name, "type", typeData));
        } catch (SQLException e) {
            e.printStackTrace();
            return GeneralResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), HttpStatusConstant.SQL_ERROR_CODE);
        } finally {
            // Đóng tất cả các resource sau khi sử dụng xong
            try {
                if (statement != null) {
                    statement.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public GeneralResponse<?> deleteItemProperty(String propertyName) {
        Connection connection = connectionService.getConnection();
        Statement statement = null;

        try {
            List<Map<String, Object>> properties = this.getProperties(connection);

            List<String> propDb = new ArrayList<>();
            properties.forEach(property -> propDb.add((String) property.get("property_name")));

            if (!propDb.contains(propertyName)) {
                return GeneralResponse.error(HttpStatus.BAD_REQUEST.value(), HttpStatusConstant.ITEM_PROPERTY_NOT_EXIST);
            }

            statement = connection.createStatement();
            statement.executeUpdate(String.format("DELETE FROM properties WHERE property_name = '%s' AND type_data = 'item'", propertyName));
            statement.executeUpdate("ALTER TABLE data_item DROP COLUMN " + propertyName);
            return GeneralResponse.ok(HttpStatus.OK.value(), HttpStatusConstant.SUCCESS_MESSAGE);
        } catch (SQLException e) {
            e.printStackTrace();
            return GeneralResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), HttpStatusConstant.SQL_ERROR_CODE);
        } finally {
            // Đóng tất cả các resource sau khi sử dụng xong
            try {
                if (statement != null) {
                    statement.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public GeneralResponse<?> getItemProperty(String propertyName) {

        Connection connection = connectionService.getConnection();
        Statement statement = null;
        ResultSet resultSet = null;
        try {
//            List<Map<String, Object>> properties = this.getProperties(connection);

//            List<String> propDb = new ArrayList<>();
//            properties.forEach(property -> propDb.add((String) property.get("property_name")));

//            if (!propDb.contains(propertyName)) {
//                return GeneralResponse.error(HttpStatus.NOT_FOUND.value(), HttpStatusConstant.PROPERTY_NOT_IN_DATABASE);
//            }

            statement = connection.createStatement();
            resultSet = statement.executeQuery(String.format("SELECT * FROM properties WHERE property_name = '%s' AND type_data = 'item'", propertyName));
            if (resultSet.next()) {
                Map<String, String> map = new HashMap<>();
                map.put("property_name", resultSet.getString("property_name"));
                map.put("data_type", resultSet.getString("data_type"));
                return GeneralResponse.ok(HttpStatus.OK.value(), HttpStatusConstant.SUCCESS_MESSAGE, map);
            } else {
                return GeneralResponse.ok(HttpStatus.OK.value(), HttpStatusConstant.SUCCESS_MESSAGE, Collections.emptyMap());
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return GeneralResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), HttpStatusConstant.SQL_ERROR_CODE);
        } finally {
            // Đóng tất cả các resource sau khi sử dụng xong
            handleConnection(connection, statement, resultSet);
        }
    }

    @Override
    public GeneralResponse<?> getListItemProperty() {
        Connection connection = connectionService.getConnection();
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            statement = connection.createStatement();
            resultSet = statement.executeQuery("SELECT * FROM properties WHERE type_data = 'user'");
            List<Map<String, String>> list = new ArrayList<>();
            while (resultSet.next()) {
                Map<String, String> map = new HashMap<>();
                map.put("data_type", resultSet.getString("data_type"));
                map.put("property_name", resultSet.getString("property_name"));
                list.add(map);
            }
            return GeneralResponse.ok(HttpStatus.OK.value(), HttpStatusConstant.SUCCESS_MESSAGE, list);
        } catch (SQLException e) {
            e.printStackTrace();
            return GeneralResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), HttpStatusConstant.SQL_ERROR_CODE);
        } finally {
            // Đóng tất cả các resource sau khi sử dụng xong
            handleConnection(connection, statement, resultSet);
        }
    }

    private void handleConnection(Connection connection, Statement statement, ResultSet resultSet) {
        try {
            if (statement != null) {
                statement.close();
            }
            if (connection != null) {
                connection.close();
            }
            if (resultSet != null) {
                resultSet.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private List<Map<String, Object>> getProperties(Connection connection) {
        Statement statement = null;
        ResultSet resultSet = null;
        List<Map<String, Object>> properties = new ArrayList<>();
        try {
            statement = connection.createStatement();
            resultSet = statement.executeQuery("select * from properties where type_data = 'item'");
            int columnCount = resultSet.getMetaData().getColumnCount();

            while (resultSet.next()) {
                Map<String, Object> objectMap = new HashMap<>();
                // Lặp qua từng cột
                for (int i = 1; i <= columnCount; i++) {
                    String columnName = resultSet.getMetaData().getColumnName(i);
                    Object columnValue = resultSet.getObject(i);
                    objectMap.put(columnName, columnValue);
                }
                properties.add(objectMap);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (statement != null) {
                    statement.close();
                }
                if (resultSet != null) {
                    resultSet.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return properties;
    }
}
