/**************************************************************************

 File:        ConstraintViolationAdvice.java
 Copyright:   (c) 2023 NazImposter
 Description: Advice class for dealing with unprocessable entity exceptions.
 Designed by: Sebastian Pitica

 Module-History:
 Date        Author                Reason
 16.11.2023  Sebastian Pitica      Structure with exception dealing class
 23.11.2023  Sebastian Pitica      Added TransactionSystemException and SQLIntegrityConstraintViolationException catch, updated structure
 29.11.2023  Sebastian Pitica      Added description
 30.12.2023  Tudor Toporas         Added logging of error message

 **************************************************************************/


package com.paw.project.Utils.Advices;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.sql.SQLIntegrityConstraintViolationException;

import static com.paw.project.Utils.Others.Classes.Utils.getDetailsFromException;


@ControllerAdvice
@Slf4j
public class ConstraintViolationAdvice {
    @ResponseBody
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    @ExceptionHandler({ConstraintViolationException.class, SQLIntegrityConstraintViolationException.class, TransactionSystemException.class})
    public String handler(Exception ex) {

        if (ex instanceof TransactionSystemException transactionSystemException) {
            return "Constraint validation error: " + ex.getMessage() + ".\n" + getDetailsFromException(transactionSystemException);
        }

        log.error("Error Message: " + ex.getMessage());
        return "Constraint validation error: " + ex.getMessage();
    }
}


