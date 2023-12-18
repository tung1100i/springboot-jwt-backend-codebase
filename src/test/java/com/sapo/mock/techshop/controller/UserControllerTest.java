package com.sapo.mock.techshop.controller;

import com.sapo.mock.techshop.dto.response.GeneralResponse;
import com.sapo.mock.techshop.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserServiceImpl userService;

    private UserController userController;

    @BeforeEach
    public void setUp() {
        userController = new UserController(userService);
    }

    @Test
    void createDataUser() {
        when(userService.createUser(new HashMap<>())).thenReturn(GeneralResponse.ok());
        var result =userController.createUser(new HashMap<>());
        assertEquals(HttpStatus.OK.value(), result.getStatusCode());

    }

    @Test
    void bulkInsert() {
        when(userService.bulkImportUser(List.of(new HashMap<>()))).thenReturn(GeneralResponse.ok());
        var result =userController.bulkImportUser(List.of(new HashMap<>()));
        assertEquals(HttpStatus.OK.value(), result.getStatusCode());
    }

    @Test
    void updateUser() {
        when(userService.updateUser(new HashMap<>(), "name")).thenReturn(GeneralResponse.ok());
        var result =userController.updateUser(new HashMap<>(), "name");
        assertEquals(HttpStatus.OK.value(), result.getStatusCode());
    }

    @Test
    void getById() {
        when(userService.getUser("name")).thenReturn(GeneralResponse.ok());
        var result =userController.getUser("name");
        assertEquals(HttpStatus.OK.value(), result.getStatusCode());
    }

    @Test
    void createUserProperty() {
        when(userService.createUserProperty(new HashMap<>())).thenReturn(GeneralResponse.ok());
        var result =userController.createUserProperty(new HashMap<>());
        assertEquals(HttpStatus.OK.value(), result.getStatusCode());
    }

    @Test
    void deleteUserProperty() {
        when(userService.deleteUserProperty("name")).thenReturn(GeneralResponse.ok());
        var result =userController.deleteUserProperty("name");
        assertEquals(HttpStatus.OK.value(), result.getStatusCode());
    }

    @Test
    void getUserProperty() {
        when(userService.getUserProperty("name")).thenReturn(GeneralResponse.ok());
        var result =userController.getUserProperty("name");
        assertEquals(HttpStatus.OK.value(), result.getStatusCode());
    }

    @Test
    void getSpecificProperties() {
        when(userService.getListUserProperty()).thenReturn(GeneralResponse.ok());
        var result =userController.getListUserProperty();
        assertEquals(HttpStatus.OK.value(), result.getStatusCode());
    }
}