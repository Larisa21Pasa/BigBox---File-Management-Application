/**************************************************************************

 File:        InternalServerException.java
 Copyright:   (c) 2023 NazImposter
 Description: Exception class for InternalServerException.
 Designed by: Sebastian Pitica

 Module-History:
 Date        Author                Reason
 22.12.2023  Pasa Larisa          Added custom exception handler for SignatureException.

 **************************************************************************/


package com.paw.project.Utils.Exceptions;

import io.jsonwebtoken.security.SignatureException;


public class JwtSignatureException extends SignatureException {
    public JwtSignatureException(String message) {
        super(message);
    }
}

