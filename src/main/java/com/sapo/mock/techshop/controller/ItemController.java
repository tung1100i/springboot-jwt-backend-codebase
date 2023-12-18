package com.sapo.mock.techshop.controller;

import com.sapo.mock.techshop.dto.response.GeneralResponse;
import com.sapo.mock.techshop.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

//viết controller cho item
@RestController
@RequestMapping("/api/v1/catalogs/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @PostMapping()
    public GeneralResponse<?> createItem(@RequestBody Map<String, Object> itemRequest) {
        return itemService.createItem(itemRequest);
    }

    @PutMapping("/{item_id}")
    public GeneralResponse<?> updateItem(@RequestBody Map<String, Object> itemRequest, @PathVariable String item_id) {
        return itemService.updateItem(itemRequest, item_id);
    }

    @GetMapping("/{item_id}")
    public GeneralResponse<?> getItem(@PathVariable String item_id) {
        return itemService.getItem(item_id);
    }

    //viết api bulk import
    @PostMapping("/bulk-import")
    public GeneralResponse<?> bulkImportItem(@RequestBody List<Map<String, Object>> itemRequest) {
        return itemService.bulkImportItem(itemRequest);
    }

    //viết api create item property
    @PostMapping("/properties")
    public GeneralResponse<?> createItemProperty(@RequestBody Map<String, Object> request) {
        return itemService.createItemProperty(request);
    }

    @DeleteMapping("/properties/{name}")
    public GeneralResponse<?> deleteItemProperty(@PathVariable String name) {
        return itemService.deleteItemProperty(name);
    }

    @GetMapping("/properties/{name}")
    public GeneralResponse<?> getItemProperty(@PathVariable String name) {
        return itemService.getItemProperty(name);
    }

    @GetMapping("/properties")
    public GeneralResponse<?> getAllItemProperty() {
        return itemService.getAllItemProperty();
    }
}
