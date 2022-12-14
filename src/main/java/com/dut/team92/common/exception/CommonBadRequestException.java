package com.dut.team92.common.exception;

public class CommonBadRequestException extends RuntimeException{
    private final Integer code;
    private final String message;

    public CommonBadRequestException(Integer code, String message) {
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
