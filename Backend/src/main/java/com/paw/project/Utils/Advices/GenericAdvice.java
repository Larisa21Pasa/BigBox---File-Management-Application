/**************************************************************************

 File:        Exception.java
 Copyright:   (c) 2023 NazImposter
 Description:
 Designed by: Matei Rares

 Module-History:
 Date        Author                Reason
 22.12.2023  Larisa Pasa           Added handling for token exceptions.
 10.12.2023  Larisa Pasa           Added handling for various exception for 401 & 403.
 29.11.2023  Matei Rares           Initial creation for managing Exception.
 30.12.2023  Tudor Toporas         Added logging of error message

 **************************************************************************/

package com.paw.project.Utils.Advices;

import io.jsonwebtoken.ExpiredJwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.web.csrf.CsrfException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.server.MethodNotAllowedException;

@ControllerAdvice
@Slf4j
public class GenericAdvice {

    @ResponseBody
    @ExceptionHandler({BadCredentialsException.class, DisabledException.class, AccountExpiredException.class,  CredentialsExpiredException.class})
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseEntity<?> handleBadCredentialsException(BadCredentialsException ex) {
        log.error("Error Message: " + ex.getMessage());
        return new ResponseEntity<>("Authentication failed: " + ex.getMessage(), HttpStatus.UNAUTHORIZED);
    }
    @ResponseBody
    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ResponseEntity<?> handleAccessDeniedException(AccessDeniedException ex) {
        log.error("Error Message: " + ex.getMessage());
        return new ResponseEntity<>("Access denied: " + ex.getMessage(), HttpStatus.FORBIDDEN);
    }
    @ExceptionHandler(MethodNotAllowedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ResponseEntity<?> handleMethodNotAllowedException(MethodNotAllowedException ex) {
        log.error("Error Message: " + ex.getMessage());
        return new ResponseEntity<>("Method not allowed: " + ex.getMessage(), HttpStatus.FORBIDDEN);
    }
    @ExceptionHandler(CsrfException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ResponseEntity<?> handleCsrfException(CsrfException ex) {
        log.error("Error Message: " + ex.getMessage());
        return new ResponseEntity<>("CSRF validation failed: " + ex.getMessage(), HttpStatus.FORBIDDEN);
    }
    @ExceptionHandler(ExpiredJwtException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ResponseEntity<?> handleExpiredJwtException(ExpiredJwtException ex) {
        log.error("Error Message: " + ex.getMessage());
        return new ResponseEntity<>("Token expired: " + ex.getMessage(), HttpStatus.FORBIDDEN);
    }
/*    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<?> handleExpiredJwtException(Exception ex) {
        return new ResponseEntity<>("ERROR " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }*/
}
