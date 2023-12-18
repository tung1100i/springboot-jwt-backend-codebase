package com.sapo.mock.techshop.controller;

import com.sapo.mock.techshop.dto.response.GeneralResponse;
import com.sapo.mock.techshop.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/catalogs/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping()
    public GeneralResponse<?> createUser(@RequestParam Map<String, Object> userRequest) {
        return userService.createUser(userRequest);
    }

    @PutMapping("/{id}")
    public GeneralResponse<?> updateUser(@RequestBody Map<String, Object> userRequest, @PathVariable("id") String id) {
        return userService.updateUser(userRequest, id);
    }

    @GetMapping("/{id}")
    public GeneralResponse<?> getUser(@PathVariable("id") String id) {
        return userService.getUser(id);
    }

    //viết api bulk import
    @PostMapping("/bulk-import")
    public GeneralResponse<?> bulkImportUser(@RequestBody List<Map<String, Object>> userRequest) {
        return userService.bulkImportUser(userRequest);
    }

    @PostMapping("/properties")
    public GeneralResponse<?> createUserProperty(@RequestBody Map<String, Object> request) {
        return userService.createUserProperty(request);
    }

    @DeleteMapping("/properties/{name}")
    public GeneralResponse<?> deleteUserProperty(@PathVariable String name) {
        return userService.deleteUserProperty(name);
    }

    @GetMapping("/properties/{name}")
    public GeneralResponse<?> getUserProperty(@PathVariable String name) {
        return userService.getUserProperty(name);
    }

    @GetMapping("/properties")
    public GeneralResponse<?> getListUserProperty() {
        return userService.getListUserProperty();
    }
}
