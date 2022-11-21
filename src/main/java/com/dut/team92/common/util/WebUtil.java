package com.dut.team92.common.util;

import com.dut.team92.common.constant.CommonConstant;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.util.ContentCachingRequestWrapper;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

@Component
public class WebUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(WebUtil.class);

    @Autowired
    private HttpServletRequest httpServletRequest;

    public String getRequestId() {
        return Optional.ofNullable(httpServletRequest)
                .map(req -> String.valueOf(req.getAttribute(CommonConstant.HttpAttribute.REQUEST_ID)))
                .orElse(null);
    }

    public String getRequestUri() {
        return Optional.ofNullable(httpServletRequest)
                .map(req -> req.getMethod() + " " + req.getRequestURI())
                .orElse("");
    }

    public Long getElapsedTime() {
        return Optional.ofNullable(httpServletRequest)
                .map(req -> String.valueOf(req.getAttribute(CommonConstant.HttpAttribute.ELAPSED_TIME)))
                .filter(StringUtils::isNumeric)
                .map(t -> System.currentTimeMillis() - Long.parseLong(t))
                .orElse(-1L);
    }

}
