/**************************************************************************

 File:        EmailValidator.java
 Copyright:   (c) 2023 NazImposter
 Description: Email validator class.
 Designed by: Sebastian Pitica

 Module-History:
 Date        Author                Reason
 16.11.2023  Sebastian Pitica      Structure and functionality
 29.11.2023  Sebastian Pitica      Added description

 **************************************************************************/

package com.paw.project.Utils.Validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EmailValidator implements ConstraintValidator<ValidEmail, String> {

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        if (s == null) {
            return false;
        } else {
            String pattern = "^[a-zA-Z._-]{1,100}@[a-zA-Z.-]+\\.[a-zA-Z]{2,}$";
            Pattern regex = Pattern.compile(pattern);
            Matcher matcher = regex.matcher(s);
            return matcher.matches();
        }
    }
}