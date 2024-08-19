/**************************************************************************

 File:        NameValidator.java
 Copyright:   (c) 2023 NazImposter
 Description: Validator for name fields.
 Designed by: Sebastian Pitica

 Module-History:
 Date        Author                Reason
 07.11.2023  Sebastian Pitica      Structure and functionality
 29.11.2023  Sebastian Pitica      Added description

 **************************************************************************/

package com.paw.project.Utils.Validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NameValidator implements ConstraintValidator<ValidName, String> {

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        if (s == null) {
            return false;
        } else {
            String pattern = "^[a-zA-Z.]{1,20}$";
            Pattern regex = Pattern.compile(pattern);
            Matcher matcher = regex.matcher(s);
            return matcher.matches();
        }
    }
}