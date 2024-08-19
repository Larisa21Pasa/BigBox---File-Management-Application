/**************************************************************************

 File:       Utils.java
 Copyright:   (c) 2023 NazImposter
 Description: Utility class for various operations.
 Designed by: Sebastian Pitica

 Module-History:
 Date        Author                Reason
 23.11.2023  Sebastian Pitica      Basic structure with static methods and @UtilityClass annotation
 29.11.2023  Sebastian Pitica      Added description

 **************************************************************************/


package com.paw.project.Utils.Others.Classes;

import jakarta.validation.ConstraintViolation;
import lombok.experimental.UtilityClass;
import org.springframework.transaction.TransactionSystemException;

import java.util.List;

@UtilityClass
public class Utils {
    public static String getDetailsFromException(TransactionSystemException ex) {
        Throwable rootCause = ex.getRootCause();
        StringBuilder message = new StringBuilder();
        if (rootCause instanceof jakarta.validation.ConstraintViolationException constraintViolationException) {
            List<ConstraintViolation<?>> constraintViolations = constraintViolationException.getConstraintViolations().stream().toList();
            for (ConstraintViolation<?> violation : constraintViolations) {
                message.append(violation.getMessage()).append(":").append(violation.getPropertyPath().toString()).append("\t");
            }
        }
        return message.toString();
    }
}
