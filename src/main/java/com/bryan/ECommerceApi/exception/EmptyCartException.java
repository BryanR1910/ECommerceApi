package com.bryan.ECommerceApi.exception;

public class EmptyCartException extends RuntimeException {
    public EmptyCartException() {
        super("Cart is empty, cannot proceed with checkout");
    }
}
