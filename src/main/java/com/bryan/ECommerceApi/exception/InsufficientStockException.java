package com.bryan.ECommerceApi.exception;

public class InsufficientStockException extends RuntimeException {
    public InsufficientStockException(String productName) {
        super("Not enough stock for product: " + productName);
    }
}
