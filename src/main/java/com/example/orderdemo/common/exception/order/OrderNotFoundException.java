package com.example.orderdemo.common.exception.order;

import com.example.orderdemo.common.exception.BusinessException;
import com.example.orderdemo.common.exception.ErrorCode;

public class OrderNotFoundException extends BusinessException {
    public OrderNotFoundException(Long orderId) {
        super(ErrorCode.INVALID_INPUT, "주문을 찾을 수 없습니다. orderId = " + orderId);
    }
}
