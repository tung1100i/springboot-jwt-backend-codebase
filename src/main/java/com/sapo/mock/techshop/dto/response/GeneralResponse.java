package com.sapo.mock.techshop.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.sapo.mock.techshop.common.constant.HttpStatusConstant;
import lombok.*;
import org.springframework.http.HttpStatus;

import java.io.Serializable;

@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GeneralResponse<T> implements Serializable {
    private int statusCode;
    private String message;
    private T data;
    private int total;

    public static <T> GeneralResponse<T> ok() {
        return GeneralResponse.<T>builder()
                .statusCode(HttpStatus.OK.value())
                .message(HttpStatus.OK.getReasonPhrase())
                .build();
    }

    public static <T> GeneralResponse<T> ok(T data) {
        return GeneralResponse.<T>builder()
                .statusCode(HttpStatus.OK.value())
                .message(HttpStatus.OK.getReasonPhrase())
                .data(data)
                .build();
    }

    public static <T> GeneralResponse<T> ok(T data, int total) {
        return GeneralResponse.<T>builder()
                .statusCode(HttpStatus.OK.value())
                .message(HttpStatus.OK.getReasonPhrase())
                .data(data)
                .total(total)
                .build();
    }

    public static <T> GeneralResponse<T> error() {
        return GeneralResponse.<T>builder()
                .statusCode(HttpStatus.OK.value())
                .message(HttpStatusConstant.UNAVAILABLE_MESSAGE)
                .build();
    }

    public static <T> GeneralResponse<T> error(int statusCode, String message) {
        return GeneralResponse.<T>builder()
                .statusCode(statusCode)
                .message(message).build();
    }

    public static <T> GeneralResponse<T> error(HttpStatus code) {
        return GeneralResponse.<T>builder()
                .statusCode(code.value())
                .message(code.getReasonPhrase()).build();
    }
}