package com.example.orderdemo.common.exception.product;

import com.example.orderdemo.common.exception.BusinessException;
import com.example.orderdemo.common.exception.ErrorCode;

public class OutOfStockException extends BusinessException {
    public OutOfStockException(Long productId) {
        super(ErrorCode.OUT_OF_STOCK, "재고가 부족합니다. productId = " + productId);
    }
}
