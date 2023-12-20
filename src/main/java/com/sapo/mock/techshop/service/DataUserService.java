package com.sapo.mock.techshop.service;

import com.sapo.mock.techshop.dto.response.GeneralResponse;

import java.util.List;
import java.util.Map;

public interface DataUserService {

    GeneralResponse createDataUser(Map<String, Object> dataUserRequest);
    GeneralResponse getUser(String id);
    GeneralResponse updateUser(Map<String, Object> request, String id);
    GeneralResponse importUser(List<Map<String, Object>> request);
    GeneralResponse createUserProperty(Map<String, Object> request);
    GeneralResponse deleteUserProperty(String name);
    GeneralResponse getUserProperty(String propertyName);
    GeneralResponse getSpecificProperties();

}
