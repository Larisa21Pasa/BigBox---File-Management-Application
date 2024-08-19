/**************************************************************************

 File:        ConflictAdvice.java
 Copyright:   (c) 2023 NazImposter
 Description: Advice class for dealing with ConflictException.
 Designed by: Sebastian Pitica

 Module-History:
 Date        Author                Reason
 07.11.2023  Sebastian Pitica      Structure with exception dealing class
 29.11.2023  Sebastian Pitica      Added description
 30.12.2023  Tudor Toporas         Added logging of error message

 **************************************************************************/

package com.paw.project.Utils.Advices;

import com.paw.project.Utils.Exceptions.ConflictException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
@Slf4j
public class ConflictAdvice {
    @ResponseBody
    @ExceptionHandler({ConflictException.class})
    @ResponseStatus(HttpStatus.CONFLICT)
    public String handler(ConflictException ex) {
        log.error("Error Message: " + ex.getMessage());
        return ex.getMessage();
    }
}