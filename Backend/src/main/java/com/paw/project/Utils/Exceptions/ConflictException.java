/**************************************************************************

 File:        ConflictException.java
 Copyright:   (c) 2023 NazImposter
 Description: Exception class for ConflictException.
 Designed by: Sebastian Pitica

 Module-History:
 Date        Author                Reason
 07.11.2023  Sebastian Pitica      Structure
 23.11.2023  Sebastian Pitica      StandardException annotation and removed the rest methods
 29.11.2023  Sebastian Pitica      Added description

 **************************************************************************/

package com.paw.project.Utils.Exceptions;

import lombok.experimental.StandardException;

@StandardException
public class ConflictException extends RuntimeException{
}
