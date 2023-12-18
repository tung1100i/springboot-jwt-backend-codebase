package com.sapo.mock.techshop.service.impl;

import com.sapo.mock.techshop.common.constant.HttpStatusConstant;
import com.sapo.mock.techshop.dto.response.GeneralResponse;
import junit.framework.TestCase;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static junit.framework.TestCase.assertFalse;
import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {

    @Mock
    private Connection connection;

    @Mock
    private Statement statement;
    @Mock
    private ResultSet resultSet;
    @Mock
    private ConnectionServiceImpl connectionService;

    @InjectMocks
    private ItemServiceImpl itemService;

    Map<String, Object> itemRequest = new HashMap<>();

    @BeforeAll
    public static void beforeClass() throws Exception {
        Connection connection = DriverManager.getConnection("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;DATABASE_TO_LOWER=TRUE", "sa", "");
        Statement s = connection.createStatement();
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
                "('user1234', '2009-11-12 17:49:30.000');" +
                "INSERT INTO data_item (item_id, last_update) VALUES" +
                "('item1234', '2009-11-12 17:49:30.000');"
        );
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

    @BeforeEach
    public void setUp() throws SQLException {
        statement = Mockito.mock(Statement.class);
        resultSet = Mockito.mock(ResultSet.class);
        connectionService = Mockito.mock(ConnectionServiceImpl.class);
        connection = DriverManager.getConnection("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;DATABASE_TO_LOWER=TRUE", "sa", "");
        Mockito.lenient().when(connectionService.getConnection()).thenReturn(connection);
        itemService = new ItemServiceImpl(connectionService);
        itemRequest.put("item_id", "item1234");
        itemRequest.put("last_update", "2018-11-12 17:49:30.000");
    }

    @Test
    @Order(1)
    public void testCreateDataItem_Success() throws SQLException {
        // Thiết lập itemRequest
        // Khi getConnection() được gọi, trả về một connection mock
        Map<String, Object> itemRequest = new HashMap<>();
        itemRequest.put("item_id", "item12345678");
        /////
//        when(resultSet.next()).thenReturn(true);
//        when(resultSet.getInt(1)).thenReturn(1);
//        when(resultSet.getString(2)).thenReturn("image");
//        when(resultSet.getString(3)).thenReturn("image");
//        when(resultSet.getString(4)).thenReturn("item");
//        when(resultSet.getTimestamp(5)).thenReturn(null);
//        when(resultSet.getTimestamp(6)).thenReturn(null);
//
//        ///
//
//        when(statement.executeQuery("SELECT * FROM properties where type_data = 'item'")).thenReturn(resultSet);
//        when(resultSet.next()).thenReturn(true); // Giả lập không tìm thấy user_id trong ResultSet

        // Gọi phương thức cần kiểm tra
        GeneralResponse<?> response = itemService.createItem(itemRequest);

        // Kiểm tra kết quả
        assertNotNull(response);
        TestCase.assertEquals(HttpStatus.OK.value(), response.getStatusCode());
        TestCase.assertEquals(HttpStatusConstant.CREATE_SUCCESS, response.getMessage());
    }

//    @Test
//    @Order(2)
//    public void testCreateDataItem_SQL_Exception() throws SQLException {
//        // Thiết lập itemRequest
//        // Khi getConnection() được gọi, trả về một connection mock
//        Map<String, Object> itemRequest = new HashMap<>();
//        itemRequest.put("item_id", "item123456");
//        itemRequest.put("last_update", "2018-11-12 17:49:30.000 +0700");
//        /////
////        when(resultSet.next()).thenReturn(true);
////        when(resultSet.getInt(1)).thenReturn(1);
////        when(resultSet.getString(2)).thenReturn("image");
////        when(resultSet.getString(3)).thenReturn("image");
////        when(resultSet.getString(4)).thenReturn("item");
////        when(resultSet.getTimestamp(5)).thenReturn(null);
////        when(resultSet.getTimestamp(6)).thenReturn(null);
////
////        ///
////
////        when(statement.executeQuery("SELECT * FROM properties where type_data = 'item'")).thenReturn(resultSet);
////        when(resultSet.next()).thenReturn(true); // Giả lập không tìm thấy item_id trong ResultSet
//
//        // Gọi phương thức cần kiểm tra
//        GeneralResponse<?> response = itemService.createItem(itemRequest);
//
//        // Kiểm tra kết quả
//        assertNotNull(response);
//        TestCase.assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCode());
//    }

    @Test
    @Order(3)
    public void createDataItem_emptyRequestId_returnsError() {
        GeneralResponse<?> response = itemService.createItem(itemRequest);
        assertNotNull(response);
        TestCase.assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCode());
        TestCase.assertEquals("The item_id is already present in the item catalog.", response.getMessage());
    }

    @Test
    @Order(4)
    public void createDataItem_longRequestId_returnsError() {
        // Arrange
        Map<String, Object> itemRequest = new HashMap<>();
        itemRequest.put("item_id", "a".repeat(129));

        // Act
        GeneralResponse<?> response = itemService.createItem(itemRequest);

        // Assert
        TestCase.assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCode());
        TestCase.assertEquals("The item_id exceeds the max length of 128 characters", response.getMessage());
    }

    @Test
    @Order(5)
    public void createDataItem_nonexistentProperties_returnsError() {
        // Arrange
        Map<String, Object> itemRequest = new HashMap<>();
        itemRequest.put("item_id", "test_item");
        itemRequest.put("property_1", "value_1");
        itemRequest.put("property_2", 2);
        itemRequest.put("nonexistent_property", "value");

        // Act
        GeneralResponse<?> response = itemService.createItem(itemRequest);

        // Assert
        TestCase.assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCode());
        TestCase.assertEquals("Property of the given name is not present in the database.", response.getMessage());
    }

    @Test
    @Order(6)
    public void createDataItem_existingRequestId_returnsError() {
        // Arrange
        Map<String, Object> itemRequest = new HashMap<>();
        itemRequest.put("item_id", "item1234");

        // Act
        GeneralResponse<?> response = itemService.createItem(itemRequest);

        // Assert
        TestCase.assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCode());
        TestCase.assertEquals("The item_id is already present in the item catalog.", response.getMessage());
    }

    @Test
    @Order(7)
    public void createDataItem_blankRequestId_returnsError() {
        // Arrange
        Map<String, Object> itemRequest = new HashMap<>();
        itemRequest.put("item_id", "");

        // Act
        GeneralResponse<?> response = itemService.createItem(itemRequest);

        // Assert
        TestCase.assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCode());
        TestCase.assertEquals("The item_id field is required", response.getMessage());
    }

    @Test
    @Order(8)
    public void testGetUserNonId() {
        GeneralResponse<?> response = itemService.getItem("");
        TestCase.assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCode());
        TestCase.assertEquals("The item_id does not match ^[a-zA-Z0-9_-:@.]+$", response.getMessage());
    }

    @Test
    @Order(9)
    public void testGetUserNotMathRegex() {
        GeneralResponse<?> response = itemService.getItem("%$&");
        TestCase.assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCode());
        TestCase.assertEquals("The item_id does not match ^[a-zA-Z0-9_-:@.]+$", response.getMessage());
    }

    @Test
    @Order(10)
    public void testGetUserNotFoundId() {
        GeneralResponse<?> response = itemService.getItem("id999");
        TestCase.assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCode());
        TestCase.assertEquals("The item_id is not present in the item catalog.", response.getMessage());
    }

    @Test
    @Order(11)
    public void testGetUserSuccess() throws SQLException {
        Map<String, Object> itemRequest = new HashMap<>();
        itemRequest.put("item_id", "item1234");
//        when(resultSet.next()).thenReturn(true);
//        when(resultSet.getString(1)).thenReturn("item1234");
//        when(resultSet.getString(2)).thenReturn("2009-11-12 17:49:30.000");
//        when(statement.executeQuery("SELECT * FROM data_item WHERE item_id = 'item1234'")).thenReturn(resultSet);
//        when(resultSet.next()).thenReturn(true);
        GeneralResponse<?> response = itemService.getItem("item1234");
        TestCase.assertEquals(HttpStatus.OK.value(), response.getStatusCode());
        var result = (Map<String, Object>) response.getData();
        TestCase.assertEquals(result.get("item_id"), itemRequest.get("item_id"));
    }

    @Test
    @Order(12)
    public void testUpdateUserNonId() {
        Map<String, Object> itemRequest = new HashMap<>();
        itemRequest.put("item_id", "item1234");
        GeneralResponse<?> response = itemService.updateItem(itemRequest, "");
        TestCase.assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCode());
        TestCase.assertEquals("The item_id field is required", response.getMessage());
    }

    @Test
    @Order(13)
    public void testUpdateUserNonProperty() {
        Map<String, Object> itemRequest = new HashMap<>();
        itemRequest.put("item_id", "item1234");
        itemRequest.put("hihi", "ggggg");
        GeneralResponse<?> response = itemService.updateItem(itemRequest, "item1234");
        TestCase.assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCode());
        TestCase.assertEquals("Property of the given name is not present in the database.", response.getMessage());
    }

    @Test
    @Order(14)
    public void testUpdateUserSuccess() throws SQLException {
//        when(resultSet.next()).thenReturn(true);
//        when(resultSet.getString(1)).thenReturn("item1234");
//        when(resultSet.getString(2)).thenReturn("2009-11-12 17:49:30.000");
//        when(statement.executeQuery("SELECT * FROM data_item WHERE item_id = 'item1234'")).thenReturn(resultSet);
//        when(resultSet.next()).thenReturn(true);
        GeneralResponse<?> response = itemService.updateItem(itemRequest, "item1234");
        TestCase.assertEquals(HttpStatus.OK.value(), response.getStatusCode());
    }

    @Test
    @Order(15)
    public void testBulkInsertSuccess() throws SQLException {

        Map<String, Object> itemRequest = new HashMap<>();
        itemRequest.put("item_id", "test1");
        itemRequest.put("last_update", "2023-12-15 13:41:02.910");

        Map<String, Object> itemRequest1 = new HashMap<>();
        itemRequest1.put("item_id", "test2");

        Map<String, Object> itemRequest3 = new HashMap<>();
        itemRequest3.put("item_id", "test2".repeat(30));
        itemRequest3.put("last_update", "2023-12-15 13:41:02.910");

        Map<String, Object> itemRequest4 = new HashMap<>();
        itemRequest4.put("item_id", "item1234");
        itemRequest4.put("last_update", "2023-12-15 13:41:02.910");

        List<Map<String, Object>> list = new ArrayList<>();
        list.add(itemRequest);
        list.add(itemRequest1);
        list.add(itemRequest3);
        list.add(itemRequest4);

//        when(resultSet.next()).thenReturn(true);
//        when(resultSet.getString(1)).thenReturn("item1234");
//
//        when(statement.executeQuery("Select * from data_item")).thenReturn(resultSet);

        GeneralResponse<?> response = itemService.bulkImportItem(list);
        TestCase.assertEquals(HttpStatus.OK.value(), response.getStatusCode());
    }

    @Test
    @Order(16)
    public void testBulkInsertSqlEx() throws SQLException {

        Map<String, Object> itemRequest = new HashMap<>();
        itemRequest.put("item_id", "test1");
        itemRequest.put("last_update", "2023-12-15 13:41:02.910 +0700");

        Map<String, Object> itemRequest1 = new HashMap<>();
        itemRequest1.put("item_id", "test2");
        itemRequest1.put("last_update", "2023-12-15 13:41:02.910 +0700");

        Map<String, Object> itemRequest3 = new HashMap<>();
        itemRequest3.put("item_id", "test2".repeat(30));
        itemRequest3.put("last_update", "2023-12-15 13:41:02.910 +0700");

        Map<String, Object> itemRequest4 = new HashMap<>();
        itemRequest4.put("item_id", "item1234");
        itemRequest4.put("last_update", "2023-12-15 13:41:02.910");

        List<Map<String, Object>> list = new ArrayList<>();
        list.add(itemRequest);
        list.add(itemRequest1);
        list.add(itemRequest3);
        list.add(itemRequest4);

//        when(resultSet.next()).thenReturn(true);
//        when(resultSet.getString(1)).thenReturn("item1234");
//
//        when(statement.executeQuery("Select * from data_item")).thenReturn(resultSet);

        GeneralResponse<?> response = itemService.bulkImportItem(list);
        TestCase.assertEquals(HttpStatus.OK.value(), response.getStatusCode());
    }

    @Test
    @Order(17)
    public void testCreateUserPropertyNonName() {
        Map<String, Object> itemRequest = new HashMap<>();
        itemRequest.put("type", "Text");
        GeneralResponse<?> response = itemService.createItemProperty(itemRequest);
        TestCase.assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCode());
        TestCase.assertEquals("Property name does not match ^[a-zA-Z_][0-9a-zA-Z_]*$, or it is a reserved keyword (‘’id’’, ‘’item_id’’), or its length exceeds 50 characters. Type information is missing, or the given type is invalid.", response.getMessage());
    }

    @Test
    @Order(18)
    public void testCreateUserPropertyBlankName() {
        Map<String, Object> itemRequest = new HashMap<>();
        itemRequest.put("type", "Text");
        itemRequest.put("property-name", "");
        GeneralResponse<?> response = itemService.createItemProperty(itemRequest);
        TestCase.assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCode());
        TestCase.assertEquals("Property name does not match ^[a-zA-Z_][0-9a-zA-Z_]*$, or it is a reserved keyword (‘’id’’, ‘’item_id’’), or its length exceeds 50 characters. Type information is missing, or the given type is invalid.", response.getMessage());
    }

    @Test
    @Order(19)
    public void testCreateUserPropertyNotMatchRegex() {
        Map<String, Object> itemRequest = new HashMap<>();
        itemRequest.put("type", "Text");
        itemRequest.put("property-name", "^%&%%^");
        GeneralResponse<?> response = itemService.createItemProperty(itemRequest);
        TestCase.assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCode());
        TestCase.assertEquals("Property name does not match ^[a-zA-Z_][0-9a-zA-Z_]*$, or it is a reserved keyword (‘’id’’, ‘’item_id’’), or its length exceeds 50 characters. Type information is missing, or the given type is invalid.", response.getMessage());
    }

    @Test
    @Order(20)
    public void testCreateUserPropertyMatchId() {
        Map<String, Object> itemRequest = new HashMap<>();
        itemRequest.put("type", "Text");
        itemRequest.put("property-name", "item_id");
        GeneralResponse<?> response = itemService.createItemProperty(itemRequest);
        TestCase.assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCode());
        TestCase.assertEquals("Property name does not match ^[a-zA-Z_][0-9a-zA-Z_]*$, or it is a reserved keyword (‘’id’’, ‘’item_id’’), or its length exceeds 50 characters. Type information is missing, or the given type is invalid.", response.getMessage());
    }

    @Test
    @Order(21)
    public void testCreateUserPropertyLength() {
        Map<String, Object> itemRequest = new HashMap<>();
        itemRequest.put("type", "Text");
        itemRequest.put("property-name", "item_id".repeat(20));
        GeneralResponse<?> response = itemService.createItemProperty(itemRequest);
        TestCase.assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCode());
        TestCase.assertEquals("Property name does not match ^[a-zA-Z_][0-9a-zA-Z_]*$, or it is a reserved keyword (‘’id’’, ‘’item_id’’), or its length exceeds 50 characters. Type information is missing, or the given type is invalid.", response.getMessage());
    }

    @Test
    @Order(22)
    public void testCreateUserPropertyLConflict() {
        Map<String, Object> itemRequest = new HashMap<>();
        itemRequest.put("type", "Text");
        itemRequest.put("property-name", "last_update");
        GeneralResponse<?> response = itemService.createItemProperty(itemRequest);
        TestCase.assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCode());
        TestCase.assertEquals("Property of the given name is already present in the database. In\n" +
                "many cases, you may consider this code success – it only tells you that nothing\n" +
                "has been written to the database.", response.getMessage());
    }

    @Test
    @Order(23)
    public void testCreateUserPropertyErrorType() {
        Map<String, Object> itemRequest = new HashMap<>();
        itemRequest.put("type", "jhjs");
        itemRequest.put("property-name", "hihihih");
        GeneralResponse<?> response = itemService.createItemProperty(itemRequest);
        TestCase.assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCode());
        TestCase.assertEquals("Property name does not match ^[a-zA-Z_][0-9a-zA-Z_]*$, or it is a reserved keyword (‘’id’’, ‘’item_id’’), or its length exceeds 50 characters. Type information is missing, or the given type is invalid.", response.getMessage());
    }

    @Test
    @Order(24)
    public void testCreateUserPropertySuccess() {
        Map<String, Object> itemRequest = new HashMap<>();
        itemRequest.put("type", "Text");
        itemRequest.put("property-name", "test");
        GeneralResponse<?> response = itemService.createItemProperty(itemRequest);
        TestCase.assertEquals(HttpStatus.CREATED.value(), response.getStatusCode());
        TestCase.assertEquals(HttpStatusConstant.CREATE_SUCCESS, response.getMessage());
    }

    @Test
    @Order(25)
    public void testDeleteUserPropertyNotMatch() {
        GeneralResponse<?> response = itemService.deleteItemProperty("^%&%");
        TestCase.assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCode());
        TestCase.assertEquals("Property of the given name is not present in the database. In many cases, you may consider this code success – it only tells you that nothing has been deleted from the database since the item property was already not present.", response.getMessage());
    }

    @Test
    @Order(26)
    public void testDeleteUserPropertyNonProperty() {
        GeneralResponse<?> response = itemService.deleteItemProperty("test2222");
        TestCase.assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCode());
        TestCase.assertEquals("Property of the given name is not present in the database. In many cases, you may consider this code success – it only tells you that nothing has been deleted from the database since the item property was already not present.", response.getMessage());
    }

    @Test
    @Order(27)
    public void testDeleteUserProperty() {
        GeneralResponse<?> response = itemService.deleteItemProperty("test");
        TestCase.assertEquals(HttpStatus.OK.value(), response.getStatusCode());
        TestCase.assertEquals(HttpStatusConstant.SUCCESS, response.getMessage());
    }

