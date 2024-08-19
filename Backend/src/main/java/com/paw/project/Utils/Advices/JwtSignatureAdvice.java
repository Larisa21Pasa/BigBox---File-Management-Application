/**************************************************************************

 File:        NotAcceptableAdvice.java
 Copyright:   (c) 2023 NazImposter
 Description:
 Designed by: Larisa Pasa

 Module-History:
 Date        Author                Reason
 22.12.2023  Larisa Pasa           Added JwtSignatureAdvice advice handler.
 **************************************************************************/
package com.paw.project.Utils.Advices;

import com.paw.project.Utils.Exceptions.JwtSignatureException;
import com.paw.project.Utils.Exceptions.NotAcceptableException;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;


@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
@Log4j2
public class JwtSignatureAdvice {

    @ResponseBody
    @ExceptionHandler({JwtSignatureException.class})
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseEntity<?> handler(JwtSignatureException ex) {
        log.info("JwtSignatureAdvice () ");

        return new ResponseEntity<>(ex.getMessage(), HttpStatus.UNAUTHORIZED);
    }

}