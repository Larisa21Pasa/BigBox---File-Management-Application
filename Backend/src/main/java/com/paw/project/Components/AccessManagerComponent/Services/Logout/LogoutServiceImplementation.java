/**************************************************************************

 File:        LogoutServiceImplementation.java
 Copyright:   (c) 2023 NazImposter
 Description:  Service for JWT handling, including token extraction, generation, and validation.
 Designed by:  Larisa Pasa

 Module-History:
 Date        Author                Reason
 21.11.2023  Larisa Pasa           Added logout logic for user with valid token.

 **************************************************************************/
package com.paw.project.Components.AccessManagerComponent.Services.Logout;

import com.paw.project.Utils.Repositories.TokenRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class LogoutServiceImplementation implements  LogoutService{
    private final TokenRepository tokenRepository;
    /**
     * Handles user logout by invalidating the provided JWT token.
     *
     * @param request        The HttpServletRequest.
     * @param response       The HttpServletResponse.
     * @param authentication The authentication object containing user details.
     */
    @Override
    public void logout(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) {
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        log.info("logout service cu authheader {} ", authHeader);
        if (authHeader == null ||!authHeader.startsWith("Bearer ")) {
            log.info("authHeadernull sau nu incepe cu bearer = {}", authHeader);
            return;
        }
        jwt = authHeader.substring(7);
        log.info("jwt {}", jwt);
        var storedToken = tokenRepository.findByToken(jwt)
                .orElse(null);
        log.info("storedToken {}", storedToken);
        if (storedToken != null) {
            storedToken.setExpired(true);
            storedToken.setRevoked(true);
            tokenRepository.save(storedToken);
            SecurityContextHolder.clearContext();
        }
    }
}
