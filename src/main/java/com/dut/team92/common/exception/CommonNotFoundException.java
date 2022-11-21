package com.dut.team92.common.exception;

public class CommonNotFoundException extends RuntimeException{
    private final Integer code;
    private final String message;

    public CommonNotFoundException(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
