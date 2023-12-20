package com.sapo.mock.techshop.Utils;

import com.sapo.mock.techshop.common.constant.DataType;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class DataUtils {
    public static final String REGEX = "^[a-zA-Z0-9_\\-:@.]+$";
    public static final String PROPERTY_NAME_REGEX = "^[a-zA-Z_]\\w*$";



    private DataUtils(){}

    public static String getValue(Object value, String type) {
        String valueString = null;
        switch (DataType.getKeyOf(type)) {
            case "Text":
            case "TextSet":
            case "Image":
            case "ImageSet":
            case "Timestamp":
            case "Boolean":
                valueString = "'" + value + "'";
                break;
            default:
                valueString = String.valueOf(value);
        }
        return valueString;
    }

}
