package com.sapo.mock.techshop.service.impl;

import com.sapo.mock.techshop.common.constant.HttpStatusConstant;
import com.sapo.mock.techshop.dto.response.GeneralResponse;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.sql.*;
import java.util.*;

import static junit.framework.TestCase.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class DataUserServiceImplTest {

    @Mock
    private Connection connection;

    @Mock
    private Statement statement;
    @Mock
    private ResultSet resultSet;
    @Mock
    private ConnectionServiceImpl connectionService;

    private DataUserServiceImpl dataUserService;


    Map<String, Object> request = new HashMap<>();

    @BeforeClass
    public static void beforeClass(){

        Connection connection = null;
        Statement s = null;
        PreparedStatement ps = null;
        ResultSet r = null;
        try {
            connection = DriverManager.getConnection("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;DATABASE_TO_LOWER=TRUE", "sa", "");
            s = connection.createStatement();
            s.execute("" +
                    "DROP TABLE IF EXISTS properties;" +
                    "DROP TABLE IF EXISTS data_user;" +
                    "DROP TABLE IF EXISTS data_item;" +
                    "CREATE TABLE properties (\n" +
                    "property_id int AUTO_INCREMENT NOT NULL,\n" +
                    "property_name varchar(255) NOT NULL,\n" +
                    "data_type varchar(255) NOT NULL,\n" +
                    "type_data varchar(255) NOT NULL,\n" +
                    "last_modified_time timestamp(0) NULL,\n" +
                    "create_time timestamp NULL DEFAULT CURRENT_TIMESTAMP,\n" +
                    "CONSTRAINT property_pkey PRIMARY KEY (property_id)\n" +
                    ");\n" +
                    "INSERT INTO properties (property_id,property_name,data_type,type_data) VALUES\n" +
                    " (1,'item_id','text','item'),\n" +
                    " (2,'last_update','timestamp','item'),\n" +
                    " (3,'user_id','text','user'),\n" +
                    " (4,'last_update','timestamp','user');\n" +
                    "\n" +
                    "CREATE TABLE data_user\n" +
                    "(\n" +
                    " user_id varchar(255) not NULL,\n" +
                    " last_update timestamp(0) null,\n" +
                    " constraint data_user_pkey PRIMARY KEY (user_id)\n" +
                    ");\n" +
                    "CREATE TABLE data_item\n" +
                    "(\n" +
                    " item_id varchar(255) not NULL,\n" +
                    " last_update timestamp(0) null,\n" +
                    " constraint data_item_pkey PRIMARY KEY (item_id)\n" +
                    ");" +
                    "INSERT INTO data_user (user_id, last_update) VALUES" +
                    "('user1234', '2009-11-12 17:49:30.000');");
            ps = connection.prepareStatement("select * from properties");
            r = ps.executeQuery();
            if (r.next()) {
                System.out.println("data?");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (Objects.nonNull(r)) {
                    r.close();
                }
                if (Objects.nonNull(ps)) {
                    ps.close();
                }
                if (Objects.nonNull(s)) {
                    s.close();
                }
                if (Objects.nonNull(connection)) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @Before
    public void setUp() throws SQLException {
        statement = Mockito.mock(Statement.class);
        resultSet = Mockito.mock(ResultSet.class);
        connectionService = Mockito.mock(ConnectionServiceImpl.class);
        connection = DriverManager.getConnection("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;DATABASE_TO_LOWER=TRUE", "sa", "");
        Mockito.lenient().when(connectionService.getConnection()).thenReturn(connection);
        dataUserService = new DataUserServiceImpl(connectionService);
        request.put("user_id", "user1234");
        request.put("last_update", "2018-11-12 17:49:30.000");
    }

    @Test
    @Order(1)
    public void testCreateDataUserSuccess() throws SQLException {
        // Thiết lập dto
        // Khi getConnection() được gọi, trả về một connection mock
        Map<String, Object> dto = new HashMap<>();
        dto.put("user_id", "user123456");
        /////
        Mockito.when(resultSet.next()).thenReturn(true);
        Mockito.when(resultSet.getInt(1)).thenReturn(1);
        Mockito.when(resultSet.getString(2)).thenReturn("image");
        Mockito.when(resultSet.getString(3)).thenReturn("text");
        Mockito.when(resultSet.getString(4)).thenReturn("user");
        Mockito.when(resultSet.getTimestamp(5)).thenReturn(null);
        Mockito.when(resultSet.getTimestamp(6)).thenReturn(null);

        ///

        when(statement.executeQuery("SELECT * FROM properties where type_data = 'user'")).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true); // Giả lập không tìm thấy user_id trong ResultSet

        // Gọi phương thức cần kiểm tra
        GeneralResponse<?> response = dataUserService.createDataUser(dto);

        // Kiểm tra kết quả
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED.value(), response.getStatusCode());
        assertEquals(HttpStatusConstant.CREATE_SUCCESS_MESSAGE, response.getMessage());
    }


    @Test
    @Order(2)
    public void testCreateDataUserSQLException() throws SQLException {
        // Thiết lập dto
        // Khi getConnection() được gọi, trả về một connection mock
        Map<String, Object> dto = new HashMap<>();
        dto.put("user_id", "user123456");
        dto.put("last_update", "2018-11-12 17:49:30.000 +0700");
        /////
        Mockito.when(resultSet.next()).thenReturn(true);
        Mockito.when(resultSet.getInt(1)).thenReturn(1);
        Mockito.when(resultSet.getString(2)).thenReturn("image");
        Mockito.when(resultSet.getString(3)).thenReturn("image");
        Mockito.when(resultSet.getString(4)).thenReturn("user");
        Mockito.when(resultSet.getTimestamp(5)).thenReturn(null);
        Mockito.when(resultSet.getTimestamp(6)).thenReturn(null);

        ///

        when(statement.executeQuery("SELECT * FROM properties where type_data = 'user'")).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true); // Giả lập không tìm thấy user_id trong ResultSet

        // Gọi phương thức cần kiểm tra
        GeneralResponse<?> response = dataUserService.createDataUser(dto);

        // Kiểm tra kết quả
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCode());
    }

    @Test
    @Order(3)
    public void createDataUserEmptyRequestIdReturnsError() {
        GeneralResponse<?> response = dataUserService.createDataUser(request);
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCode());
        assertEquals("The user_id is already present in the item catalog.", response.getMessage());
    }

    @Test
    @Order(4)
    public void createDataUserLongRequestIdReturnsError() {
        // Arrange
        Map<String, Object> dto = new HashMap<>();
        dto.put("user_id", "a".repeat(129));

        // Act
        GeneralResponse<?> response = dataUserService.createDataUser(dto);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCode());
        assertEquals("The user_id exceeds the max length of 128 characters", response.getMessage());
    }

    @Test
    @Order(5)
    public void createDataUserNonexistentPropertiesReturnsError() {
        // Arrange
        Map<String, Object> dto = new HashMap<>();
        dto.put("user_id", "test_user");
        dto.put("property_1", "value_1");
        dto.put("property_2", 2);
        dto.put("nonexistent_property", "value");

        // Act
        GeneralResponse<?> response = dataUserService.createDataUser(dto);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCode());
        assertEquals(HttpStatusConstant.PROPERTY_NOT_IN_DATABASE, response.getMessage());
    }

    @Test
    @Order(6)
    public void createDataUserExistingRequestIdReturnsError() {
        // Arrange
        Map<String, Object> dto = new HashMap<>();
        dto.put("user_id", "user1234");

        // Act
        GeneralResponse<?> response = dataUserService.createDataUser(dto);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCode());
        assertEquals("The user_id is already present in the item catalog.", response.getMessage());
    }

    @Test
    @Order(7)
    public void createDataUserBlankRequestIdReturnsError() {
        // Arrange
        Map<String, Object> dto = new HashMap<>();
        dto.put("user_id", "");

        // Act
        GeneralResponse<?> response = dataUserService.createDataUser(dto);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCode());
        assertEquals("The user_id field is required", response.getMessage());
    }

    @Test
    @Order(8)
    public void testGetUserNonId() {
        GeneralResponse<?> response = dataUserService.getUser("");
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCode());
        assertEquals("The user_id field is required", response.getMessage());
    }

    @Test
    @Order(9)
    public void testGetUserNotMathRegex() {
        GeneralResponse<?> response = dataUserService.getUser("%$&");
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCode());
        assertEquals("The user_id does not match ^[a-zA-Z0-9_-:@.]+$", response.getMessage());
    }

    @Test
    @Order(10)
    public void testGetUserNotFoundId() {
        GeneralResponse<?> response = dataUserService.getUser("id999");
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCode());
        assertEquals("User of the given user_id is not present in the catalog.", response.getMessage());
    }

    @Test
    @Order(11)
    public void testGetUserSuccess() throws SQLException {
        Map<String, Object> dto = new HashMap<>();
        dto.put("user_id", "user1234");
        Mockito.when(resultSet.next()).thenReturn(true);
        Mockito.when(resultSet.getString(1)).thenReturn("user1234");
        Mockito.when(resultSet.getString(2)).thenReturn("2009-11-12 17:49:30.000");
        when(statement.executeQuery("SELECT * FROM data_user WHERE user_id = 'user1234'")).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        GeneralResponse<?> response = dataUserService.getUser("user1234");
        assertEquals(HttpStatus.OK.value(), response.getStatusCode());
        var result = (Map<String, Object>) response.getData();
        assertEquals(result.get("user_id"), dto.get("user_id"));
    }

    @Test
    @Order(12)
    public void testUpdateUserNonId() {
        Map<String, Object> dto = new HashMap<>();
        dto.put("user_id", "user1234");
        GeneralResponse<?> response = dataUserService.updateUser(dto, "");
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCode());
        assertEquals("The user_id field is required", response.getMessage());
    }

    @Test
    @Order(13)
    public void testUpdateUserNonProperty() {
        Map<String, Object> dto = new HashMap<>();
        dto.put("user_id", "user1234");
        dto.put("hihi", "ggggg");
        GeneralResponse<?> response = dataUserService.updateUser(dto, "user1234");
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCode());
        assertEquals("Property of the given name is not present in the database.", response.getMessage());
    }

    @Test
    @Order(14)
    public void testUpdateUserSuccess() throws SQLException {
        Mockito.when(resultSet.next()).thenReturn(true);
        Mockito.when(resultSet.getString(1)).thenReturn("user1234");
        Mockito.when(resultSet.getString(2)).thenReturn("2009-11-12 17:49:30.000");
        when(statement.executeQuery("SELECT * FROM data_user WHERE user_id = 'user1234'")).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        GeneralResponse<?> response = dataUserService.updateUser(request, "user1234");
        assertEquals(HttpStatus.OK.value(), response.getStatusCode());
    }

    @Test
    @Order(15)
    public void testBulkInsertSuccess() throws SQLException {

        Map<String, Object> dto = new HashMap<>();
        dto.put("user_id", "test1");
        dto.put("last_update", "2023-12-15 13:41:02.910");

        Map<String, Object> dto1 = new HashMap<>();
        dto1.put("user_id", "test2");

        Map<String, Object> dto3 = new HashMap<>();
        dto3.put("user_id", "test2".repeat(30));
        dto3.put("last_update", "2023-12-15 13:41:02.910");

        Map<String, Object> dto4 = new HashMap<>();
        dto4.put("user_id", "user1234");
        dto4.put("last_update", "2023-12-15 13:41:02.910");

        List<Map<String, Object>> list = new ArrayList<>();
        list.add(dto);
        list.add(dto1);
        list.add(dto3);
        list.add(dto4);

        Mockito.when(resultSet.next()).thenReturn(true);
        Mockito.when(resultSet.getString(1)).thenReturn("user1234");

        when(statement.executeQuery("Select * from data_user")).thenReturn(resultSet);

        GeneralResponse<?> response = dataUserService.importUser(list);
        assertEquals(HttpStatus.OK.value(), response.getStatusCode());
    }

    @Test
    @Order(16)
    public void testBulkInsertSqlEx() throws SQLException {

        Map<String, Object> dto = new HashMap<>();
        dto.put("user_id", "test1");
        dto.put("last_update", "2023-12-15 13:41:02.910 +0700");

        Map<String, Object> dto1 = new HashMap<>();
        dto1.put("user_id", "test2");
        dto1.put("last_update", "2023-12-15 13:41:02.910 +0700");

        Map<String, Object> dto3 = new HashMap<>();
        dto3.put("user_id", "test2".repeat(30));
        dto3.put("last_update", "2023-12-15 13:41:02.910 +0700");

        Map<String, Object> dto4 = new HashMap<>();
        dto4.put("user_id", "user1234");
        dto4.put("last_update", "2023-12-15 13:41:02.910");

        List<Map<String, Object>> list = new ArrayList<>();
        list.add(dto);
        list.add(dto1);
        list.add(dto3);
        list.add(dto4);

        Mockito.when(resultSet.next()).thenReturn(true);
        Mockito.when(resultSet.getString(1)).thenReturn("user1234");

        when(statement.executeQuery("Select * from data_user")).thenReturn(resultSet);

        GeneralResponse<?> response = dataUserService.importUser(list);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), response.getStatusCode());
    }

    @Test
    @Order(17)
    public void testCreateUserPropertyNonName() {
        Map<String, Object> dto = new HashMap<>();
        dto.put("type", "Text");
        GeneralResponse<?> response = dataUserService.createUserProperty(dto);
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCode());
        assertEquals(HttpStatusConstant.INVALID_PROPERTY_USER, response.getMessage());
    }

    @Test
    @Order(18)
    public void testCreateUserPropertyBlankName() {
        Map<String, Object> dto = new HashMap<>();
        dto.put("type", "Text");
        dto.put("property-name", "");
        GeneralResponse<?> response = dataUserService.createUserProperty(dto);
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCode());
        assertEquals(HttpStatusConstant.INVALID_PROPERTY_USER, response.getMessage());
    }

    @Test
    @Order(19)
    public void testCreateUserPropertyNotMatchRegex() {
        Map<String, Object> dto = new HashMap<>();
        dto.put("type", "Text");
        dto.put("property-name", "^%&%%^");
        GeneralResponse<?> response = dataUserService.createUserProperty(dto);
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCode());
        assertEquals(HttpStatusConstant.INVALID_PROPERTY_USER, response.getMessage());
    }

    @Test
    @Order(20)
    public void testCreateUserPropertyMatchId() {
        Map<String, Object> dto = new HashMap<>();
        dto.put("type", "Text");
        dto.put("property-name", "user_id");
        GeneralResponse<?> response = dataUserService.createUserProperty(dto);
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCode());
        assertEquals(HttpStatusConstant.INVALID_PROPERTY_USER, response.getMessage());
    }

    @Test
    @Order(21)
    public void testCreateUserPropertyLength() {
        Map<String, Object> dto = new HashMap<>();
        dto.put("type", "Text");
        dto.put("property-name", "user_id".repeat(20));
        GeneralResponse<?> response = dataUserService.createUserProperty(dto);
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCode());
        assertEquals(HttpStatusConstant.INVALID_PROPERTY_USER, response.getMessage());
    }

    @Test
    @Order(22)
    public void testCreateUserPropertyLConflict() {
        Map<String, Object> dto = new HashMap<>();
        dto.put("type", "Text");
        dto.put("property-name", "last_update");
        GeneralResponse<?> response = dataUserService.createUserProperty(dto);
        assertEquals(HttpStatus.CONFLICT.value(), response.getStatusCode());
        assertEquals(HttpStatusConstant.CONFLICT_PROPERTY_USER, response.getMessage());
    }

    @Test
    @Order(23)
    public void testCreateUserPropertyErrorType() {
        Map<String, Object> dto = new HashMap<>();
        dto.put("type", "jhjs");
        dto.put("property-name", "hihihih");
        GeneralResponse<?> response = dataUserService.createUserProperty(dto);
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCode());
        assertEquals(HttpStatusConstant.TYPE_NOT_DEFINE, response.getMessage());
    }

    @Test
    @Order(24)
    public void testCreateUserPropertySuccess() {
        Map<String, Object> dto = new HashMap<>();
        dto.put("type", "Text");
        dto.put("property-name", "test");
        GeneralResponse<?> response = dataUserService.createUserProperty(dto);
        assertEquals(HttpStatus.CREATED.value(), response.getStatusCode());
        assertEquals(HttpStatusConstant.SUCCESS_MESSAGE, response.getMessage());
    }

    @Test
    @Order(25)
    public void testDeleteUserPropertyNotMatch() {
        GeneralResponse<?> response = dataUserService.deleteUserProperty("^%&%");
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCode());
        assertEquals("Property name does not match ^[a-zA-Z0-9_-:]+$.", response.getMessage());
    }

    @Test
    @Order(26)
    public void testDeleteUserPropertyNonProperty() {
        GeneralResponse<?> response = dataUserService.deleteUserProperty("test2222");
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCode());
        assertEquals(HttpStatusConstant.PROPERTY_NOT_IN_DATABASE, response.getMessage());
    }

    @Test
    @Order(27)
    public void testDeleteUserProperty() {
        GeneralResponse<?> response = dataUserService.deleteUserProperty("test");
        assertEquals(HttpStatus.OK.value(), response.getStatusCode());
        assertEquals(HttpStatusConstant.SUCCESS_MESSAGE, response.getMessage());
    }

    @Test
    @Order(28)
    public void testGetUserPropertyNotMatch() {
        GeneralResponse<?> response = dataUserService.getUserProperty("^%&%");
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCode());
        assertEquals("Property name does not match ^[a-zA-Z0-9_-:]+$.", response.getMessage());
    }

    @Test
    @Order(29)
    public void testGetUserPropertyNotFound() {
        GeneralResponse<?> response = dataUserService.getUserProperty("test2222");
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCode());
        assertEquals(HttpStatusConstant.PROPERTY_NOT_IN_DATABASE, response.getMessage());
    }

    @Test
    @Order(29)
    public void testGetUserPropertySuccess() {
        GeneralResponse<?> response = dataUserService.getUserProperty("user_id");

        Map<String, Object> properties = (Map<String, Object>) response.getData();

        assertEquals(HttpStatus.OK.value(), response.getStatusCode());
        assertEquals(properties.get("property_name"), "user_id");
    }

    @Test
    @Order(30)
    public void testGetSpecificProperties() {
        GeneralResponse<?> response = dataUserService.getSpecificProperties();
        List<Map<String, Object>> properties = (List<Map<String, Object>>) response.getData();
        assertEquals(HttpStatus.OK.value(), response.getStatusCode());
        assertFalse(properties.isEmpty());
    }
}