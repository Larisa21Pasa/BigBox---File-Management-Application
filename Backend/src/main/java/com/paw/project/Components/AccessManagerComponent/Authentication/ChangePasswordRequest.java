/**************************************************************************

 File: ChangePasswordRequest.java
 Copyright: (c) 2023 NazImposter
 Description: DTO class for handling change password requests in the Access Manager component.
 Designed by: Pasa Larisa

 Module-History:
 Date        Author                Reason
 11.12.2023  Pasa Larisa           Initial creation for managing changing password requests.
 **************************************************************************/
package com.paw.project.Components.AccessManagerComponent.Authentication;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;

@Getter
@Setter
@NoArgsConstructor
public class ChangePasswordRequest {

    private String currentPassword;
    private String newPassword;
    private String confirmationPassword;
}
