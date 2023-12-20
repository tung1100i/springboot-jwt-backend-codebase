package com.sapo.mock.techshop.controller;

import com.sapo.mock.techshop.dto.response.GeneralResponse;
import com.sapo.mock.techshop.service.DataItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/catalogs/items")
@RequiredArgsConstructor
public class DataItemController {
    private final DataItemService dataItemService;

    @PostMapping
    public GeneralResponse createDataItem(@RequestBody Map<String, Object> request) {
        return dataItemService.createDataItem(request);
    }

    @PostMapping("/bulk-import")
    public GeneralResponse bulkInsert(@RequestBody List<Map<String, Object>> request) {
        return dataItemService.importItem(request);
    }


    @PutMapping("/{id}")
    public GeneralResponse update(@RequestBody Map<String, Object> request, @PathVariable String id) {
        return dataItemService.update(request, id);
    }

    @GetMapping("/{id}")
    public GeneralResponse getById(@PathVariable String id) {
        return dataItemService.getById(id);
    }

    @PostMapping("/properties")
    public GeneralResponse createItemProperty(@RequestBody Map<String, Object> request) {
        return dataItemService.createItemProperty(request);
    }

    @DeleteMapping("/properties/{name}")
    public GeneralResponse deleteItemProperty(@PathVariable String name) {
        return dataItemService.deleteItemProperty(name);
    }

    @GetMapping("/properties/{name}")
    public GeneralResponse getItemProperty(@PathVariable String name) {
        return dataItemService.getItemProperty(name);
    }

    @GetMapping("/properties")
    public GeneralResponse getListItemProperty() {
        return dataItemService.getListItemProperty();
    }
}
