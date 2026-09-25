package com.laioffer.onlineorder.exception;

// 用于将“找不到资源”统一映射为 HTTP 404。
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
