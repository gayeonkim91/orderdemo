package com.example.orderdemo.common.exception;

public enum ErrorCode {
    INVALID_ORDER("INVALID_ORDER", "잘못된 주문 요청입니다."),
    PRODUCT_NOT_FOUND("PRODUCT_NOT_FOUND", "상품을 찾을 수 없습니다."),
    ORDER_NOT_FOUND("ORDER_NOT_FOUND", "주문을 찾을 수 없습니다."),
    OUT_OF_STOCK("OUT_OF_STOCK", "재고가 부족합니다."),
    INVALID_INPUT("INVALID_INPUT", "입력값이 올바르지 않습니다."),
    INTERNAL_ERROR("INTERNAL_ERROR", "서버 내부 오류가 발생했습니다.");

    private final String code;
    private final String message;

    ErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
