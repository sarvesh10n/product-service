package com.scaler.capstone.product.advices;

import com.scaler.capstone.product.dto.ExceptionDTO;
import com.scaler.capstone.product.exceptions.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class ControllerAdvices {

    @ExceptionHandler(RuntimeException.class)
    ResponseEntity<ExceptionDTO> handleRuntimeException(RuntimeException ex){
        ExceptionDTO exceptionDto = new ExceptionDTO(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
        return new ResponseEntity<>(exceptionDto, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(ProductNotExistException.class)
    ResponseEntity<ExceptionDTO> handleNotFoundException(ProductNotExistException ex){
        ExceptionDTO exceptionDto = new ExceptionDTO(HttpStatus.NOT_FOUND, ex.getMessage());
        return new ResponseEntity<>(exceptionDto, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(InvalidDataException.class)
    ResponseEntity<ExceptionDTO> handleInvalidException(InvalidDataException ex){
        ExceptionDTO exceptionDto = new ExceptionDTO(HttpStatus.BAD_REQUEST, ex.getMessage());
        return new ResponseEntity<>(exceptionDto, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InsufficientStockException.class)
    ResponseEntity<ExceptionDTO> handleInsufficientException(InsufficientStockException ex){
        ExceptionDTO exceptionDto = new ExceptionDTO(HttpStatus.BAD_REQUEST, ex.getMessage());
        return new ResponseEntity<>(exceptionDto, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ResourceAccessForbiddenException.class)
    ResponseEntity<ExceptionDTO> handleForbiddenException(ResourceAccessForbiddenException ex){
        ExceptionDTO exceptionDto = new ExceptionDTO(HttpStatus.FORBIDDEN, ex.getMessage());
        return new ResponseEntity<>(exceptionDto, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(PaymentClientException.class)
    ResponseEntity<ExceptionDTO> handlePaymentClientException(PaymentClientException ex){
        ExceptionDTO exceptionDto = new ExceptionDTO(HttpStatus.BAD_REQUEST, ex.getMessage());
        return new ResponseEntity<>(exceptionDto, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(CategoryNotExistException.class)
    ResponseEntity<ExceptionDTO> handleCategoryNotExistException(CategoryNotExistException ex){
        ExceptionDTO exceptionDto = new ExceptionDTO(HttpStatus.BAD_REQUEST, ex.getMessage());
        return new ResponseEntity<>(exceptionDto, HttpStatus.BAD_REQUEST);
    }




    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }
}