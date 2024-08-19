/**************************************************************************

 File:        UnprocessableContentException.java
 Copyright:   (c) 2023 NazImposter
 Description: Exception class for UnprocessableContentException.
 Designed by: Larisa Pasa

 Module-History:
 Date        Author                Reason
 11.12.2023  Pasa Larisa      Structure
 **************************************************************************/
package com.paw.project.Utils.Exceptions;

import static com.paw.project.Utils.Others.Classes.AccessManagerMacros.UNPROCESSABLE_CONTENT_EXCEPTION_FIELDS;

public class UnprocessableContentException extends RuntimeException {
    public UnprocessableContentException() {
        super(UNPROCESSABLE_CONTENT_EXCEPTION_FIELDS);
    }

    public UnprocessableContentException(String message) {
        super(message);
    }
}