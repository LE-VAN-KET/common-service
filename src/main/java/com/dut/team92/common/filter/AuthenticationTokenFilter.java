package com.dut.team92.common.filter;

import com.dut.team92.common.constant.CommonConstant;
import com.dut.team92.common.exception.InvalidTokenHeader;
import com.dut.team92.common.exception.handler.CommonExceptionHandler;
import com.dut.team92.common.exception.model.CommonErrorResponse;
import com.dut.team92.common.security.TokenProvider;
import com.dut.team92.common.util.WebUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

import static net.logstash.logback.argument.StructuredArguments.v;

@Component
@Slf4j
public class AuthenticationTokenFilter extends OncePerRequestFilter {
    @Autowired
    private TokenProvider tokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            log.debug("JwtTokenFilter is calling for uri: {} {}",
                    request.getMethod(), request.getRequestURI());

            String jwt = tokenProvider.parseJwt(request);
            String requestUri = request.getRequestURI();

            if (StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)) {
                Authentication authentication = tokenProvider.parseAuthentication(jwt);
                SecurityContextHolder.getContext().setAuthentication(authentication);

                log.debug("Set Authentication to security context for '{}', uri: {}",
                        authentication.getName(), requestUri);
            }

            filterChain.doFilter(request, response);
        } catch (InvalidTokenHeader invalidTokenHeader) {
            log.error("[Error] | Code: {} | Type: {} | Path: {} | Elapsed time: {} ms | Message: {}",
                    invalidTokenHeader.getCode(), "InvalidTokenHeader", String.join(" ",
                            request.getMethod(), request.getRequestURI()),
                    v("elapsed_time", null), invalidTokenHeader.getMessage());
            CommonErrorResponse commonErrorResponse = new CommonErrorResponse(
                    null,
                    HttpStatus.UNAUTHORIZED,
                    invalidTokenHeader.getMessage());
            response.setContentType("application/json");
            response.setStatus(commonErrorResponse.getCode());
            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(commonErrorResponse);
            response.getWriter().write(json);
        }
    }

}
