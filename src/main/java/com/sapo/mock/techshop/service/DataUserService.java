package com.sapo.mock.techshop.service;

import com.sapo.mock.techshop.dto.response.GeneralResponse;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;

public interface DataUserService {

    GeneralResponse<?> createDataUser(Map<String, Object> dataUserRequest);
    GeneralResponse<?> getUser(String id);
    GeneralResponse<?> updateUser(Map<String, Object> request, String id);
    GeneralResponse<?> bulkInsert(List<Map<String, Object>> request);

}
