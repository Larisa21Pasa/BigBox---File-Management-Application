/**************************************************************************

 File:        NotAcceptableAdvice.java
 Copyright:   (c) 2023 NazImposter
 Description:
 Designed by: Larisa Pasa

 Module-History:
 Date        Author                Reason
 22.12.2023  Larisa Pasa           Added ChangePasswordException exception.
 10.12.2023  Larisa Pasa           Added header for class
 30.12.2023  Tudor Toporas         Added logging of error message
 **************************************************************************/
package com.paw.project.Utils.Advices;


import com.paw.project.Utils.Exceptions.ChangePasswordException;
import com.paw.project.Utils.Exceptions.NotAcceptableException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
@Slf4j
public class NotAcceptableAdvice {
    @ResponseBody
    @ExceptionHandler({NotAcceptableException.class})
    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    public String handler(NotAcceptableException ex) {
        log.error("Error Message: " + ex.getMessage());
        return ex.getMessage();
    }

    @ResponseBody
    @ExceptionHandler({ChangePasswordException.class})
    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    public String handler(ChangePasswordException ex) {
        log.error("Error Message: " + ex.getMessage());
        return ex.getMessage();
    }

}
