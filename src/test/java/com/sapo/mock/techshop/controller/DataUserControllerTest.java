package com.sapo.mock.techshop.controller;

import com.sapo.mock.techshop.dto.response.GeneralResponse;
import com.sapo.mock.techshop.service.impl.DataUserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DataUserControllerTest {
    @Mock
    private DataUserServiceImpl dataUserService;

    private DataUserController dataUserController;

    @BeforeEach
    public void setUp() {
        dataUserController = new DataUserController(dataUserService);
    }

    @Test
    void createDataUser() {
        when(dataUserService.createDataUser(new HashMap<>())).thenReturn(GeneralResponse.ok());
        var result =dataUserController.createDataUser(new HashMap<>());
        assertEquals(HttpStatus.OK.value(), result.getStatusCode());

    }

    @Test
    void bulkInsert() {
        when(dataUserService.importUser(List.of(new HashMap<>()))).thenReturn(GeneralResponse.ok());
        var result =dataUserController.bulkInsert(List.of(new HashMap<>()));
        assertEquals(HttpStatus.OK.value(), result.getStatusCode());
    }

    @Test
    void updateUser() {
        when(dataUserService.updateUser(new HashMap<>(), "name")).thenReturn(GeneralResponse.ok());
        var result =dataUserController.updateUser(new HashMap<>(), "name");
        assertEquals(HttpStatus.OK.value(), result.getStatusCode());
    }

    @Test
    void getById() {
        when(dataUserService.getUser("name")).thenReturn(GeneralResponse.ok());
        var result =dataUserController.getById("name");
        assertEquals(HttpStatus.OK.value(), result.getStatusCode());
    }

    @Test
    void createUserProperty() {
        when(dataUserService.createUserProperty(new HashMap<>())).thenReturn(GeneralResponse.ok());
        var result =dataUserController.createUserProperty(new HashMap<>());
        assertEquals(HttpStatus.OK.value(), result.getStatusCode());
    }

    @Test
    void deleteUserProperty() {
        when(dataUserService.deleteUserProperty("name")).thenReturn(GeneralResponse.ok());
        var result =dataUserController.deleteUserProperty("name");
        assertEquals(HttpStatus.OK.value(), result.getStatusCode());
    }

    @Test
    void getUserProperty() {
        when(dataUserService.getUserProperty("name")).thenReturn(GeneralResponse.ok());
        var result =dataUserController.getUserProperty("name");
        assertEquals(HttpStatus.OK.value(), result.getStatusCode());
    }

    @Test
    void getSpecificProperties() {
        when(dataUserService.getSpecificProperties()).thenReturn(GeneralResponse.ok());
        var result =dataUserController.getSpecificProperties();
        assertEquals(HttpStatus.OK.value(), result.getStatusCode());
    }
}