package com.sapo.mock.techshop.service.impl;

import com.sapo.mock.techshop.Utils.DataUtils;
import com.sapo.mock.techshop.common.constant.Constant;
import com.sapo.mock.techshop.common.constant.DataType;
import com.sapo.mock.techshop.common.constant.HttpStatusConstant;
import com.sapo.mock.techshop.dto.response.GeneralResponse;
import com.sapo.mock.techshop.service.ConnectionService;
import com.sapo.mock.techshop.service.DataUserService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.sql.*;
import java.util.*;
import java.util.Date;
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

            if (Objects.isNull(dataUserRequest.get(Constant.USER_ID)) || StringUtils.isBlank((String) dataUserRequest.get(Constant.USER_ID))) {
                return GeneralResponse.error(HttpStatus.BAD_REQUEST.value(), HttpStatusConstant.INVALID_USER_ID);
            }

            if (((String) dataUserRequest.get(Constant.USER_ID)).length() > 128) {
                return GeneralResponse.error(HttpStatus.BAD_REQUEST.value(), "The user_id exceeds the max length of 128 characters");
            }
            List<Map<String, Object>> properties = this.getAllProperties(connection);

            String userId = (String) dataUserRequest.get(Constant.USER_ID);

            List<String> proDb = properties.stream().map(property -> (String) property.get(Constant.PROPERTY_NAME)).collect(Collectors.toList());
            Map<String, String> mapPropertyType = properties.stream().collect(Collectors.toMap(property -> (String) property.get(Constant.PROPERTY_NAME), property -> (String) property.get("data_type")));
            boolean notInDb = dataUserRequest.keySet().stream().anyMatch(key -> !proDb.contains(key));

            if (notInDb) {
                return GeneralResponse.error(HttpStatus.BAD_REQUEST.value(), HttpStatusConstant.PROPERTY_NOT_IN_DATABASE);
            }

            statement = connection.createStatement();
            resultSet = statement.executeQuery("SELECT * FROM data_user WHERE user_id = '" + userId + "'");
            if (resultSet.next()) {
                return GeneralResponse.error(HttpStatus.BAD_REQUEST.value(), "The user_id is already present in the item catalog.");
            } else {
                StringBuilder sql = new StringBuilder("INSERT INTO data_user ( ");
                dataUserRequest.keySet().forEach(key -> sql.append(key).append(", "));
                sql.deleteCharAt(sql.lastIndexOf(","));
                sql.append(") VALUES (");
                dataUserRequest.forEach((key, value) -> sql.append(DataUtils.getValue(value, mapPropertyType.get(key))).append(", "));
                sql.deleteCharAt(sql.lastIndexOf(","));
                sql.append(");");
                statement.executeUpdate(sql.toString());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            handleConnection(connection, resultSet, statement);

        }
        return GeneralResponse.error(HttpStatus.CREATED.value(), HttpStatusConstant.CREATE_SUCCESS_MESSAGE);
    }

    private void handleConnection(Connection connection, ResultSet resultSet, Statement statement) {
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
                return GeneralResponse.error(HttpStatus.BAD_REQUEST.value(), HttpStatusConstant.INVALID_USER_ID);
            }

            if (!id.matches(DataUtils.REGEX)) {
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
            handleConnection(connection, resultSet, statement);
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
                return GeneralResponse.error(HttpStatus.BAD_REQUEST.value(), HttpStatusConstant.INVALID_USER_ID);
            }
            List<Map<String, Object>> properties = this.getAllProperties(connection);
            Map<String, String> mapPropertyType = properties.stream().collect(Collectors.toMap(property -> (String) property.get(Constant.PROPERTY_NAME), property -> (String) property.get("data_type")));

            List<String> proDb = properties.stream().map(property -> (String) property.get(Constant.PROPERTY_NAME)).collect(Collectors.toList());
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
                request.forEach((key, value) -> sql.append(key).append(" = ").append(DataUtils.getValue(value, mapPropertyType.get(key))).append(", "));
                sql.deleteCharAt(sql.lastIndexOf(","));
                sql.append(" WHERE user_id = '").append(id).append("';");
                statement.executeUpdate(sql.toString());
                return GeneralResponse.ok();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return GeneralResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), HttpStatusConstant.SQL_ERROR_CODE);
        } finally {
            handleConnection(connection, resultSet, statement);
        }
    }

    /**
     * Creates a new data user in the database.
     *
     * @param request a list of map containing the properties of the data user to be created
     * @return a general response indicating the status of the operation and any errors that may have occurred
     */
    @Override
    public GeneralResponse<?> importUser(List<Map<String, Object>> request) {
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
            List<String> proDb = properties.stream().map(property -> (String) property.get(Constant.PROPERTY_NAME)).collect(Collectors.toList());

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
                idsDb.add(resultSet.getString(Constant.USER_ID));
            }
            List<Map<String, Object>> entities = new ArrayList<>();
            for (Map<String, Object> user : request) {
                if (!user.containsKey(Constant.USER_ID) || ((String) user.get(Constant.USER_ID)).length() > 128 ||
                        StringUtils.isBlank((String) user.get(Constant.USER_ID)) || idsDb.contains((String) user.get(Constant.USER_ID))) {
                    countError++;
                    continue;
                }
                entities.add(user);
            }
            if (entities.isEmpty()) {
                return GeneralResponse.ok(HttpStatus.OK.value(), String.format(HttpStatusConstant.BULK_INSERT, reqSize - countError));
            }
            return this.buildQuery(proDb, entities, statement, connection, reqSize, countError);
        } catch (SQLException e) {
            e.printStackTrace();
            return GeneralResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), HttpStatusConstant.SQL_ERROR_CODE);
        } finally {
            handleConnection(connection, resultSet, statement);
        }
    }

    private GeneralResponse buildQuery(List<String> proDb, List<Map<String, Object>> entities, Statement statement, Connection connection, int reqSize, int countError) {
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
        } catch (SQLException e) {
            return GeneralResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), HttpStatusConstant.SQL_ERROR_CODE);
        }
        return GeneralResponse.ok(HttpStatus.OK.value(), String.format(HttpStatusConstant.BULK_INSERT, reqSize - countError));
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
            if (!request.containsKey(Constant.PROPERTY_NAME_KEY) || StringUtils.isBlank((String) request.get(Constant.PROPERTY_NAME_KEY))
                    || !((String) request.get(Constant.PROPERTY_NAME_KEY)).matches(DataUtils.PROPERTY_NAME_REGEX) ||
                    ((String) request.get(Constant.PROPERTY_NAME_KEY)).length() > 50 || this.sensitiveKey().contains(((String) request.get(Constant.PROPERTY_NAME_KEY)))) {
                return GeneralResponse.error(HttpStatus.BAD_REQUEST.value(), HttpStatusConstant.INVALID_PROPERTY_USER);
            }
            List<Map<String, Object>> properties = this.getAllProperties(connection);
            List<String> proDb = properties.stream().map(property -> (String) property.get(Constant.PROPERTY_NAME)).collect(Collectors.toList());

            if (proDb.contains(request.get(Constant.PROPERTY_NAME_KEY))) {
                return GeneralResponse.error(HttpStatus.CONFLICT.value(), HttpStatusConstant.CONFLICT_PROPERTY_USER);
            }

            if (!DataType.validType((String) request.get("type")) || StringUtils.isBlank((String) request.get("type"))) {
                return GeneralResponse.error(HttpStatus.BAD_REQUEST.value(), HttpStatusConstant.TYPE_NOT_DEFINE);
            }

            String type = DataType.getValueOf((String) request.get("type"));

            statement = connection.createStatement();
            statement.executeUpdate(String.format("ALTER TABLE data_user ADD COLUMN %s %s", request.get(Constant.PROPERTY_NAME_KEY), type));
            statement.executeUpdate(String.format("INSERT INTO properties (property_name, data_type, type_data) VALUES ('%s', '%s', '%s')", request.get(Constant.PROPERTY_NAME_KEY), type, "user"));
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
            if (StringUtils.isBlank(name) || !name.matches(DataUtils.PROPERTY_NAME_REGEX)) {
                return GeneralResponse.error(HttpStatus.BAD_REQUEST.value(), "Property name does not match ^[a-zA-Z0-9_-:]+$.");
            }
            List<Map<String, Object>> properties = this.getAllProperties(connection);
            List<String> proDb = properties.stream().map(property -> (String) property.get(Constant.PROPERTY_NAME)).collect(Collectors.toList());

            if (!proDb.contains(name)) {
                return GeneralResponse.error(HttpStatus.BAD_REQUEST.value(), HttpStatusConstant.PROPERTY_NOT_IN_DATABASE);
            }

            statement = connection.createStatement();
            statement.executeUpdate(String.format("DELETE FROM properties WHERE property_name = '%s' AND type_data ='user'", name));
            statement.executeUpdate(String.format("ALTER TABLE data_user DROP COLUMN %s", name));
            return GeneralResponse.ok(HttpStatus.OK.value(), HttpStatusConstant.SUCCESS_MESSAGE);
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
            if (StringUtils.isBlank(propertyName) || !propertyName.matches(DataUtils.PROPERTY_NAME_REGEX)) {
                return GeneralResponse.error(HttpStatus.BAD_REQUEST.value(), "Property name does not match ^[a-zA-Z0-9_-:]+$.");
            }
            List<Map<String, Object>> properties = this.getAllProperties(connection);
            List<String> proDb = properties.stream().map(property -> (String) property.get(Constant.PROPERTY_NAME)).collect(Collectors.toList());

            if (!proDb.contains(propertyName)) {
                return GeneralResponse.error(HttpStatus.BAD_REQUEST.value(), HttpStatusConstant.PROPERTY_NOT_IN_DATABASE);
            }

            statement = connection.createStatement();
            resultSet = statement.executeQuery(String.format("SELECT * FROM properties WHERE property_name = '%s' AND type_data ='user'", propertyName));
            Map<String, Object> property = new HashMap<>();
            if (resultSet.next()) {
                property.put(Constant.PROPERTY_NAME, resultSet.getString(Constant.PROPERTY_NAME));
                property.put("data_type", DataType.getKeyOf(resultSet.getString("data_type")));
            }
            return GeneralResponse.ok(property);
        } catch (SQLException e) {
            e.printStackTrace();
            return GeneralResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), HttpStatusConstant.SQL_ERROR_CODE);
        } finally {
            handleConnection(connection, resultSet, statement);
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
                obj.put(Constant.PROPERTY_NAME_KEY, resultSet.getObject("propertyName"));
                obj.put("data_type", DataType.getKeyOf(resultSet.getString("type")));
                objs.add(obj);
            }
            return GeneralResponse.ok(HttpStatus.OK.value(), HttpStatusConstant.SUCCESS_MESSAGE, objs);
        } catch (SQLException e) {
            e.printStackTrace();
            return GeneralResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), HttpStatusConstant.SQL_ERROR_CODE);
        } finally {
            handleConnection(connection, resultSet, statement);
        }
    }

    private List<String> sensitiveKey() {
        return List.of("id", Constant.USER_ID);
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
            throw new InternalError(e);
        } finally {
            try {
                if (Objects.nonNull(statement)) {
                    statement.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return properties;
    }

    public static void main(String[] args) {
        Map<String, String> xxx =new HashMap<>();

        xxx.put("basic_type", "basic_type");
        xxx.put("description", "description");
        xxx.put("created_date", "2023-12-25 17:10:39");
        xxx.put("long_time", "1704868132864");
        xxx.put("salary", "1708132864");
        xxx.put("year", "2023");
        xxx.put("address", "No 60 Hoang Quoc Viet, Nghia Do, Cau Giay");
        xxx.put("weight", "4566");
        xxx.put("temp", "37.5");
        xxx.put("expression", "jfkjfkjsfkjwieojfoiwejfksjfk");
        xxx.put("updated_date", "2023-12-25 17:10:39");
        xxx.put("status", "1");
        xxx.put("total", "10.25");
        xxx.put("field_type", "N");
        xxx.put("date_of_birth", "2010-01-12");
        xxx.put("is_good", "2010-01-12");

        String aa = "varchar, longtext, bigint, datetime, int. decimal, tinyint, blob, smallint, double, text, timestamp, bit, binary, float, char, date";

//        System.out.println(xxx.values().stream().map(i -> "\"" + i + "\"").collect(Collectors.toList()));
//        System.out.println(new ArrayList<>(xxx.keySet()));
        System.out.println(new Date());

        String string = "[1, 2.3, 'abc']";

        String[] elements = string.split(",");
//        String[] elements = string.replace("[", "").replace("]", "").split(",");

        System.out.println(elements);

//        List<String> fields = new ArrayList<>(List.of("basic_type",
//                "description", "created_date",
//                "long_time", "salary",
//                "sex", "address",
//                "weight", "temp",
//                "expression", "updated_date",
//                "status", "total",
//                "field_type", "date_of_birth"));
//        String primaryKey = "field2";
//        int primaryPos = fields.lastIndexOf(primaryKey);
//        Map<String, String> mapNameType = Map.of("field1", "TEXT", "field2", "String", "field3", "double", "field4", "time");
//
//        List<String> values = new ArrayList<>();
//        for (int i = 0; i < 20; i++) {
//            values.add("value" + i);
//        }
//
//        Map<Integer, String> mapPosType = new HashMap<>();
//        for (int i = 0; i < fields.size(); i++) {
//            mapPosType.put(i, mapNameType.get(fields.get(i)));
//        }
//
//        int totalSize = values.size();
//        int splitSize = fields.size();
//
//        List<String> keys = new ArrayList<>();
//        for (int i = primaryPos; i < totalSize; i += splitSize) {
//            keys.add(getValue(values.get(i), mapPosType.get(primaryPos)));
//        }
////        System.out.println(keys);
//
//
//
//        String result = keys.stream()
//                .collect(Collectors.joining(", ", "(", ")"));
//
//        String builder = "SELECT tbl." + primaryKey + " FROM " + " table_name tbl WHERE " +
//                " tbl." + primaryKey + " IN " + result;
//
//        List<String> bbb = new ArrayList<>(List.of("value9"));
//        System.out.println(haveCommonElementWithSet(values, bbb));

//        for (String value : values) {
//            System.out.print("\"" + value + "\",");
//        }
    }

    public static String getValue(String value, String type) {
        String valueString;
        switch (type) {
            case "TEXT":
            case "String":
                valueString = "'" + value + "'";
                break;
            default:
                valueString = String.valueOf(value);
                break;
        }
        return valueString;
    }

    public static boolean haveCommonElementWithSet(List<String> list1, List<String> list2) {
        Set<String> set = new HashSet<>(list1);
        for (String item : list2) {
            if (set.contains(item)) {
                return true;
            }
        }
        return false;
    }

}