//    @Test
//    @Order(28)
//    public void testGetUserPropertyNotMatch() {
//        GeneralResponse<?> response = itemService.getItemProperty("^%&%");
//        TestCase.assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatusCode());
//        Map<String, Object> result = (Map<String, Object>) response.getData();
//        TestCase.assertEquals(result.size(), 0);
//    }

//    @Test
//    @Order(29)
//    public void testGetUserPropertyNotFound() {
//        GeneralResponse<?> response = itemService.getItemProperty("test2222");
//        TestCase.assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatusCode());
//        Map<String, Object> result = (Map<String, Object>) response.getData();
//        TestCase.assertEquals(result.size(), 0);
//    }

    @Test
    @Order(29)
    public void testGetUserPropertySuccess() {
        GeneralResponse<?> response = itemService.getItemProperty("last_update");
        Map<String, Object> properties = (Map<String, Object>) response.getData();
        TestCase.assertEquals(HttpStatus.OK.value(), response.getStatusCode());
        TestCase.assertEquals(properties.get("property-name"), "last_update");
    }

    @Test
    @Order(30)
    public void testGetSpecificProperties() {
        GeneralResponse<?> response = itemService.getAllItemProperty();
        List<Map<String, Object>> properties = (List<Map<String, Object>>) response.getData();
        TestCase.assertEquals(HttpStatus.OK.value(), response.getStatusCode());
        assertFalse(properties.isEmpty());
    }
}