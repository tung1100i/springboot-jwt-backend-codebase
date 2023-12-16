package com.sapo.mock.techshop.service.impl;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.test.util.ReflectionTestUtils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class ConnectionServiceImplTest {

    @Mock
    private static Connection connection;

    private ConnectionServiceImpl connectionService;

    @BeforeAll
    public static void beforeClass() throws Exception {
        Connection connection = DriverManager.getConnection("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;DATABASE_TO_LOWER=TRUE", "sa", "");
    }

    @BeforeEach
    void setUp() throws SQLException {
        connectionService = new ConnectionServiceImpl();
        connection = DriverManager.getConnection("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;DATABASE_TO_LOWER=TRUE", "sa", "");
        ReflectionTestUtils.setField(connectionService, "url", "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;DATABASE_TO_LOWER=TRUE");
        ReflectionTestUtils.setField(connectionService, "user", "sa");
        ReflectionTestUtils.setField(connectionService, "password", "");
    }

    @Test
    void getConnection() {
        assertNotNull(connectionService.getConnection());
    }

    @Test
    void getConnectionFail() {
        ReflectionTestUtils.setField(connectionService, "url", "jdbc:postgresql://107.206.248.168:5432/vrp_a");
        assertNull(connectionService.getConnection());
    }
}