package com.scaler.capstone.product.controlleradvices;

import com.scaler.capstone.product.dto.ExceptionDTO;
import com.scaler.capstone.product.exceptions.ProductNotExistException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ExceptionHandlers {


    @ExceptionHandler(ProductNotExistException.class)
    public ResponseEntity<ExceptionDTO> handleProductNotExistsException(
            ProductNotExistException exception
    ) {
        ExceptionDTO dto = new ExceptionDTO(HttpStatus.OK,exception.getMessage());
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }
}
