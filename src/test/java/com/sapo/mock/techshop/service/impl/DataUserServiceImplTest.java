package com.sapo.mock.techshop.service.impl;

import com.sapo.mock.techshop.common.constant.HttpStatusConstant;
import com.sapo.mock.techshop.dto.response.GeneralResponse;
import com.sapo.mock.techshop.service.ConnectionService;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.util.ReflectionUtils;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
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

    @BeforeClass
    public static void beforeClass() throws Exception {
        Connection connection = DriverManager.getConnection("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1", "sa", "");
        Statement s = connection.createStatement();
        s.execute("CREATE TABLE properties (\n" +
                "property_id int NOT NULL,\n" +
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
                ");");
        PreparedStatement ps = connection.prepareStatement("select * from properties");
        ResultSet r = ps.executeQuery();
        if (r.next()) {
            System.out.println("data?");
        }
        r.close();
        ps.close();
        s.close();
        connection.close();
    }

    @Before
    public void setUp() throws SQLException {
        statement = Mockito.mock(Statement.class);
        resultSet = Mockito.mock(ResultSet.class);
        connectionService = Mockito.mock(ConnectionServiceImpl.class);
        connection = DriverManager.getConnection("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1", "sa", "");
        Mockito.lenient().when(connectionService.getConnection()).thenReturn(connection);
        dataUserService = new DataUserServiceImpl(connectionService);
    }

    @Test
    public void testCreateDataUser_Success() throws SQLException {
        // Thiết lập dataUserRequest
        Map<String, Object> dataUserRequest = new HashMap<>();
        dataUserRequest.put("user_id", "12345");
        // Thiết lập connection và resultSet
        // Khi getConnection() được gọi, trả về một connection mock

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
        GeneralResponse<?> response = dataUserService.createDataUser(dataUserRequest);

        // Kiểm tra kết quả
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED.value(), response.getStatusCode());
        assertEquals(HttpStatusConstant.CREATE_SUCCESS_MESSAGE, response.getMessage());
    }


    @Test
    public void createDataUser_emptyRequestId_returnsError() {
        // Arrange
        Map<String, Object> dataUserRequest = new HashMap<>();

        // Act
        GeneralResponse<?> response = dataUserService.createDataUser(dataUserRequest);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCode());
        assertEquals("The user_id field is required", response.getMessage());
    }

    @Test
    public void createDataUser_longRequestId_returnsError() {
        // Arrange
        Map<String, Object> dataUserRequest = new HashMap<>();
        dataUserRequest.put("user_id", "a".repeat(129));

        // Act
        GeneralResponse<?> response = dataUserService.createDataUser(dataUserRequest);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCode());
        assertEquals("The user_id exceeds the max length of 128 characters", response.getMessage());
    }

    @Test
    public void createDataUser_nonexistentProperties_returnsError() {
        // Arrange
        Map<String, Object> dataUserRequest = new HashMap<>();
        dataUserRequest.put("user_id", "test_user");
        dataUserRequest.put("property_1", "value_1");
        dataUserRequest.put("property_2", 2);
        dataUserRequest.put("nonexistent_property", "value");

        // Act
        GeneralResponse<?> response = dataUserService.createDataUser(dataUserRequest);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCode());
        assertEquals(HttpStatusConstant.PROPERTY_NOT_IN_DATABASE, response.getMessage());
    }

    @Test
    public void createDataUser_existingRequestId_returnsError() {
        // Arrange
        Map<String, Object> dataUserRequest = new HashMap<>();
        dataUserRequest.put("user_id", "test_user");

        // Act
        GeneralResponse<?> response = dataUserService.createDataUser(dataUserRequest);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCode());
        assertEquals("The user_id is already present in the item catalog.", response.getMessage());
    }

    public void testGetUser() {
    }

    public void testUpdateUser() {
    }

    public void testBulkInsert() {
    }

    public void testCreateUserProperty() {
    }

    public void testDeleteUserProperty() {
    }

    public void testGetUserProperty() {
    }

    public void testGetSpecificProperties() {
    }
}