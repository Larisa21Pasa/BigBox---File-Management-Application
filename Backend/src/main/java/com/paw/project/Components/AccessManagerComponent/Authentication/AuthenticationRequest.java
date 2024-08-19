/**************************************************************************

 File: AuthenticationRequest.java
 Copyright: (c) 2023 NazImposter
 Description: DTO class for handling authentication requests in the Access Manager component.
 Designed by: Pasa Larisa

 Module-History:
 Date        Author                Reason
 11.11.2023  Pasa Larisa           Initial creation for managing authentication requests.
 29.11.2023  Matei Rares           Modify password acces modifier
 12.01.2023  Toporas Tudor         Added useful functions
 **************************************************************************/
package com.paw.project.Components.AccessManagerComponent.Authentication;

import com.paw.project.Utils.Validators.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationRequest {
    @ValidEmail
    private String email;
    @ValidPassword
    private String password;

    public boolean hasNullProperties(){
        return email == null || password == null;
    }

    public boolean hasAllPropertiesValid(){
        return
                new EmailValidator().isValid(email, null) &&
                new PasswordValidator().isValid(password, null);
    }

    public boolean HasInvalidProperties(){
        return !hasAllPropertiesValid();
    }
}
