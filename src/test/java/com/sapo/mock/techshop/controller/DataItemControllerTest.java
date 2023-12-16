package com.sapo.mock.techshop.controller;

import com.sapo.mock.techshop.dto.response.GeneralResponse;
import com.sapo.mock.techshop.service.impl.DataItemServiceImpl;
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
class DataItemControllerTest {

    @Mock
    private DataItemServiceImpl dataItemService;

    private DataItemController dataItemController;

    @BeforeEach
    public void setUp() {
        dataItemController = new DataItemController(dataItemService);
    }

    @Test
    void createDataItem() {
        when(dataItemService.createDataItem(new HashMap<>())).thenReturn(GeneralResponse.ok());
        var result =dataItemController.createDataItem(new HashMap<>());
        assertEquals(HttpStatus.OK.value(), result.getStatusCode());

    }

    @Test
    void bulkInsert() {
        when(dataItemService.bulkInsert(List.of(new HashMap<>()))).thenReturn(GeneralResponse.ok());
        var result =dataItemController.bulkInsert(List.of(new HashMap<>()));
        assertEquals(HttpStatus.OK.value(), result.getStatusCode());
    }

    @Test
    void update() {
        when(dataItemService.update(new HashMap<>(), "name")).thenReturn(GeneralResponse.ok());
        var result =dataItemController.update(new HashMap<>(), "name");
        assertEquals(HttpStatus.OK.value(), result.getStatusCode());
    }

    @Test
    void getById() {
        when(dataItemService.getById("name")).thenReturn(GeneralResponse.ok());
        var result =dataItemController.getById("name");
        assertEquals(HttpStatus.OK.value(), result.getStatusCode());
    }

    @Test
    void createItemProperty() {
        when(dataItemService.createItemProperty(new HashMap<>())).thenReturn(GeneralResponse.ok());
        var result =dataItemController.createItemProperty(new HashMap<>());
        assertEquals(HttpStatus.OK.value(), result.getStatusCode());
    }

    @Test
    void deleteItemProperty() {
        when(dataItemService.deleteItemProperty("name")).thenReturn(GeneralResponse.ok());
        var result =dataItemController.deleteItemProperty("name");
        assertEquals(HttpStatus.OK.value(), result.getStatusCode());
    }

    @Test
    void getItemProperty() {
        when(dataItemService.getItemProperty("name")).thenReturn(GeneralResponse.ok());
        var result =dataItemController.getItemProperty("name");
        assertEquals(HttpStatus.OK.value(), result.getStatusCode());
    }

    @Test
    void getListItemProperty() {
        when(dataItemService.getListItemProperty()).thenReturn(GeneralResponse.ok());
        var result =dataItemController.getListItemProperty();
        assertEquals(HttpStatus.OK.value(), result.getStatusCode());
    }
}