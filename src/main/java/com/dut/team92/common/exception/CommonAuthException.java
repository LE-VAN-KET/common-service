package com.dut.team92.common.exception;

public class CommonAuthException extends RuntimeException {
    private final Integer code;
    private final String message;

    public CommonAuthException(Integer code, String message) {
        super(message);
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
