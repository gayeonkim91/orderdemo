package com.example.orderdemo.common.exception.order;

import com.example.orderdemo.common.exception.BusinessException;
import com.example.orderdemo.common.exception.ErrorCode;

public class InvalidOrderException extends BusinessException {
    public InvalidOrderException() {
        super(ErrorCode.INVALID_ORDER);
    }

    public InvalidOrderException(String message) {
        super(ErrorCode.INVALID_ORDER, message);
    }
}
