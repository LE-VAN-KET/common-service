package com.dut.team92.common.exception.model;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class CommonErrorResponse {
    private String id;
    private Integer code;
    private String message;

    public CommonErrorResponse(String id, Integer code, String message) {
        this.id = id;
        this.code = code;
        this.message = message;
    }

    public CommonErrorResponse(String id, HttpStatus status, String message) {
        this.id = id;
        this.code = status.value();
        this.message = message;
    }

    public CommonErrorResponse() {
    }
}
