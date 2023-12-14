package com.sapo.mock.techshop.common.exception;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
public class BusinessException extends RuntimeException {
    private int code;
    private String message;

    public BusinessException(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
