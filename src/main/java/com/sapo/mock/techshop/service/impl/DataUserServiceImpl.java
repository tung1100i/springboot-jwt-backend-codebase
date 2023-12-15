package com.sapo.mock.techshop.service.impl;

import com.sapo.mock.techshop.common.constant.DataType;
import com.sapo.mock.techshop.common.constant.HttpStatusConstant;
import com.sapo.mock.techshop.dto.response.GeneralResponse;
import com.sapo.mock.techshop.service.ConnectionService;
import com.sapo.mock.techshop.service.DataUserService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class DataUserServiceImpl implements DataUserService {
    private final ConnectionService connectionService;

    /**
     * Creates a new data user in the database.
     *
     * @param dataUserRequest a map containing the properties of the data user to be created
     * @return a general response indicating the status of the operation and any errors that may have occurred
     */
    @Override
    public GeneralResponse<?> createDataUser(Map<String, Object> dataUserRequest) {
        Connection connection = connectionService.getConnection();
        ResultSet resultSet = null;
        Statement statement = null;
        try {

            if (Objects.isNull(dataUserRequest.get("user_id")) || StringUtils.isBlank((String) dataUserRequest.get("user_id"))) {
                return GeneralResponse.error(HttpStatus.BAD_REQUEST.value(), "The user_id field is required");
            }

            if (((String) dataUserRequest.get("user_id")).length() > 128) {
                return GeneralResponse.error(HttpStatus.BAD_REQUEST.value(), "The user_id exceeds the max length of 128 characters");
            }
            List<Map<String, Object>> properties = this.getAllProperties(connection);

            String user_id = (String) dataUserRequest.get("user_id");
            dataUserRequest.remove("user_id");

            List<String> proDb = properties.stream().map(property -> (String) property.get("property_name")).collect(Collectors.toList());
            boolean notInDb = dataUserRequest.keySet().stream().anyMatch(key -> !proDb.contains(key));

            if (notInDb) {
                return GeneralResponse.error(HttpStatus.BAD_REQUEST.value(), HttpStatusConstant.PROPERTY_NOT_IN_DATABASE);
            }

            statement = connection.createStatement();
            resultSet = statement.executeQuery("SELECT * FROM data_user WHERE user_id = '" + user_id + "'");
            if (resultSet.next()) {
                return GeneralResponse.error(HttpStatus.BAD_REQUEST.value(), "The user_id is already present in the item catalog.");
            } else {
                StringBuilder sql = new StringBuilder("INSERT INTO data_user (user_id, ");
                dataUserRequest.keySet().forEach(key -> sql.append(key).append(", "));
                sql.deleteCharAt(sql.lastIndexOf(","));
                sql.append(") VALUES (").append("'").append(user_id).append("'");
                dataUserRequest.forEach((key, value) -> sql.append(", ").append("'").append(value).append("'"));
                sql.append(");");
                statement.executeUpdate(sql.toString());
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
        return GeneralResponse.error(HttpStatus.CREATED.value(), HttpStatusConstant.CREATE_SUCCESS_MESSAGE);
    }


    /**
     * Retrieves the details of a data user from the database.
     *
     * @param id the unique identifier of the data user
     * @return a general response containing the data user details or an error if the data user could not be found
     */
    @Override
    public GeneralResponse<?> getUser(String id) {

        Connection connection = connectionService.getConnection();
        ResultSet resultSet = null;
        Statement statement = null;
        try {

            if (Objects.isNull(id) || StringUtils.isBlank(id)) {
                return GeneralResponse.error(HttpStatus.BAD_REQUEST.value(), "The user_id field is required");
            }

            if (!id.matches("^[a-zA-Z0-9_\\-:@.]+$")) {
                return GeneralResponse.error(HttpStatus.BAD_REQUEST.value(), "The user_id does not match ^[a-zA-Z0-9_-:@.]+$");
            }

            statement = connection.createStatement();
            resultSet = statement.executeQuery("SELECT * FROM data_user WHERE user_id = '" + id + "'");
            int countColum = resultSet.getMetaData().getColumnCount();
            if (!resultSet.next()) {
                return GeneralResponse.error(HttpStatus.BAD_REQUEST.value(), "User of the given user_id is not present in the catalog.");
            } else {
                Map<String, Object> property = new HashMap<>();
                for (int i = 1; i <= countColum; i++) {
                    property.put(resultSet.getMetaData().getColumnName(i), resultSet.getObject(i));
                }
                return GeneralResponse.ok(property);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return GeneralResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), HttpStatusConstant.SQL_ERROR_CODE);
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
    }


    /**
     * Updates the properties of a data user in the database.
     *
     * @param request a map containing the properties of the data user to be updated
     * @param id      the unique identifier of the data user
     * @return a general response indicating the status of the operation and any errors that may have occurred
     */
    @Override
    public GeneralResponse<?> updateUser(Map<String, Object> request, String id) {
        Connection connection = connectionService.getConnection();
        ResultSet resultSet = null;
        Statement statement = null;
        try {

            if (Objects.isNull(id) || StringUtils.isBlank(id)) {
                return GeneralResponse.error(HttpStatus.BAD_REQUEST.value(), "The user_id field is required");
            }
            List<Map<String, Object>> properties = this.getAllProperties(connection);

            List<String> proDb = properties.stream().map(property -> (String) property.get("property_name")).collect(Collectors.toList());
            boolean notInDb = request.keySet().stream().anyMatch(key -> !proDb.contains(key));

            if (notInDb) {
                return GeneralResponse.error(HttpStatus.BAD_REQUEST.value(), HttpStatusConstant.PROPERTY_NOT_IN_DATABASE);
            }
            statement = connection.createStatement();
            resultSet = statement.executeQuery("SELECT COUNT(*) FROM data_user WHERE user_id = '" + id + "'");
            resultSet.next();
            if (resultSet.getInt(1) < 1) {
                return GeneralResponse.error(HttpStatus.BAD_REQUEST.value(), "User of the given user_id is not present in the catalog.");
            } else {
                StringBuilder sql = new StringBuilder("UPDATE data_user SET ");
                request.forEach((key, value) -> sql.append(key).append(" = ").append("'").append(value).append("'").append(", "));
                sql.deleteCharAt(sql.lastIndexOf(","));
                sql.append(" WHERE user_id = '").append(id).append("';");
                statement.executeUpdate(sql.toString());
                return GeneralResponse.ok(HttpStatusConstant.SUCCESS_MESSAGE);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return GeneralResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), HttpStatusConstant.SQL_ERROR_CODE);
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
    }

    /**
     * Creates a new data user in the database.
     *
     * @param request a list of map containing the properties of the data user to be created
     * @return a general response indicating the status of the operation and any errors that may have occurred
     */
    @Override
    public GeneralResponse<?> bulkInsert(List<Map<String, Object>> request) {
        if (request.size() > 50000) {
            return GeneralResponse.error(HttpStatus.BAD_REQUEST.value(), HttpStatusConstant.OUT_RANGE_SIZE);
        }
        int reqSize = request.size();
        Connection connection = connectionService.getConnection();
        ResultSet resultSet = null;
        Statement statement = null;
        int countError = 0;
        try {
            List<Map<String, Object>> properties = this.getAllProperties(connection);
            List<String> proDb = properties.stream().map(property -> (String) property.get("property_name")).collect(Collectors.toList());

            boolean badReq = false;

            for (Map<String, Object> user : request) {
                boolean nonProp = false;
                for (String key : user.keySet()) {
                    if (!proDb.contains(key)) {
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
                return GeneralResponse.error(HttpStatus.BAD_REQUEST.value(), HttpStatusConstant.PROPERTY_NOT_IN_DATABASE);
            }

            statement = connection.createStatement();
            resultSet = statement.executeQuery("Select * from data_user");
            List<String> idsDb = new ArrayList<>();
            while (resultSet.next()) {
                idsDb.add(resultSet.getString("user_id"));
            }
            List<Map<String, Object>> entities = new ArrayList<>();
            for (Map<String, Object> user : request) {
                if (!user.containsKey("user_id")) {
                    countError++;
                    continue;
                }
                String id = (String) user.get("user_id");
                if (id.length() > 128 || StringUtils.isBlank(id) || idsDb.contains(id)) {
                    countError++;
                    continue;
                }
                entities.add(user);
            }
            if (entities.isEmpty()) {
                return GeneralResponse.ok(HttpStatus.OK.value(), String.format(HttpStatusConstant.BULK_INSERT, reqSize - countError));
            }

            try {
                StringBuilder sql = new StringBuilder();
                sql.append("INSERT INTO data_user (");
                proDb.forEach(prop -> sql.append(prop).append(", "));
                sql.deleteCharAt(sql.lastIndexOf(","));
                sql.append(") VALUES ");
                entities.forEach(user -> {
                    sql.append("( ");
                    proDb.forEach(prop -> {
                        if (user.containsKey(prop) || Objects.nonNull(user.get(prop))) {
                            sql.append("'").append(user.get(prop)).append("'").append(",");
                        } else {
                            sql.append("null").append(",");
                        }
                    });
                    sql.deleteCharAt(sql.lastIndexOf(","));
                    sql.append("),");
                });
                sql.deleteCharAt(sql.lastIndexOf(","));
                statement.executeUpdate(sql.toString());
                connection.commit();
                return GeneralResponse.ok(HttpStatus.OK.value(), String.format(HttpStatusConstant.BULK_INSERT, reqSize - countError));
            } catch (SQLException e) {
                return GeneralResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), HttpStatusConstant.SQL_ERROR_CODE);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return GeneralResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), HttpStatusConstant.SQL_ERROR_CODE);
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
    }

    /**
     * Creates a new user property in the database.
     *
     * @param request a map containing the properties of the user property to be created
     * @return a general response indicating the status of the operation and any errors that may have occurred
     */
    @Override
    public GeneralResponse<?> createUserProperty(Map<String, Object> request) {
        Connection connection = connectionService.getConnection();
        Statement statement = null;
        try {
            if (!request.containsKey("property-name") || StringUtils.isBlank((String) request.get("property-name"))
                    || !((String) request.get("property-name")).matches("^[a-zA-Z_][0-9a-zA-Z_]*$") ||
                    ((String) request.get("property-name")).length() > 50 || Arrays.asList("id", "user_id").contains(((String) request.get("property-name")))) {
                return GeneralResponse.error(HttpStatus.BAD_REQUEST.value(), HttpStatusConstant.INVALID_PROPERTY_USER);
            }
            List<Map<String, Object>> properties = this.getAllProperties(connection);
            List<String> proDb = properties.stream().map(property -> (String) property.get("property_name")).collect(Collectors.toList());

            if (proDb.contains((String) request.get("property-name"))) {
                return GeneralResponse.error(HttpStatus.CONFLICT.value(), HttpStatusConstant.CONFLICT_PROPERTY_USER);
            }

            if (!DataType.validType((String) request.get("type")) || StringUtils.isBlank((String) request.get("type"))) {
                return GeneralResponse.error(HttpStatus.BAD_REQUEST.value(), HttpStatusConstant.TYPE_NOT_DEFINE);
            }

            String type = DataType.getValueOf((String) request.get("type"));

            statement = connection.createStatement();
            statement.executeUpdate(String.format("ALTER TABLE data_user ADD COLUMN %s %s", request.get("property-name"), type));
            statement.executeUpdate(String.format("INSERT INTO properties (property_name, data_type, type_data) VALUES ('%s', '%s', '%s')", request.get("property-name"), type, "user"));
            return GeneralResponse.ok(HttpStatus.CREATED.value(), HttpStatusConstant.SUCCESS_MESSAGE);
        } catch (SQLException e) {
            e.printStackTrace();
            return GeneralResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), HttpStatusConstant.SQL_ERROR_CODE);
        } finally {
            try {
                if (Objects.nonNull(connection)) {
                    connection.close();
                }
                if (Objects.nonNull(statement)) {
                    statement.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Deletes a user property from the database.
     *
     * @param name the name of the property to be deleted
     * @return a general response indicating the status of the operation and any errors that may have occurred
     */
    @Override
    public GeneralResponse<?> deleteUserProperty(String name) {
        Connection connection = connectionService.getConnection();
        Statement statement = null;
        try {
            if (StringUtils.isBlank(name) || !name.matches("^[a-zA-Z_][0-9a-zA-Z_]*$")) {
                return GeneralResponse.error(HttpStatus.BAD_REQUEST.value(), "Property name does not match ^[a-zA-Z0-9_-:]+$.");
            }
            List<Map<String, Object>> properties = this.getAllProperties(connection);
            List<String> proDb = properties.stream().map(property -> (String) property.get("property_name")).collect(Collectors.toList());

            if (!proDb.contains(name)) {
                return GeneralResponse.error(HttpStatus.BAD_REQUEST.value(), HttpStatusConstant.PROPERTY_NOT_IN_DATABASE);
            }

            statement = connection.createStatement();
            statement.executeUpdate(String.format("DELETE FROM properties WHERE property_name = '%s' AND type_data ='user'", name));
            statement.executeUpdate(String.format("ALTER TABLE data_user DROP COLUMN %s", name));
            return GeneralResponse.ok(HttpStatus.CREATED.value(), HttpStatusConstant.SUCCESS_MESSAGE);
        } catch (SQLException e) {
            e.printStackTrace();
            return GeneralResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), HttpStatusConstant.SQL_ERROR_CODE);
        } finally {
            try {
                if (Objects.nonNull(connection)) {
                    connection.close();
                }
                if (Objects.nonNull(statement)) {
                    statement.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * Retrieves the details of a user property from the database.
     *
     * @param propertyName the name of the property
     * @return a general response containing the user property details or an error if the user property could not be found
     */
    @Override
    public GeneralResponse<?> getUserProperty(String propertyName) {
        Connection connection = connectionService.getConnection();
        ResultSet resultSet = null;
        Statement statement = null;
        try {
            if (StringUtils.isBlank(propertyName) || !propertyName.matches("^[a-zA-Z_][0-9a-zA-Z_]*$")) {
                return GeneralResponse.error(HttpStatus.BAD_REQUEST.value(), "Property name does not match ^[a-zA-Z0-9_-:]+$.");
            }
            List<Map<String, Object>> properties = this.getAllProperties(connection);
            List<String> proDb = properties.stream().map(property -> (String) property.get("property_name")).collect(Collectors.toList());

            if (!proDb.contains(propertyName)) {
                return GeneralResponse.error(HttpStatus.BAD_REQUEST.value(), HttpStatusConstant.PROPERTY_NOT_IN_DATABASE);
            }

            statement = connection.createStatement();
            resultSet = statement.executeQuery(String.format("SELECT * FROM properties WHERE property_name = '%s' AND type_data ='user'", propertyName));
            Map<String, Object> property = new HashMap<>();
            if (resultSet.next()) {
                property.put("property_name", resultSet.getString("property_name"));
                property.put("data_type", resultSet.getString("data_type"));
            }
            return GeneralResponse.ok(property);
        } catch (SQLException e) {
            e.printStackTrace();
            return GeneralResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), HttpStatusConstant.SQL_ERROR_CODE);
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
    }

    /**
     * Retrieves the details of a specific properties from the database.
     *
     * @return a general response containing the specific properties details or an error if the specific properties could not be found
     */
    @Override
    public GeneralResponse<?> getSpecificProperties() {
        String sql = "SELECT property_name as propertyName, data_type as type FROM properties WHERE type_data = 'user'";
        Connection connection = connectionService.getConnection();
        ResultSet resultSet = null;
        Statement statement = null;
        try {
            statement = connection.createStatement();
            resultSet = statement.executeQuery(sql);
            List<Map<String, Object>> objs = new ArrayList<>();
            while (resultSet.next()) {
                Map<String, Object> obj = new HashMap<>();
                obj.put("property-name", resultSet.getObject("propertyName"));
                obj.put("data_type", resultSet.getObject("type"));
                objs.add(obj);
            }
            return GeneralResponse.ok(HttpStatus.INTERNAL_SERVER_ERROR.value(), HttpStatusConstant.SQL_ERROR_CODE, objs);
        } catch (SQLException e) {
            e.printStackTrace();
            return GeneralResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), HttpStatusConstant.SQL_ERROR_CODE);
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
    }

    private List<Map<String, Object>> getAllProperties(Connection connection) {
        String sql = "SELECT * FROM properties where type_data = 'user'";
        Statement statement = null;
        List<Map<String, Object>> properties = new ArrayList<>();
        try {
            statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            int countColum = resultSet.getMetaData().getColumnCount();
            while (resultSet.next()) {
                Map<String, Object> property = new HashMap<>();
                for (int i = 1; i <= countColum; i++) {
                    property.put(resultSet.getMetaData().getColumnName(i), resultSet.getObject(i));
                }
                properties.add(property);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return properties;
    }

}
