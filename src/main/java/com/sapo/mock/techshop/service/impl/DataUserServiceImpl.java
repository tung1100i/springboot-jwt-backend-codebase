package com.sapo.mock.techshop.service.impl;

import com.sapo.mock.techshop.common.constant.HttpStatusConstant;
import com.sapo.mock.techshop.dto.response.GeneralResponse;
import com.sapo.mock.techshop.service.ConnectionService;
import com.sapo.mock.techshop.service.DataUserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;


@Service
public class DataUserServiceImpl implements DataUserService {

    @Autowired
    private ConnectionService connectionService;

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


    private List<Map<String, Object>> getAllProperties(Connection connection) {
        String sql = "SELECT * FROM properties where type_data = 'user'";
        PreparedStatement statement = null;
        List<Map<String, Object>> properties = new ArrayList<>();
        try {
            statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();
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
