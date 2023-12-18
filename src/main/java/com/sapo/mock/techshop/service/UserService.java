package com.sapo.mock.techshop.service;

import com.sapo.mock.techshop.dto.response.GeneralResponse;

import java.util.List;
import java.util.Map;

public interface UserService {
    GeneralResponse<?> createUser(Map<String, Object> userRequest);

    GeneralResponse<?> updateUser(Map<String, Object> userRequest, String userId);

    GeneralResponse<?> getUser(String id);

    GeneralResponse<?> bulkImportUser(List<Map<String, Object>> userRequest);

    GeneralResponse<?> createUserProperty(Map<String, Object> request);

    GeneralResponse<?> deleteUserProperty(String name);

    GeneralResponse<?> getUserProperty(String name);

    GeneralResponse<?> getListUserProperty();
}
