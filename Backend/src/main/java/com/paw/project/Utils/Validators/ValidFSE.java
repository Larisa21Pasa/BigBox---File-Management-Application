/**************************************************************************

 File:        ValidFSE.java
 Copyright:   (c) 2023 NazImposter
 Description: FileSystemEntry name validator annotation interface.
 Designed by: Toporas Tudor

 Module-History:
 Date        Author                Reason
 12.01.2024  Toporas Tudor     Structure and functionality

 **************************************************************************/

package com.paw.project.Utils.Validators;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = FSEntryValidator.class)
public @interface ValidFSE {
    String message() default "Name of entry can have any character maximum 100 total";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
