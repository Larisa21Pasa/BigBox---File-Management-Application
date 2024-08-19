/**************************************************************************

 File:       JsonParseAdvice.java
 Copyright:   (c) 2023 NazImposter
 Description: Advice class for dealing with parsing exceptions.
 Designed by: Sebastian Pitica

 Module-History:
 Date        Author                Reason
 16.11.2023  Sebastian Pitica      Structure with exception dealing class
 23.11.2023  Sebastian Pitica      Added DateTimeParseException
 29.11.2023  Sebastian Pitica      Added description
 30.12.2023  Tudor Toporas         Added logging of error message

 **************************************************************************/

package com.paw.project.Utils.Advices;

import com.fasterxml.jackson.core.JsonParseException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.time.format.DateTimeParseException;

@ControllerAdvice
@Slf4j
public class JsonParseAdvice {

    @ResponseBody
    @ExceptionHandler({HttpMessageNotReadableException.class, JsonParseException.class, DateTimeParseException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)

    public String handler(Exception ex) {
        log.error("Error Message: " + ex.getMessage());
        return "Malformed JSON request: " + ex.getMessage();
    }
}