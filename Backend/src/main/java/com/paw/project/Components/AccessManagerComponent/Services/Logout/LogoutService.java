/**************************************************************************

 File:        JWTService.java
 Copyright:   (c) 2023 NazImposter
 Description:  Service for JWT handling, including token extraction, generation, and validation.
 Designed by:  Larisa Pasa

 Module-History:
 Date        Author                Reason
 21.11.2023  Larisa Pasa           Created interface for Logout custom service.

 **************************************************************************/
package com.paw.project.Components.AccessManagerComponent.Services.Logout;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;

public interface LogoutService extends LogoutHandler {
    public void logout(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    );
}
