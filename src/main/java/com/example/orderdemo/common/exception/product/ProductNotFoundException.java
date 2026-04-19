package com.example.orderdemo.common.exception.product;

import com.example.orderdemo.common.exception.BusinessException;
import com.example.orderdemo.common.exception.ErrorCode;

import java.util.List;

public class ProductNotFoundException extends BusinessException {
    public ProductNotFoundException(List<Long> productIdList) {
        super(ErrorCode.PRODUCT_NOT_FOUND, "상품을 찾을 수 없습니다. productIds = " + productIdList.toString());
    }
}
