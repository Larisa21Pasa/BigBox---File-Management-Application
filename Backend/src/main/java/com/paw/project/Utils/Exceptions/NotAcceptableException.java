/**************************************************************************

 File:        NotAcceptableException.java
 Copyright:   (c) 2023 NazImposter
 Description: Exception class for NotAcceptableException.
 Designed by: Larisa Pasa

 Module-History:
 Date        Author                Reason
 11.12.2023  Pasa Larisa      Structure
 **************************************************************************/
package com.paw.project.Utils.Exceptions;


public class NotAcceptableException extends RuntimeException{
    public NotAcceptableException(String message) {
        super(message);
    }
}

