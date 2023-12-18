package com.sapo.mock.techshop.common.constant;

import java.util.HashMap;
import java.util.Map;

public enum DataType {


    // Text=text, TextSet=text, Image=text, ImageSet=text, Integer=bigInt, Decimal=double precision, Timestamp=timestamp, Boolean=boolean
    TEXT("Text", "text"),
    TEXT_SET("TextSet", "text"),
    IMAGE("Image", "text"),
    IMAGE_SET("ImageSet", "text"),
    INTEGER("Integer", "bigint"),
    DECIMAL("Decimal", "double precision"),
    TIMESTAMP("Timestamp", "timestamp"),
    BOOLEAN("Decimal", "boolean"),
    ;

    private final String key;
    private final String value;

    private static final Map<String, String> map = new HashMap<>();
    private static final Map<String, String> reversetmap = new HashMap<>();

    DataType(String key, String value) {
        this.key = key;
        this.value = value;
    }

    static {
        for (DataType bonusType : DataType.values()) {
            map.put(bonusType.key, bonusType.value);
            reversetmap.put(bonusType.value, bonusType.key);
        }
    }

    public static String getValueOf(String key) {
        return map.get(key);
    }

    public static String getKeyOf(String value) {
        return reversetmap.get(value);
    }

    public static boolean validType(String type) {
        return map.containsKey(type);
    }


}