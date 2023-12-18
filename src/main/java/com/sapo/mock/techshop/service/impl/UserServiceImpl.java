package com.sapo.mock.techshop.service.impl;

import com.sapo.mock.techshop.common.Utils.DataUtils;
import com.sapo.mock.techshop.common.constant.DataType;
import com.sapo.mock.techshop.common.constant.HttpStatusConstant;
import com.sapo.mock.techshop.dto.response.GeneralResponse;
import com.sapo.mock.techshop.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

@Service
public class UserServiceImpl extends ConnectionServiceImpl implements UserService {
    @Override
    public GeneralResponse<?> createUser(Map<String, Object> userRequest) {
        Connection connection = getConnection();
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            String sql = "Select * from properties where type_data = 'user'";
            statement = connection.createStatement();
            statement.executeQuery(sql);
            List<Map<String, Object>> properties = new ArrayList<>();
            while (statement.getResultSet().next()) {
                Map<String, Object> property = new HashMap<>();
                for (int i = 1; i <= statement.getResultSet().getMetaData().getColumnCount(); i++) {
                    String columnName = statement.getResultSet().getMetaData().getColumnName(i);
                    String columnValue = statement.getResultSet().getString(i);
                    property.put(columnName, columnValue);
                }
                properties.add(property);
            }

            if (userRequest.get("user_id") == null || userRequest.get("user_id").toString().isEmpty()) {
                return GeneralResponse.error(HttpStatus.BAD_REQUEST.value(), "The user_id field is required");
            }

            if (userRequest.get("user_id").toString().length() > 128) {
                return GeneralResponse.error(HttpStatus.BAD_REQUEST.value(), "The user_id exceeds the max length of 128 characters");
            }

            String userId = (String) userRequest.get("user_id");

            List<String> propertyNames = new ArrayList<>();
            for (Map<String, Object> property : properties) {
                propertyNames.add(property.get("property_name").toString());
            }
            boolean exist = userRequest.keySet().stream().anyMatch(key -> propertyNames.contains(key));
            if (!exist) {
                return GeneralResponse.error(HttpStatus.BAD_REQUEST.value(), "Property of the given name is not present in the database.");
            }

            resultSet = statement.executeQuery("SELECT * FROM dev1year.data_user WHERE user_id = '" + userId + "'");
            if (resultSet.next()) {
                return GeneralResponse.error(HttpStatus.CONFLICT.value(), "The user_id is already present in the user catalog.");
            } else {
                StringBuilder sb = new StringBuilder();
                sb.append("Insert into dev1year.data_user (user_id");
                userRequest.keySet().forEach(key -> sb.append(", ").append(key));
                sb.append(") values ('").append(userId);
                userRequest.keySet().forEach(key -> sb.append("', '").append(userRequest.get(key)).append("'"));
                sb.append(");");
                statement.executeQuery(sb.toString());
            }
        } catch (SQLException e) {
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
        return GeneralResponse.ok(HttpStatus.OK.value(), HttpStatusConstant.CREATE_SUCCESS);
    }

