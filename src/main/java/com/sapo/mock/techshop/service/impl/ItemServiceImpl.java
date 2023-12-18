package com.sapo.mock.techshop.service.impl;

import com.sapo.mock.techshop.common.Utils.DataUtils;
import com.sapo.mock.techshop.common.constant.DataType;
import com.sapo.mock.techshop.common.constant.HttpStatusConstant;
import com.sapo.mock.techshop.dto.response.GeneralResponse;
import com.sapo.mock.techshop.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl extends ConnectionServiceImpl implements ItemService {
    @Override
    public GeneralResponse<?> createItem(Map<String, Object> itemRequest) {
//        // tạo kết nối database
//        Connection connection = getConnection();
//        // tạo statement để thực thi câu lệnh sql
//        Statement statement;
//        ResultSet resultSet = null;
//        try {
//            statement = connection.createStatement();
//            // Thực thi câu lệnh sql lấy các bản ghi có type_data là item trong bảng properties
//            statement.executeQuery("SELECT * FROM properties WHERE type_data = 'item' and property_name != 'item_id'");
//            //add trường property_name của các bản ghi thuộc query vừa tạo vào 1 array list String
//            List<Map<String, Object>> properties = new ArrayList<>();
//            while (statement.getResultSet().next()) {
//                Map<String, Object> map = new HashMap<>();
//                //lặp qua từng cột
//                for (int i = 1; i <= statement.getResultSet().getMetaData().getColumnCount(); i++) {
//                    //lấy tên cột
//                    String columnName = statement.getResultSet().getMetaData().getColumnName(i);
//                    //lấy giá trị của cột
//                    String columnValue = statement.getResultSet().getString(i);
//                    //add vào map
//                    map.put(columnName, columnValue);
//                }
//                //thêm vào list
//                properties.add(map);
//            }
//            //lặp qua các phần tử trong properties và in ra chúng
//            for (Map<String, Object> property : properties) {
//                System.out.println(property);
//            }
//
//            //kiểm tra xem itemRequest có chứa item_id không hoặc item_id có bị bỏ trống không, nếu không có thì trả về lỗi
//            if (itemRequest.get("item_id") == null || itemRequest.get("item_id").toString().isEmpty()) {
//                return GeneralResponse.error(HttpStatus.BAD_REQUEST.value(), "The item_id is required");
//            }
//
//            //kiểm tra xem item_id có vượt quá 128 ký tự hay không, nếu có thì trả về lỗi
//            if (itemRequest.get("item_id").toString().length() > 128) {
//                return GeneralResponse.error(HttpStatus.BAD_REQUEST.value(), "The item_id exceeds the max length of 128 characters");
//            }
//
//            String item_id = itemRequest.get("item_id").toString();
//            itemRequest.remove("item_id");
//
//            List<String> propDb = new ArrayList<>();
//            //lặp qua các property trong properties, add vào propDb giá trị property_name
//            for (Map<String, Object> property : properties) {
//                propDb.add(property.get("property_name").toString());
//            }
//
//            //Kiểm tra xem có bất kỳ phần tử nào trong tập hợp các khóa của itemRequest mà không tồn tại trong tập hợp propDb hay không
//            boolean check = itemRequest.keySet().stream().anyMatch(key -> !propDb.contains(key));
//            //nếu không có thì trả về lỗi
//            if (check) {
//                return GeneralResponse.error(HttpStatus.BAD_REQUEST.value(), "Property of the given name is not present in the database");
//            }
//
//            //kiểm tra xem item_id có trùng với item_id của bản ghi nào trong bảng data_item hay không, nếu có thì trả về lỗi
//            resultSet = statement.executeQuery("SELECT COUNT(*) FROM dev1year.data_item WHERE item_id = '" + item_id + "'");
//
//            if (resultSet.next()) {
//                return GeneralResponse.error(HttpStatus.BAD_REQUEST.value(), "The item_id is already present in the item catalog.");
//            } else {
//                StringBuilder sql = new StringBuilder();
//                sql.append("INSERT INTO dev1year.data_item (item_id");
//                itemRequest.keySet().forEach(key -> sql.append(", ").append(key));
//                sql.append(") VALUES ('").append(item_id);
//                itemRequest.forEach((key, value) -> sql.append("', '").append(value));
//                sql.append("');");
//                statement.executeUpdate(sql.toString());
//            };
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            //đóng kết nối
//            try {
//                connection.close();
//            } catch (SQLException e) {
//                e.printStackTrace();
//            }
//        }
        Connection connection = this.getConnection();
        Statement statement;
        ResultSet resultSet = null;
        try {
            statement = connection.createStatement();

            List<Map<String, Object>> properties = this.getProperties(connection);

            if (Objects.isNull(itemRequest.get("item_id")) || StringUtils.isBlank((String) itemRequest.get("item_id"))) {
                return GeneralResponse.error(HttpStatus.BAD_REQUEST.value(), "The item_id field is required");
            }

            if (((String) itemRequest.get("item_id")).length() > 128) {
                return GeneralResponse.error(HttpStatus.BAD_REQUEST.value(), "The item_id exceeds the max length of 128 characters");
            }

            String item_id = (String) itemRequest.get("item_id");
            itemRequest.remove("item_id");

            List<String> propDb = new ArrayList<>();

            properties.forEach(property -> propDb.add((String) property.get("property_name")));

            boolean check = itemRequest.keySet().stream().anyMatch(key -> !propDb.contains(key));

            if (check) {
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
                itemRequest.keySet().forEach(key -> {
                    stringBuffer.append(", ").append(key);
                });
                stringBuffer.append(") VALUES ('").append(item_id).append("'");
                itemRequest.forEach((key, value) -> {
                    stringBuffer.append(", '").append(value).append("'");
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
        return GeneralResponse.ok(HttpStatus.OK.value(), HttpStatusConstant.CREATE_SUCCESS);
    }

    @Override
    public GeneralResponse<?> updateItem(Map<String, Object> itemRequest, String itemId){
        // tạo kết nối database
        Connection connection = getConnection();
        // tạo statement để thực thi câu lệnh sql
        Statement statement;
        try {
            statement = connection.createStatement();
            // Thực thi câu lệnh sql lấy các bản ghi có type_data là item trong bảng properties
            statement.executeQuery("SELECT * FROM properties WHERE type_data = 'item' and property_name != 'item_id'");
            //add trường property_name của các bản ghi thuộc query vừa tạo vào 1 array list String
            List<Map<String, Object>> properties = new ArrayList<>();
            while (statement.getResultSet().next()) {
                Map<String, Object> map = new HashMap<>();
                //lặp qua từng cột
                for (int i = 1; i <= statement.getResultSet().getMetaData().getColumnCount(); i++) {
                    //lấy tên cột
                    String columnName = statement.getResultSet().getMetaData().getColumnName(i);
                    //lấy giá trị của cột
                    String columnValue = statement.getResultSet().getString(i);
                    //add vào map
                    map.put(columnName, columnValue);
                }
                //thêm vào list
                properties.add(map);
            }
            //lặp qua các phần tử trong properties và in ra chúng
            for (Map<String, Object> property : properties) {
                System.out.println(property);
            }

            //kiểm tra xem itemRequest có chứa item_id không hoặc item_id có bị bỏ trống không, nếu không có thì trả về lỗi
            if (itemId == null || itemId.isEmpty()) {
                return GeneralResponse.error(HttpStatus.BAD_REQUEST.value(), "The item_id is required");
            }

            itemRequest.remove("item_id");

            List<String> propDb = new ArrayList<>();
            //lặp qua các property trong properties, add vào propDb giá trị property_name
            for (Map<String, Object> property : properties) {
                propDb.add(property.get("property_name").toString());
            }

            //Kiểm tra xem có bất kỳ phần tử nào trong tập hợp các khóa của itemRequest mà không tồn tại trong tập hợp propDb hay không
            boolean check = itemRequest.keySet().stream().anyMatch(key -> !propDb.contains(key));
            //nếu không có thì trả về lỗi
            if (check) {
                return GeneralResponse.error(HttpStatus.BAD_REQUEST.value(), "Property of the given name is not present in the database");
            }

            //kiểm tra xem item_id có trùng với item_id của bản ghi nào trong bảng data_item hay không, nếu không thì trả về lỗi
            statement.executeQuery("SELECT COUNT(*) FROM dev1year.data_item WHERE item_id = '" + itemId + "'");
            if (!statement.getResultSet().next()) {
                return GeneralResponse.error(HttpStatus.BAD_REQUEST.value(), "The item_id is not present in the item catalog.");
            } else {
                StringBuilder sql = new StringBuilder();
                sql.append("UPDATE dev1year.data_item SET ");
                itemRequest.forEach((key, value) -> sql.append(key).append(" = '").append(value).append("'").append(","));
                sql.deleteCharAt(sql.length() - 1);
                sql.append(" WHERE item_id = '").append(itemId).append("';");
                statement.executeUpdate(sql.toString());
            };
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //đóng kết nối
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return GeneralResponse.ok(HttpStatus.OK.value(), HttpStatusConstant.SUCCESS);
    }

    @Override
    public GeneralResponse<?> getItem(String itemId) {
        // tạo kết nối database
        Connection connection = getConnection();
        // tạo statement để thực thi câu lệnh sql
        Statement statement;
        try {
            statement = connection.createStatement();
            //kiểm tra xem item_id có match với ^[a-zA-Z0-9_-:@.]+$ hay không, nếu không thì trả về lỗi
            if (!itemId.matches(DataUtils.REGEX)) {
                return GeneralResponse.error(HttpStatus.BAD_REQUEST.value(), "The item_id does not match ^[a-zA-Z0-9_-:@.]+$");
            }

            statement.executeQuery("SELECT * FROM dev1year.data_item WHERE item_id = '" + itemId + "'");
            if (!statement.getResultSet().next()) {
                return GeneralResponse.error(HttpStatus.BAD_REQUEST.value(), "The item_id is not present in the item catalog.");
            } else {
                Map<String, Object> map = new HashMap<>();
                //lặp qua từng cột
                for (int i = 1; i <= statement.getResultSet().getMetaData().getColumnCount(); i++) {
                    //lấy tên cột
                    String columnName = statement.getResultSet().getMetaData().getColumnName(i);
                    //lấy giá trị của cột
                    String columnValue = statement.getResultSet().getString(i);
                    //add vào map
                    map.put(columnName, columnValue);
                }
                return GeneralResponse.ok(map, HttpStatusConstant.SUCCESS);
            }
        } catch (Exception e) {
            return GeneralResponse.error(HttpStatus.BAD_REQUEST.value(), "Error");
        } finally {
            //đóng kết nối
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public GeneralResponse<?> bulkImportItem(List<Map<String, Object>> itemRequest) {
        // tạo kết nối database
        Connection connection = getConnection();
        // tạo statement để thực thi câu lệnh sql
        Statement statement = null;
        ResultSet resultSet = null;
        int errorRecords = 0;
        //Kiểm tra số lượng object trong itemRequest có lớn hơn 50000 hay không, nếu có thì trả về lỗi
        if (itemRequest.size() > 50000) {
            return GeneralResponse.error(HttpStatus.BAD_REQUEST.value(), "The request size exceeds the limit of 50000 records");
        }
        try {
            //Lấy ra list item_id trong database
            statement = connection.createStatement();
            resultSet = statement.executeQuery("SELECT item_id FROM data_item");
            List<String> itemIds = new ArrayList<>();
            while (resultSet.next()) {
                itemIds.add(resultSet.getString("item_id"));
            }

            List<Map<String, Object>> listImport = new ArrayList<>();
            //duyệt qua từng phần tử trong itemRequest
            for (Map<String, Object> item : itemRequest) {
                //Kiểm tra xem item có chứa item_id không hoặc item_id có bị bỏ trống không, nếu không có thì bỏ qua
                if (!item.containsKey("item_id") || item.get("item_id").toString().isBlank()) {
                    errorRecords++;
                    continue;
                }
                //kiểm tra độ dài item_id có lớn hơn 128 ký tự hay không, nếu có thì bỏ qua
                if (item.get("item_id").toString().length() > 128) {
                    errorRecords++;
                    continue;
                }
                //kiểm tra xem item_id có tồn tại trong database hay không, nếu có thì bỏ qua
                if (itemIds.contains(item.get("item_id").toString())) {
                    errorRecords++;
                    continue;
                }
            //add item hợp lệ vào listImport
                listImport.add(item);
            }

            List<Map<String, Object>> properties = this.getProperties(connection);
            Map<String, String> propertyMap = properties.stream().collect(Collectors.toMap(property -> (String) property.get("property_name"), property -> (String) property.get("data_type")));
            List<String> propertyNames = new ArrayList<>();
            properties.forEach(property -> propertyNames.add((String) property.get("property_name")));
            boolean error = false;
            //duyệt qua từng phần tử trong itemRequest
            for (Map<String, Object> item : itemRequest) {
                boolean notProperty = false;
                for (String key : item.keySet()) {
                    //Nếu không tồn tại property_name trong database thì notProperty = true, bỏ qua
                    if (!propertyNames.contains(key)) {
                        notProperty = true;
                        break;
                    }
                }
                if (notProperty) {
                    error = true;
                    break;
                }
            }
            if (error) {
                return GeneralResponse.error(HttpStatus.BAD_REQUEST.value(), "Property of the given name is not present in the database");
            }

            //Ghép toàn bộ thông tin lấy được thành câu lệnh insert
            StringBuilder sql = new StringBuilder();
            sql.append("INSERT INTO dev1year.data_item ( ");
            propertyNames.forEach(property -> sql.append(property).append(","));
            sql.deleteCharAt(sql.length() - 1);
            sql.append(") VALUES ");
            listImport.forEach(item -> {
                sql.append("(");
                propertyNames.forEach(property -> {
                    if (item.containsKey(property) && Objects.nonNull(item.get(property))) {
                        sql.append(DataUtils.getValue(item.get(property), propertyMap.get(property))).append(",");
                    } else {
                        sql.append("null").append(",");
                    }
                });
                sql.deleteCharAt(sql.length() - 1);
                sql.append("),");
            });
            sql.deleteCharAt(sql.length() - 1);
            statement.executeQuery(sql.toString());
        }
        catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (Objects.nonNull(connection)) {
                    connection.close();
                }
                if (Objects.nonNull(resultSet)) {
                    resultSet.close();
                }
                if (Objects.nonNull(statement)) {
                    statement.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return GeneralResponse.ok(HttpStatus.OK.value(),  String.format("Successfully imported %s records. Failed imported records might occur due to the item_id field is not present, or item_id exceeds the max length of 128 characters", itemRequest.size() - errorRecords));
    }

    @Override
    public GeneralResponse<?> createItemProperty(Map<String, Object> request) {
        Connection connection = getConnection();
        Statement statement = null;
        ResultSet resultSet = null;

        String property_name = (String) request.get("property-name");
        if (!request.containsKey("property-name") || Objects.isNull(request.get("property-name")) || property_name.isBlank() ||
                !property_name.matches(DataUtils.REGEX) || property_name.length() > 50 || property_name.equals("id") || property_name.equals("item_id")) {
            return GeneralResponse.error(HttpStatus.BAD_REQUEST.value(), "Property name does not match ^[a-zA-Z_][0-9a-zA-Z_]*$, or it\n" +
                    "is a reserved keyword (‘’id’’, ‘’item_id’’), or its length exceeds 50 characters.\n" +
                    "Type information is missing, or the given type is invalid.");
        }
        try {
            List<Map<String, Object>> properties = this.getProperties(connection);
            List<String> propertyNames = new ArrayList<>();
            properties.forEach(property -> propertyNames.add((String) property.get("property_name")));

            if (propertyNames.contains(property_name)) {
                return GeneralResponse.error(HttpStatus.BAD_REQUEST.value(), "Property of the given name is already present in the database. In\n" +
                        "many cases, you may consider this code success – it only tells you that nothing\n" +
                        "has been written to the database.");
            }
            //Kiểm tra xem type có phải là Text, TextSet, Image, ImageSet, Timestamp, Boolean, Integer, Decimal hay không, nếu không thì trả về lỗi
            if (!DataType.validType(request.get("type").toString()) || Objects.isNull(request.get("type")) || request.get("type").toString().isBlank()) {
                return GeneralResponse.error(HttpStatus.BAD_REQUEST.value(), "Property name does not match ^[a-zA-Z_][0-9a-zA-Z_]*$, or it\n" +
                        "is a reserved keyword (‘’id’’, ‘’item_id’’), or its length exceeds 50 characters.\n" +
                        "Type information is missing, or the given type is invalid.");
            }

            String data_type = DataType.getValueOf(request.get("type").toString());
            statement = connection.createStatement();
//            statement.executeQuery("INSERT INTO dev1year.properties (property_name, data_type, type_data) VALUES ('" + property_name + "', '" + data_type + "', 'item')");
            statement.executeUpdate(String.format("INSERT INTO dev1year.properties (property_name, data_type, type_data) VALUES ('%s', '%s', '%s')", property_name, data_type, "item"));
            statement.executeUpdate("Alter table dev1year.data_item add column " + property_name + " " + data_type + ";");
            return GeneralResponse.ok(HttpStatus.CREATED.value(), HttpStatusConstant.CREATE_SUCCESS);
        } catch (SQLException e) {
            e.printStackTrace();
            return GeneralResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Error");
        } finally {
            this.closeConnection(connection, resultSet, statement);
        }
    }

    @Override
    public GeneralResponse<?> deleteItemProperty(String propertyName) {
        Connection connection = getConnection();
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            List<Map<String, Object>> properties = this.getProperties(connection);
            List<String> propertyNames = new ArrayList<>();
            properties.forEach(property -> propertyNames.add((String) property.get("property_name")));

            if (!propertyNames.contains(propertyName)) {
                return GeneralResponse.error(HttpStatus.BAD_REQUEST.value(), "Property of the given name is not present in the database. In\n" +
                        "many cases, you may consider this code success – it only tells you that nothing\n" +
                        "has been deleted from the database since the item property was already not\n" +
                        "present.");
            }

            statement = connection.createStatement();
            statement.executeUpdate("DELETE FROM dev1year.properties WHERE property_name = '" + propertyName + "' and type_data = 'item';");
            statement.executeUpdate("Alter table dev1year.data_item drop column " + propertyName + ";");
            return GeneralResponse.ok(HttpStatus.OK.value(), HttpStatusConstant.SUCCESS);
        } catch (SQLException e) {
            e.printStackTrace();
            return GeneralResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Error");
        } finally {
            this.closeConnection(connection, resultSet, statement);
        }
    }

    @Override
    public GeneralResponse<?> getItemProperty(String propertyName) {
        Connection connection = getConnection();
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            List<Map<String, Object>> properties = this.getProperties(connection);
            List<String> propertyNames = new ArrayList<>();
            properties.forEach(property -> propertyNames.add((String) property.get("property_name")));

            if (!propertyNames.contains(propertyName)) {
                return GeneralResponse.error(HttpStatus.NOT_FOUND.value(), "Property of the given name is not present in the database.");
            }

            statement = connection.createStatement();
            resultSet = statement.executeQuery("SELECT * FROM dev1year.properties WHERE property_name = '" + propertyName + "' and type_data = 'item'");
            if (resultSet.next()) {
                Map<String, String> property = new HashMap<>();
                property.put("property-name", resultSet.getString("property_name"));
                property.put("type", DataType.getKeyOf(resultSet.getString("data_type")));
                return GeneralResponse.ok(HttpStatus.OK.value(), HttpStatusConstant.SUCCESS, property);
            } else {
                return GeneralResponse.ok(HttpStatus.OK.value(), HttpStatusConstant.SUCCESS, new HashMap<>());
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return GeneralResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Error");
        } finally {
            this.closeConnection(connection, resultSet, statement);
        }
    }

    @Override
    public GeneralResponse<?> getAllItemProperty() {
        Connection connection = getConnection();
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            statement = connection.createStatement();
            resultSet = statement.executeQuery("SELECT property_name, data_type FROM dev1year.properties WHERE type_data = 'item'");
            List<Map<String, String>> listItemProperty = new ArrayList<>();
            while (resultSet.next()) {
                Map<String, String> property = new HashMap<>();
                property.put("type", resultSet.getString("data_type"));
                property.put("property-name", resultSet.getString("property_name"));
                listItemProperty.add(property);
            }
                return GeneralResponse.ok(HttpStatus.OK.value(), HttpStatusConstant.SUCCESS, listItemProperty);
        } catch (SQLException e) {
            e.printStackTrace();
            return GeneralResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Error");
        } finally {
            this.closeConnection(connection, resultSet, statement);
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

    private void closeConnection(Connection connection, ResultSet resultSet, Statement statement) {
        try {
            if (Objects.nonNull(connection)) {
                connection.close();
            }
            if (Objects.nonNull(resultSet)) {
                resultSet.close();
            }
            if (Objects.nonNull(statement)) {
                statement.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
