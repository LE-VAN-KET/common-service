package com.dut.team92.common.exception;

import com.dut.team92.common.exception.CommonServerErrorException;
import lombok.Getter;

@Getter
public class RetrievePublicKeyException extends CommonServerErrorException {
    public RetrievePublicKeyException(String message) {
        super(500, message);
    }
}
