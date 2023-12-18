package com.sapo.mock.techshop.service;

import com.sapo.mock.techshop.dto.response.GeneralResponse;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public interface ItemService {
    GeneralResponse<?> createItem(Map<String, Object> itemRequest);

    GeneralResponse<?> updateItem(Map<String, Object> itemRequest, String itemId);

    GeneralResponse<?> getItem(String item_id);

    GeneralResponse<?> bulkImportItem(List<Map<String, Object>> itemRequest);

    GeneralResponse<?> createItemProperty(Map<String, Object> request);

    GeneralResponse<?> deleteItemProperty(String name);

    GeneralResponse<?> getItemProperty(String name);

    GeneralResponse<?> getAllItemProperty();
}
