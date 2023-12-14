package com.sapo.mock.techshop.service;

import com.sapo.mock.techshop.dto.response.GeneralResponse;

import java.util.List;
import java.util.Map;

public interface DataItemService {
    GeneralResponse<?> createDataItem(Map<String, Object> dataItemRequest);
    GeneralResponse<?> update(Map<String, Object> dataItemRequest, String id);
    GeneralResponse<?> getById(String id);

    GeneralResponse<?> bulkInsert(List<Map<String, Object>> data);

}
