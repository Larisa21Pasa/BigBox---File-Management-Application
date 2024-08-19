/**************************************************************************

 File:        ChangePasswordException.java
 Copyright:   (c) 2023 NazImposter
 Description: Exception class for ChangePasswordException.
 Designed by: Larisa Pasa

 Module-History:
 Date        Author                Reason
 11.12.2023  Pasa Larisa      Structure
 **************************************************************************/
package com.paw.project.Utils.Exceptions;


public class ChangePasswordException extends RuntimeException{
    public ChangePasswordException(String message) {
        super(message);
    }
}