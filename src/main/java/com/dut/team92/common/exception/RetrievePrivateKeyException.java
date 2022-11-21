package com.dut.team92.common.exception;

import com.dut.team92.common.exception.CommonServerErrorException;
import lombok.Getter;

@Getter
public class RetrievePrivateKeyException extends CommonServerErrorException {

    public RetrievePrivateKeyException(String message) {
        super(500, message);
    }
}