    @Override
    public GeneralResponse<?> updateUser(Map<String, Object> userRequest, String userId) {
        Connection connection = getConnection();
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            String sql = "Select * from properties where type_data = 'user'";
            statement = connection.createStatement();
            statement.executeQuery(sql);
            List<Map<String, Object>> properties = new ArrayList<>();
            while (statement.getResultSet().next()) {
                Map<String, Object> property = new HashMap<>();
                for (int i = 1; i <= statement.getResultSet().getMetaData().getColumnCount(); i++) {
                    String columnName = statement.getResultSet().getMetaData().getColumnName(i);
                    String columnValue = statement.getResultSet().getString(i);
                    property.put(columnName, columnValue);
                }
                properties.add(property);
            }

            if (userId == null || userId.isBlank()) {
                return GeneralResponse.error(HttpStatus.BAD_REQUEST.value(), "The user_id field is required");
            }

            List<String> propertyNames = new ArrayList<>();
            for (Map<String, Object> property : properties) {
                propertyNames.add(property.get("property_name").toString());
            }
            boolean exist = userRequest.keySet().stream().anyMatch(key -> propertyNames.contains(key));
            if (!exist) {
                return GeneralResponse.error(HttpStatus.BAD_REQUEST.value(), "Property of the given name is not present in the database.");
            }

            resultSet = statement.executeQuery("SELECT COUNT(*) FROM dev1year.data_user WHERE user_id = '" + userId + "'");
            resultSet.next();
            int count = resultSet.getInt(1);
            if (count < 1) {
                return GeneralResponse.error(HttpStatus.CONFLICT.value(), "User of the given user_id is not present in the user catalog.");
            } else {
                StringBuilder sb = new StringBuilder();
                sb.append("Update dev1year.data_user SET ");
                userRequest.forEach((key, value) -> sb.append(key).append(" = '").append(value).append("'").append(","));
                sb.deleteCharAt(sb.length() - 1);
                sb.append(" WHERE user_id = '").append(userId).append("';");
                statement.executeUpdate(sb.toString());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeConnection(connection, resultSet, statement);
        }
        return GeneralResponse.ok(HttpStatus.OK.value(), HttpStatusConstant.CREATE_SUCCESS);
    }

