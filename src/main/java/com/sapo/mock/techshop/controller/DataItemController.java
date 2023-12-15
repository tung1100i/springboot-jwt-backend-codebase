package com.sapo.mock.techshop.controller;

import com.sapo.mock.techshop.dto.response.GeneralResponse;
import com.sapo.mock.techshop.service.DataItemService;
import com.sapo.mock.techshop.service.impl.PropertyServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/catalogs/items")
public class DataItemController {

    @Autowired
    private DataItemService dataItemService;


    @Autowired
    private PropertyServiceImpl propertyService;

    @PostMapping
    public GeneralResponse<?> createDataItem(@RequestBody Map<String, Object> request) {
        return dataItemService.createDataItem(request);
    }

    @PostMapping("/bulk-import")
    public GeneralResponse<?> bulkInsert(@RequestBody List<Map<String, Object>> request) {
        return dataItemService.bulkInsert(request);
    }


    @PutMapping("/{id}")
    public GeneralResponse<?> update(@RequestBody Map<String, Object> request, @PathVariable String id) {
        return dataItemService.update(request, id);
    }

    @GetMapping("/{id}")
    public GeneralResponse<?> getById(@PathVariable String id) {
        return dataItemService.getById(id);
    }
}
