package com.sapo.mock.techshop.controller;


import com.sapo.mock.techshop.dto.response.GeneralResponse;
import com.sapo.mock.techshop.service.DataUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * This class provides the REST API for managing users in the TechShop mock database.
 */
@RestController
@RequestMapping("/api/v1/catalogs/users")
@RequiredArgsConstructor
public class DataUserController {

    /**
     * The DataUserService instance used for interacting with the database.
     */
    private final DataUserService dataUserService;

    /**
     * Creates a new user in the database.
     *
     * @param request The request body containing the user information.
     * @return A GeneralResponse indicating the success or failure of the operation.
     */
    @PostMapping
    public GeneralResponse<?> createDataUser(@RequestBody Map<String, Object> request) {
        return dataUserService.createDataUser(request);
    }

    /**
     * Retrieves a user from the database.
     *
     * @param id The ID of the user to retrieve.
     * @return A GeneralResponse containing the user information, or an error message.
     */
    @GetMapping("/{id}")
    public GeneralResponse<?> getById(@PathVariable("id") String id) {
        return dataUserService.getUser(id);
    }

    /**
     * Updates an existing user in the database.
     *
     * @param request The request body containing the updated user information.
     * @param id      The ID of the user to update.
     * @return A GeneralResponse indicating the success or failure of the operation.
     */
    @PutMapping("/{id}")
    public GeneralResponse<?> updateUser(@RequestBody Map<String, Object> request, @PathVariable("id") String id) {
        return dataUserService.updateUser(request, id);
    }

    /**
     * Imports a list of users into the database.
     *
     * @param request A list of user information.
     * @return A GeneralResponse indicating the success or failure of the operation.
     */
    @PostMapping("/bulk-import")
    public GeneralResponse<?> bulkInsert(@RequestBody List<Map<String, Object>> request) {
        return dataUserService.bulkInsert(request);
    }

    /**
     * Creates a new user property in the database.
     *
     * @param request The request body containing the property information.
     * @return A GeneralResponse indicating the success or failure of the operation.
     */
    @PostMapping("/properties")
    public GeneralResponse<?> createUserProperty(@RequestBody Map<String, Object> request) {
        return dataUserService.createUserProperty(request);
    }

    /**
     * Deletes a user property from the database.
     *
     * @param columnName The name of the property to delete.
     * @return A GeneralResponse indicating the success or failure of the operation.
     */
    @DeleteMapping("/properties/{columnName}")
    public GeneralResponse<?> deleteUserProperty(@PathVariable String columnName) {
        return dataUserService.deleteUserProperty(columnName);
    }

    /**
     * Retrieves a specific user property from the database.
     *
     * @param columnName The name of the property to retrieve.
     * @return A GeneralResponse containing the property information, or an error message.
     */
    @GetMapping("/properties/{columnName}")
    public GeneralResponse<?> getUserProperty(@PathVariable String columnName) {
        return dataUserService.getUserProperty(columnName);
    }

    /**
     * Retrieves a list of all user properties.
     *
     * @return A GeneralResponse containing the list of properties, or an error message.
     */
    @GetMapping("/properties")
    public GeneralResponse<?> getSpecificProperties() {
        return dataUserService.getSpecificProperties();
    }
}
