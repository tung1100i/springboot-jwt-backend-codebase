package com.sapo.mock.techshop.controller;


import com.sapo.mock.techshop.dto.response.GeneralResponse;
import com.sapo.mock.techshop.service.DataUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/catalogs/users")
public class DataUserController {

    @Autowired
    private DataUserService dataUserService;

    @PostMapping
    public GeneralResponse<?> createDataUser(@RequestBody Map<String, Object> request) {
        return dataUserService.createDataUser(request);
    }

    @GetMapping("/{id}")
    public GeneralResponse<?> createDataUser(@PathVariable("id") String id) {
        return dataUserService.getUser(id);
    }

    @PutMapping("/{id}")
    public GeneralResponse<?> updateUser(@RequestBody Map<String, Object> request, @PathVariable("id") String id) {
        return dataUserService.updateUser(request, id);
    }

    @PostMapping("/bulk-import")
    public GeneralResponse<?> bulkInsert(@RequestBody List<Map<String, Object>> request) {
        return dataUserService.bulkInsert(request);
    }
}
