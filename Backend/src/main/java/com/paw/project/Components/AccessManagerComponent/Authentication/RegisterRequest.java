/**************************************************************************

 File: RegisterRequest.java
 Copyright: (c) 2023 NazImposter
 Description: DTO class for handling registration requests in the Access Manager component.
 Designed by: Pasa Larisa

 Module-History:
 Date        Author                Reason
 02.12.2023  Pasa Larisa           Changed roles management: keeping RoleEnum for admin creation in maintenance.
 21.11.2023  Pasa Larisa           Changed roles management : from DB to Enum.
 11.11.2023  Pasa Larisa           Initial creation for managing registration requests.
 12.01.2023  Toporas Tudor         Added useful functions

 **************************************************************************/
package com.paw.project.Components.AccessManagerComponent.Authentication;

import com.paw.project.Utils.Models.Enums.RoleEnum;
import com.paw.project.Utils.Validators.EmailValidator;
import com.paw.project.Utils.Validators.PasswordValidator;
import com.paw.project.Utils.Validators.StringValidator;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {

    private String name;
    private String email;
    private String password;
    private RoleEnum role;

    public boolean hasNullProperties(){
        return name == null || email == null || password == null || role == null;
    }

    public void setRoleIfNull(RoleEnum role){
        if(this.role == null){
            this.role = role;
        }
    }

    public boolean hasAllPropertiesValid(){
        return
                new EmailValidator().isValid(email, null) &&
                new PasswordValidator().isValid(password, null) &&
                new StringValidator().isValid(name, null);
    }

    public boolean HasInvalidProperties(){
        return !hasAllPropertiesValid();
    }

}