    @Override
    public GeneralResponse<?> getUser(String userId) {
        Connection connection = this.getConnection();
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            statement = connection.createStatement();
            // Kiểm tra xem user_id có match với regex hay không, nếu không thì trả về lỗi
            if (!userId.matches(DataUtils.REGEX)) {
                return GeneralResponse.error(HttpStatus.BAD_REQUEST.value(), "The user_id does not match ^[a-zA-Z0-9_-:@.]+$");
            }

            // Thực hiện truy vấn SQL
            resultSet = statement.executeQuery("SELECT * FROM data_user WHERE user_id = '" + userId + "'");
            if (!resultSet.next()) {
                return GeneralResponse.error(HttpStatus.NOT_FOUND.value(), "No user found with the given user_id.");
            } else {
                Map<String, Object> map = new HashMap<>();
                // Lặp qua từng cột và lấy thông tin
                for (int i = 1; i <= statement.getResultSet().getMetaData().getColumnCount(); i++) {
                    String columnName = statement.getResultSet().getMetaData().getColumnName(i);
                    Object columnValue = statement.getResultSet().getObject(i);
                    map.put(columnName, columnValue);
                }
                return GeneralResponse.ok(map, HttpStatusConstant.SUCCESS);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return GeneralResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Database error occurred.");
        } finally {
            // Đóng kết nối và statement
            try {
                if (resultSet != null) resultSet.close();
                if (statement != null) statement.close();
                if (connection != null) connection.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
//        return GeneralResponse.ok(HttpStatus.OK.value(), HttpStatusConstant.SUCCESS);;
    }

    @Override
    public GeneralResponse<?> bulkImportUser(List<Map<String, Object>> userRequest) {
        Connection connection = this.getConnection();
        Statement statement = null;
        ResultSet resultSet = null;
        int errorRecords = 0;
        if (userRequest.size() > 50000) {
            return GeneralResponse.error(HttpStatus.BAD_REQUEST.value(), "The request size exceeds the limit of 50000 records");
        }
        try {
            List<Map<String, Object>> properties = this.getProperties(connection);
            List<String> propertyNames = new ArrayList<>();
            properties.forEach(property -> propertyNames.add((String) property.get("property_name")));

            boolean errorUser = false;
            for (Map<String, Object> user : userRequest) {
                boolean notProperty = false;
                for (String key : user.keySet()) {
                    if (!propertyNames.contains(key)) {
                        notProperty = true;
                        break;
                    }
                }
                if (notProperty) {
                    errorUser = true;
                    break;
                }
            }
            if (errorUser) {
                return GeneralResponse.error(HttpStatus.BAD_REQUEST.value(), "Property of the given name is not present in the database.");
            }

            statement = connection.createStatement();
            resultSet = statement.executeQuery("SELECT * FROM dev1year.data_user");
            List<String> userIds = new ArrayList<>();
            while (resultSet.next()) {
                userIds.add(resultSet.getString("user_id"));
            }
            List<Map<String, Object>> listImport = new ArrayList<>();
            for (Map<String, Object> user : userRequest) {
                if (!user.containsKey("user_id") || user.get("user_id").toString().isBlank()) {
                    errorRecords++;
                    continue;
                }
                if (user.get("user_id").toString().length() > 128) {
                    errorRecords++;
                    continue;
                }
                String userId = (String) user.get("user_id");
                if (userIds.contains(userId)) {
                    errorRecords++;
                } else {
                    listImport.add(user);
                }
            }

            if (listImport.isEmpty()) {
                return GeneralResponse.error(HttpStatus.OK.value(), "All records are invalid");
            }

            StringBuilder sql = new StringBuilder();
            sql.append("Insert into dev1year.data_user (");
            propertyNames.forEach(property -> sql.append(property).append(", "));
            sql.deleteCharAt(sql.length() - 2);
            sql.append(") values ");
            listImport.forEach(user -> {
                sql.append("(");
                propertyNames.forEach(property -> {
                    if (user.containsKey(property)) {
                        sql.append("'").append(user.get(property)).append("', ");
                    } else {
                        sql.append("'', ");
                    }
                });
                sql.deleteCharAt(sql.length() - 2);
                sql.append("), ");
            });
            sql.deleteCharAt(sql.length() - 2);
            sql.append(";");
            statement.executeUpdate(sql.toString());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeConnection(connection, resultSet, statement);
        }
        return GeneralResponse.ok(HttpStatus.OK.value(),  String.format("Successfully imported %s records. Failed imported records might occur due to the item_id field is not present, or item_id exceeds the max length of 128 characters", userRequest.size() - errorRecords));
    }

    @Override
    public GeneralResponse<?> createUserProperty(Map<String, Object> request) {
        Connection connection = this.getConnection();
        Statement statement = null;
        ResultSet resultSet = null;
        String propertyName = request.get("property-name").toString();
        if (!request.containsKey("property-name") || propertyName.isBlank() || !propertyName.matches(DataUtils.REGEX) ||
                propertyName.length() > 50 || Arrays.asList("id", "user_id").contains(propertyName)) {
            return GeneralResponse.error(HttpStatus.BAD_REQUEST.value(), "Property name does not match ^[a-zA-Z_][0-9a-zA-Z_]*$, or it\n" +
                    "is a reserved keyword (‘’id’’, ‘’user_id’’), or its length exceeds 50 characters.\n" +
                    "Type information is missing, or the given type is invalid.");
        }
        try {
            List<Map<String, Object>> properties = this.getProperties(connection);
            List<String> propertyNames = new ArrayList<>();
            properties.forEach(property -> propertyNames.add((String) property.get("property_name")));

            if (propertyNames.contains(propertyName)) {
                return GeneralResponse.error(HttpStatus.CONFLICT.value(), "Property of the given name is already present in the database.");
            }
            // Kiểm tra xem type có hợp lệ hay không
            if (!DataType.validType(String.valueOf(request.get("type"))) || StringUtils.isBlank(String.valueOf(request.get("type")))) {
                return GeneralResponse.error(HttpStatus.BAD_REQUEST.value(), "Type information is missing, or the given type is invalid.");
            }

            String data_type = DataType.getValueOf(request.get("type").toString());

            statement = connection.createStatement();
            statement.executeUpdate(String.format("Insert into dev1year.properties (property_name, data_type, type_data) values ('%s', '%s', '%s')", propertyName, data_type, "user"));
            statement.executeUpdate(String.format("Alter table dev1year.data_user add column %s %s", propertyName, data_type));
            return GeneralResponse.ok(HttpStatus.CREATED.value(), HttpStatusConstant.CREATE_SUCCESS);
        } catch (Exception e) {
            e.printStackTrace();
            return GeneralResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Database error occurred.");
        } finally {
            closeConnection(connection, resultSet, statement);
        }
    }

    @Override
    public GeneralResponse<?> deleteUserProperty(String name) {
        Connection connection = this.getConnection();
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            List<Map<String, Object>> properties = this.getProperties(connection);
            List<String> propertyNames = new ArrayList<>();
            properties.forEach(property -> propertyNames.add((String) property.get("property_name")));

            if (!propertyNames.contains(name)) {
                return GeneralResponse.error(HttpStatus.BAD_REQUEST.value(), "Property of the given name is not present in the database.");
            }

            if (!name.matches("^[a-zA-Z0-9_\\-:]+$")) {
                return GeneralResponse.error(HttpStatus.BAD_REQUEST.value(), "Property name does not match ^[a-zA-Z0-9_-:]+$.");
            }
            statement = connection.createStatement();
            statement.executeUpdate(String.format("DELETE from dev1year.properties WHERE property_name = '%s'", name));
            statement.executeUpdate("ALTER TABLE dev1year.data_user DROP COLUMN " + name);
            return GeneralResponse.ok(HttpStatus.OK.value(), HttpStatusConstant.SUCCESS_MESSAGE);
        } catch (Exception e) {
            e.printStackTrace();
            return GeneralResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Database error occurred.");
        } finally {
            closeConnection(connection, resultSet, statement);
        }
    }

    @Override
    public GeneralResponse<?> getUserProperty(String name) {
        Connection connection = this.getConnection();
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            statement = connection.createStatement();
            resultSet = statement.executeQuery(String.format("SELECT * FROM dev1year.properties WHERE property_name = '%s'", name));
            Map<String, Object> map = new HashMap<>();
            if (resultSet.next()) {
                Map<String, String> property = new HashMap<>();
                property.put("property-name", resultSet.getString("property_name"));
                property.put("type", resultSet.getString("data_type"));
                return GeneralResponse.ok(HttpStatus.OK.value(), HttpStatusConstant.SUCCESS, property);
            } else {
                return GeneralResponse.ok(HttpStatus.OK.value(), HttpStatusConstant.SUCCESS, new HashMap<>());
            }
        } catch (Exception e) {
            e.printStackTrace();
            return GeneralResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Database error occurred.");
        } finally {
            closeConnection(connection, resultSet, statement);
        }
    }

