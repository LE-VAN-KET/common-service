package com.dut.team92.common.exception.handler;

import com.dut.team92.common.exception.*;
import com.dut.team92.common.exception.model.CommonErrorResponse;
import com.dut.team92.common.util.MessageSourceUtil;
import com.dut.team92.common.util.WebUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import javax.persistence.RollbackException;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.Optional;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.joining;
import static net.logstash.logback.argument.StructuredArguments.v;

@RestControllerAdvice
public class CommonExceptionHandler {
    private static final Logger log = (Logger) LogManager.getLogger(CommonExceptionHandler.class);
    @Autowired
    private MessageSourceUtil messageSourceUtil;

    @Autowired
    private WebUtil webUtil;

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    protected ResponseEntity<CommonErrorResponse> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  WebRequest request) {
        Optional<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(x -> String.format("'%s' %s", x.getField(), messageSourceUtil.getMessage(x.getDefaultMessage())
                ))
                .collect(collectingAndThen(joining("; "), Optional::ofNullable));


        addErrorLog(HttpStatus.BAD_REQUEST, errors.orElse(ex.getMessage()), "MethodArgumentNotValidException");
        CommonErrorResponse commonErrorResponse = new CommonErrorResponse(
                webUtil.getRequestId(),
                HttpStatus.BAD_REQUEST,
                errors.orElse(ex.getMessage()));
        return new ResponseEntity<>(commonErrorResponse, HttpStatus.BAD_REQUEST);
    }

    /*** Logging ***/
    protected void addErrorLog(HttpStatus httpStatus, String errorMessage, Throwable ex) {
        addErrorLog(httpStatus.value(), errorMessage, ex);
    }

    protected void addErrorLog(HttpStatus httpStatus, String errorMessage, String exceptionType) {
        addErrorLog(httpStatus.value(), errorMessage, exceptionType);
    }

    protected void addErrorLog(Integer errorCode, String errorMessage, Throwable ex) {
        log.error("[Error] | Code: {} | Type: {} | Path: {} | Elapsed time: {} ms | Message: {}",
                errorCode, ex.getClass().getTypeName(), webUtil.getRequestUri(),
                v("elapsed_time", webUtil.getElapsedTime()), errorMessage, ex);
    }

    protected void addErrorLog(Integer errorCode, String errorMessage, String exceptionType) {
        log.error("[Error] | Code: {} | Type: {} | Path: {} | Elapsed time: {} ms | Message: {}",
                errorCode, exceptionType, webUtil.getRequestUri(),
                v("elapsed_time", webUtil.getElapsedTime()), errorMessage);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected CommonErrorResponse handleBadRequestException(Exception ex, String exceptionType) {
        addErrorLog(HttpStatus.BAD_REQUEST, ex.getMessage(), exceptionType);
        return new CommonErrorResponse(
                webUtil.getRequestId(),
                HttpStatus.BAD_REQUEST,
                ex.getMessage());
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    protected CommonErrorResponse handleServerErrorException(Exception ex, String exceptionType) {
        addErrorLog(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(), exceptionType);
        return new CommonErrorResponse(
                webUtil.getRequestId(),
                HttpStatus.INTERNAL_SERVER_ERROR,
                ex.getMessage());
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(value = {CommonAuthException.class})
    public CommonErrorResponse handleCommonAuthException(CommonAuthException ex) {
        addErrorLog(HttpStatus.UNAUTHORIZED, ex.getMessage(), "CommonAuthException");
        return new CommonErrorResponse(
                webUtil.getRequestId(),
                HttpStatus.UNAUTHORIZED,
                ex.getMessage());
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(value = {InvalidTokenHeader.class})
    public CommonErrorResponse handleCommonAuthException(InvalidTokenHeader ex) {
        addErrorLog(HttpStatus.UNAUTHORIZED, ex.getMessage(), "InvalidTokenHeader");
        return new CommonErrorResponse(
                webUtil.getRequestId(),
                HttpStatus.UNAUTHORIZED,
                ex.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ConstraintViolationException.class)
    public CommonErrorResponse handleConstraintViolationException(ConstraintViolationException ex) {

        Optional<String> violations = ex.getConstraintViolations()
                .stream()
                .map(v -> v.getPropertyPath() + ": " + messageSourceUtil.getMessage(v.getMessage()))
                .collect(collectingAndThen(joining("; "), Optional::ofNullable));

        String errMsg = violations.orElse(ex.getMessage());
        addErrorLog(HttpStatus.BAD_REQUEST, errMsg, "ConstraintViolationException");
        return new CommonErrorResponse(
                webUtil.getRequestId(),
                HttpStatus.BAD_REQUEST,
                errMsg);
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public CommonErrorResponse handleAll(Exception ex) {
        addErrorLog(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(), ex);
        String errMsg = "Unexpected internal server error occurs";
        return new CommonErrorResponse(
                webUtil.getRequestId(),
                HttpStatus.INTERNAL_SERVER_ERROR,
                errMsg);
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(LoadKeyStoreTokenException.class)
    public CommonErrorResponse handleLoadKeystoreTokenException(LoadKeyStoreTokenException exception) {
        return handleServerErrorException(exception, "LoadKeyStoreTokenException");
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(RetrievePublicKeyException.class)
    public CommonErrorResponse handleRetrievePublicKeyException(RetrievePublicKeyException exception) {
        return handleServerErrorException(exception, "RetrievePublicKeyException");
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(RetrievePrivateKeyException.class)
    public CommonErrorResponse handleRetrievePrivateKeyException(RetrievePrivateKeyException exception) {
        return handleServerErrorException(exception, "RetrievePrivateKeyException");
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(value = {CommonNotFoundException.class})
    public CommonErrorResponse handleCommonNotFoundException(CommonNotFoundException ex) {
        addErrorLog(HttpStatus.NOT_FOUND, ex.getMessage(), "CommonNotFoundException");
        return new CommonErrorResponse(
                webUtil.getRequestId(),
                HttpStatus.NOT_FOUND,
                ex.getMessage());
    }

    @ExceptionHandler(TransactionSystemException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected CommonErrorResponse handleTransactionException(TransactionSystemException ex) throws Throwable {
        Throwable cause = ex.getCause();
        if (!(cause instanceof RollbackException))
            throw cause;
        if (!(cause.getCause() instanceof ConstraintViolationException))
            throw cause.getCause();
        ConstraintViolationException validationException = (ConstraintViolationException) cause.getCause();
        Optional<String> errors = validationException.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage)
                .collect(collectingAndThen(joining("; "), Optional::ofNullable));
        addErrorLog(HttpStatus.BAD_REQUEST, errors.orElse(ex.getMessage()), "ConstraintViolationException");
        return new CommonErrorResponse(
                webUtil.getRequestId(),
                HttpStatus.BAD_REQUEST,
                errors.orElse(ex.getMessage()));
    }

}
