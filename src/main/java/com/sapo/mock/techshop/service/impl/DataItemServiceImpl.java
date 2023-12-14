package com.sapo.mock.techshop.service.impl;

import com.sapo.mock.techshop.common.constant.HttpStatusConstant;
import com.sapo.mock.techshop.dto.response.GeneralResponse;
import com.sapo.mock.techshop.service.ConnectionService;
import com.sapo.mock.techshop.service.DataItemService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.sql.*;
import java.util.*;


@Service
public class DataItemServiceImpl implements DataItemService {

    @Autowired
    private ConnectionService connectionService;

    @Override
    public GeneralResponse<?> createDataItem(Map<String, Object> dataItemRequest) {

        Connection connection = connectionService.getConnection();
        Statement statement;
        ResultSet resultSet = null;
        try {
            statement = connection.createStatement();
            resultSet = statement.executeQuery("select * from properties where type_data = 'item' and property_name != 'item_id'");
            int columnCount = resultSet.getMetaData().getColumnCount();

            List<Map<String, Object>> properties = new ArrayList<>();

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

            if (Objects.isNull(dataItemRequest.get("item_id")) || StringUtils.isBlank((String) dataItemRequest.get("item_id"))) {
                return GeneralResponse.error(HttpStatus.BAD_REQUEST.value(), "The item_id field is required");
            }

            if (((String) dataItemRequest.get("item_id")).length() > 128) {
                return GeneralResponse.error(HttpStatus.BAD_REQUEST.value(), "The item_id exceeds the max length of 128 characters");
            }

            String item_id = (String) dataItemRequest.get("item_id");
            dataItemRequest.remove("item_id");

            List<String> propDb = new ArrayList<>();

            properties.forEach(property -> propDb.add((String) property.get("property_name")));

            boolean nonProp = dataItemRequest.keySet().stream().anyMatch(key -> !propDb.contains(key));

            if (nonProp) {
                return GeneralResponse.error(HttpStatus.BAD_REQUEST.value(), "Property of the given name is not present in the database.");
            }
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT COUNT(*) FROM data_item WHERE item_id = ?");
            preparedStatement.setString(1, item_id);

            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return GeneralResponse.error(HttpStatus.BAD_REQUEST.value(), "The item_id is already present in the item catalog.");
            } else {

                StringBuffer stringBuffer = new StringBuffer();
                stringBuffer.append("INSERT INTO data_item (item_id");
                dataItemRequest.keySet().forEach(key -> {
                    stringBuffer.append(", ").append(key);
                });
                stringBuffer.append(") VALUES (").append(item_id);
                dataItemRequest.forEach((key, value) -> {
                    stringBuffer.append(", ").append(value);
                });
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
            resultSet = statement.executeQuery("select * from properties where type_data = 'item' and property_name != 'item_id'");
            int columnCount = resultSet.getMetaData().getColumnCount();

            List<Map<String, Object>> properties = new ArrayList<>();

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

            if (Objects.isNull(id) || StringUtils.isBlank(id)) {
                return GeneralResponse.error(HttpStatus.BAD_REQUEST.value(), "The item_id field is required");
            }

            dataItemRequest.remove("item_id");

            List<String> propDb = new ArrayList<>();

            properties.forEach(property -> propDb.add((String) property.get("property_name")));

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
                dataItemRequest.forEach((key, value) -> {
                    stringBuffer.append(key).append(" =  ").append("'").append(value).append("'").append(",");
                });
                stringBuffer.deleteCharAt(stringBuffer.length() - 1);
                stringBuffer.append(" WHERE item_id = ").append("'").append(id).append("';");
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

        return GeneralResponse.ok(HttpStatus.OK.value(), HttpStatusConstant.SUCCESS_MESSAGE);
    }

    @Override
    public GeneralResponse<?> getById(String id) {
        Connection connection = connectionService.getConnection();
        Statement statement;
        ResultSet resultSet = null;
        try {
            if (!id.matches("^[a-zA-Z0-9_\\-:@.]+$")) {
                return GeneralResponse.error(HttpStatus.BAD_REQUEST.value(), "The item_id does not match ^[a-zA-Z0-9_-:@.]+$");
            }
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM data_item WHERE item_id = ?");
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
    }

    @Override
    public GeneralResponse<?> bulkInsert(List<Map<String, Object>> data) {
        Connection connection = connectionService.getConnection();
        Statement statement;
        ResultSet resultSet = null;

        int countError = 0;
        if (data.size() > 50000) {
            return GeneralResponse.error(HttpStatus.BAD_REQUEST.value(), "The request size exceeds the limit of 50000 records");
        }
        try {
            statement = connection.createStatement();
            resultSet = statement.executeQuery("select * from properties where type_data = 'item'");
            int columnCount = resultSet.getMetaData().getColumnCount();

            List<Map<String, Object>> properties = new ArrayList<>();

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
                        stringBuffer.append("'").append(item.get(prop)).append("'").append(",");
                    } else {
                        stringBuffer.append("null").append(",");
                    }
                });
                stringBuffer.deleteCharAt(stringBuffer.length() - 1);
                stringBuffer.append("),");
            });
            stringBuffer.deleteCharAt(stringBuffer.length() - 1);
            statement.executeUpdate(stringBuffer.toString());
        } catch (
                SQLException e) {
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
        return GeneralResponse.ok(HttpStatus.OK.value(), String.format("Successfully imported %s records. Failed imported records might occur due to the item_id field is not present, or item_id exceeds the max length of 128 characters", data.size() - countError));
    }
}
