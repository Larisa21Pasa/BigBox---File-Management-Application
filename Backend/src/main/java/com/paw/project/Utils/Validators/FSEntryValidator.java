/**************************************************************************

 File:        FSEntryValidator.java
 Copyright:   (c) 2023 NazImposter
 Description: FileSystemEntry name validator
 Designed by: Toporas Tudor

 Module-History:
 Date        Author                Reason
 16.11.2023  Toporas Tudor      Structure and functionality

 **************************************************************************/

package com.paw.project.Utils.Validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FSEntryValidator implements ConstraintValidator<ValidFSE, String> {
    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        if (s == null) {
            return false;
        } else {
            String pattern = "^.{1,100}$";
            Pattern regex = Pattern.compile(pattern);
            Matcher matcher = regex.matcher(s);
            return matcher.matches();
        }
    }
}
