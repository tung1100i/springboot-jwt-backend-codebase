package com.sapo.mock.techshop.common.Utils;

import com.sapo.mock.techshop.common.constant.DataType;
import com.sapo.mock.techshop.common.exception.BusinessException;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

@Log4j2
public class DataUtils {
    public static final String TIMESTAMP_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";
    public static final String REGEX = "^[a-zA-Z0-9_\\-:@.]+$";


    public static Timestamp convertTimestampToString(String input) {
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS Z");
        Date parsedDate = new Date();
        try {
            // Chuyển đổi chuỗi thành đối tượng Date
            parsedDate = inputFormat.parse(input);
        } catch (Exception e) {
            log.error("Error when convert string to timestamp: " + input);
            throw new BusinessException(HttpStatus.BAD_REQUEST.value(), "Error when convert string to timestamp: " + input);
        }
        return new Timestamp(parsedDate.getTime());
    }

    public static boolean checkRegex(String input) {
        return input.matches(REGEX);
    }

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
            case "Integer":
            case "Decimal":
                valueString = String.valueOf(value);
                break;
        }
        return valueString;
    }
}
