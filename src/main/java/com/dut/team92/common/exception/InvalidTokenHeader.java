package com.dut.team92.common.exception;

import lombok.Getter;

@Getter
public class InvalidTokenHeader extends CommonAuthException {
    public InvalidTokenHeader(String message) {
        super(401, message);
    }
}
