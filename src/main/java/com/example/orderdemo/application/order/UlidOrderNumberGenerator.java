package com.example.orderdemo.application.order;

import com.github.f4b6a3.ulid.UlidCreator;
import org.springframework.stereotype.Component;

@Component
public class UlidOrderNumberGenerator implements OrderNumberGenerator {
    @Override
    public String generate() {
        return "ORDER-" + UlidCreator.getUlid().toString();
    }
}