    @Override
    public GeneralResponse<?> getListUserProperty() {
        Connection connection = this.getConnection();
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            statement = connection.createStatement();
            resultSet = statement.executeQuery("SELECT property_name, data_type FROM dev1year.properties WHERE type_data = 'user'");
            List<Map<String, String>> properties = new ArrayList<>();
            while (resultSet.next()) {
                Map<String, String> property = new HashMap<>();
                property.put("type", resultSet.getString("data_type"));
                property.put("property-name", resultSet.getString("property_name"));
                properties.add(property);
            }
            return GeneralResponse.ok(HttpStatus.OK.value(), HttpStatusConstant.SUCCESS, properties);
        } catch (Exception e) {
            e.printStackTrace();
            return GeneralResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Database error occurred.");
        } finally {
            closeConnection(connection, resultSet, statement);
        }
    }

    private List<Map<String, Object>> getProperties(Connection connection) {
        Statement statement = null;
        String sql = "Select * from properties where type_data = 'user'";
        List<Map<String, Object>> properties = null;
        try {
            statement = connection.createStatement();
            statement.executeQuery(sql);
            properties = new ArrayList<>();
            while (statement.getResultSet().next()) {
                Map<String, Object> property = new HashMap<>();
                for (int i = 1; i <= statement.getResultSet().getMetaData().getColumnCount(); i++) {
                    String columnName = statement.getResultSet().getMetaData().getColumnName(i);
                    String columnValue = statement.getResultSet().getString(i);
                    property.put(columnName, columnValue);
                }
                properties.add(property);
            }
        } catch (SQLException e) {
            e.printStackTrace();
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
