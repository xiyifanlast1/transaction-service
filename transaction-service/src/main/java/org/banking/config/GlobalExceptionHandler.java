package org.banking.config;

import lombok.extern.slf4j.Slf4j;
import org.banking.infra.base.ServiceResult;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ServiceResult<Object>> handleIllegalArgumentException(IllegalArgumentException ex) {
        var errorMessage="input invalid: "+ex.getMessage();
        var errorResponse= new ServiceResult<>()
                .setCode(1)
                .setMessage(errorMessage);
        log.error(errorMessage,ex);
        return ResponseEntity.status(HttpStatus.OK).body(errorResponse);
    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ServiceResult<Object>> handleAllException(Exception ex) {
        var errorMessage="an unexpected error occured: "+ex.getMessage();
        var errorResponse= new ServiceResult<>()
                .setCode(1)
                .setMessage(errorMessage);
        log.error(errorMessage,ex);
        return ResponseEntity.status(HttpStatus.OK).body(errorResponse);
    }
}
