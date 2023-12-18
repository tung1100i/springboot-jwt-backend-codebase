package com.sapo.mock.techshop.controller;

import com.sapo.mock.techshop.dto.response.GeneralResponse;
import com.sapo.mock.techshop.service.ItemService;
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
class ItemControllerTest {

    @Mock
    private ItemService itemService;

    private ItemController itemController;

    @BeforeEach
    public void setUp() {
        itemController = new ItemController(itemService);
    }

    @Test
    void createDataItem() {
        when(itemService.createItem(new HashMap<>())).thenReturn(GeneralResponse.ok());
        var result =itemController.createItem(new HashMap<>());
        assertEquals(HttpStatus.OK.value(), result.getStatusCode());

    }

    @Test
    void bulkInsert() {
        when(itemService.bulkImportItem(List.of(new HashMap<>()))).thenReturn(GeneralResponse.ok());
        var result =itemController.bulkImportItem(List.of(new HashMap<>()));
        assertEquals(HttpStatus.OK.value(), result.getStatusCode());
    }

    @Test
    void update() {
        when(itemService.updateItem(new HashMap<>(), "name")).thenReturn(GeneralResponse.ok());
        var result =itemController.updateItem(new HashMap<>(), "name");
        assertEquals(HttpStatus.OK.value(), result.getStatusCode());
    }

    @Test
    void getById() {
        when(itemService.getItem("name")).thenReturn(GeneralResponse.ok());
        var result =itemController.getItem("name");
        assertEquals(HttpStatus.OK.value(), result.getStatusCode());
    }

    @Test
    void createItemProperty() {
        when(itemService.createItemProperty(new HashMap<>())).thenReturn(GeneralResponse.ok());
        var result =itemController.createItemProperty(new HashMap<>());
        assertEquals(HttpStatus.OK.value(), result.getStatusCode());
    }

    @Test
    void deleteItemProperty() {
        when(itemService.deleteItemProperty("name")).thenReturn(GeneralResponse.ok());
        var result =itemController.deleteItemProperty("name");
        assertEquals(HttpStatus.OK.value(), result.getStatusCode());
    }

    @Test
    void getItemProperty() {
        when(itemService.getItemProperty("name")).thenReturn(GeneralResponse.ok());
        var result =itemController.getItemProperty("name");
        assertEquals(HttpStatus.OK.value(), result.getStatusCode());
    }

    @Test
    void getListItemProperty() {
        when(itemService.getAllItemProperty()).thenReturn(GeneralResponse.ok());
        var result =itemController.getAllItemProperty();
        assertEquals(HttpStatus.OK.value(), result.getStatusCode());
    }
}