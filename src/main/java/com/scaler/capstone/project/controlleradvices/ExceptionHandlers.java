package com.scaler.capstone.project.controlleradvices;

import com.scaler.capstone.project.dto.ExceptionDTO;
import com.scaler.capstone.project.exceptions.ProductNotExistException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice //TODO : This class there some methods that may modify what controller is returning
public class ExceptionHandlers {
    @ExceptionHandler(ArithmeticException.class)  //TODO : This related to @ControllerAdvise
    public ResponseEntity<Void> handleArithmeticException(){
        return new ResponseEntity<> (HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(ArrayIndexOutOfBoundsException.class)  //TODO : This related to @ControllerAdvise
    public ResponseEntity<Void> handleArrayIndexOutOfBondException(){
        return new ResponseEntity<> (HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(ProductNotExistException.class)
    public ResponseEntity<ExceptionDTO> handleProductNotExistsException(
            ProductNotExistException exception
    ) {
        ExceptionDTO dto = new ExceptionDTO();
        dto.setMessage(exception.getMessage());

        return new ResponseEntity<>(dto, HttpStatus.OK);
    }
}
