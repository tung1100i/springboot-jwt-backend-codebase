package com.sapo.mock.techshop.common.exception.handler;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class HandleExceptionTest {

    private HandleException exception;

    @BeforeEach
    void setUp() {
        exception = new HandleException();
    }

    @Test
    void handleNullPointerException() {
        var result = exception.handleNullPointerException(new NullPointerException());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), result.getStatusCode());
    }

    @Test
    void handleSQLException() {
        var result = exception.handleSQLException(new SQLException());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), result.getStatusCode());
    }

    @Test
    void handleException() {
        var result = exception.handleException(new Exception());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), result.getStatusCode());
    }
}