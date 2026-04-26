package com.example.orderdemo.api.error;

import com.example.orderdemo.common.exception.ErrorCode;
import org.springframework.http.HttpStatus;

public final class ErrorCodeHttpStatusMapper {
    private ErrorCodeHttpStatusMapper() {}

    public static HttpStatus map(ErrorCode errorCode) {
        return switch(errorCode) {
            case INVALID_ORDER, INVALID_INPUT -> HttpStatus.BAD_REQUEST;
            case ORDER_NOT_FOUND, PRODUCT_NOT_FOUND -> HttpStatus.NOT_FOUND;
            case OUT_OF_STOCK, CONCURRENT_ORDER -> HttpStatus.CONFLICT;
            default -> HttpStatus.INTERNAL_SERVER_ERROR;
        };
    }
}
