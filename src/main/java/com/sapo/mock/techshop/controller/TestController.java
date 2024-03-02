package com.sapo.mock.techshop.controller;


import com.sapo.mock.techshop.service.TestService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

/**
 * This class provides the REST API for managing users in the TechShop mock database.
 */
@RestController
@RequestMapping("/test")
@RequiredArgsConstructor
public class TestController {
    private final TestService testService;
    @GetMapping
    public void createDataUser(@RequestParam String request) throws IOException {
        testService.getIndex(request);
    }
}
