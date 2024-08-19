/**************************************************************************

 File:        UnprocessableContentAdvice.java
 Copyright:   (c) 2023 NazImposter
 Description:
 Designed by: Larisa Pasa

 Module-History:
 Date        Author                Reason
 10.12.2023  Larisa Pasa           Added header for class
 30.12.2023  Tudor Toporas         Added logging of error message
 **************************************************************************/
package com.paw.project.Utils.Advices;

import com.paw.project.Utils.Exceptions.UnprocessableContentException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
@Slf4j
public class UnprocessableContentAdvice {
    @ResponseBody
    @ExceptionHandler({UnprocessableContentException.class})
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public String handler(UnprocessableContentException ex) {
        log.error("Error Message: " + ex.getMessage());
        return ex.getMessage();
    }
}


