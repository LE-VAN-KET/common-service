package com.dut.team92.common.exception;

import com.dut.team92.common.exception.CommonServerErrorException;
import lombok.Getter;

@Getter
public class LoadKeyStoreTokenException extends CommonServerErrorException {

    public LoadKeyStoreTokenException(String message) {
        super(500, message);
    }
}
