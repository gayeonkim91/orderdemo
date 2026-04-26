package com.example.orderdemo.common.exception.order;

import com.example.orderdemo.common.exception.BusinessException;
import com.example.orderdemo.common.exception.ErrorCode;

public class ConcurrentOrderException extends BusinessException {
    public ConcurrentOrderException() {
        super(ErrorCode.CONCURRENT_ORDER);
    }
}
