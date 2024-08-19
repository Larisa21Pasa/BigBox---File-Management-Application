/**************************************************************************

 File:        IDMNotFoundAdvice.java
 Copyright:   (c) 2023 NazImposter
 Description:
 Designed by: Matei Rares

 Module-History:
 Date        Author                Reason
 29.11.2023  Matei Rares           Initial creation for managing UsernameNotFoundException.
 30.12.2023  Tudor Toporas         Added logging of error message

 **************************************************************************/

package com.paw.project.Utils.Advices;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
@Slf4j
public class IDMNotFoundAdvice{
    @ResponseBody
    @ExceptionHandler({UsernameNotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<?> handleException(UsernameNotFoundException ex) {
        log.error("Error Message: " + ex.getMessage());
        return new ResponseEntity("IDMNotFoundAdvice: "+ex.getMessage() + " "+ex.getStackTrace()  , HttpStatus.NOT_FOUND);
    }
}
