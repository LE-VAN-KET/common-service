package com.dut.team92.common.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

@Component
public class MessageSourceUtil {
    @Autowired
    private MessageSource messageSource;

    public String getMessage(String key, Object[] arg) {
        try {
            return messageSource.getMessage(key, arg, LocaleContextHolder.getLocale());
        } catch (NoSuchMessageException ex) {
            return key;
        }
    }

    public String getMessage(String messageCode) {
        return getMessage(messageCode, null);
    }
}
