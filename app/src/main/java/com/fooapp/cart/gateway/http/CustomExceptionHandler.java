package com.fooapp.cart.gateway.http;

import com.fooapp.cart.conf.log.LogKey;
import com.fooapp.cart.gateway.http.jsons.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import static net.logstash.logback.argument.StructuredArguments.value;

@Slf4j
@ControllerAdvice
public class CustomExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    @ResponseBody
    public ErrorResponse processValidationError(IllegalArgumentException e) {
        log.warn("Error processing request {}", e.getMessage(), e);
        return new ErrorResponse(ErrorResponse.ERR_UNPROCESSABLE_ENTITY, e.getMessage());
    }


    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    @ResponseBody
    public ErrorResponse processMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        log.warn("Method not allowed error occurred: {}", e.getMessage(), e);
        return new ErrorResponse(ErrorResponse.ERR_METHOD_NOT_SUPPORTED, e.getMessage());
    }


    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public ErrorResponse processException(Exception e) {
        log.error("An unexpected error occured: {}", value(LogKey.CAUSE.toString(), e.getMessage()), e);
        return new ErrorResponse(ErrorResponse.ERR_INTERNAL_SERVER_ERROR, "Internal server error");
    }
}
