/**************************************************************************

 File:        JWTService.java
 Copyright:   (c) 2023 NazImposter
 Description:  Service for JWT handling, including token extraction, generation, and validation.
 Designed by:  Larisa Pasa

 Module-History:
 Date        Author                Reason
 21.11.2023  Larisa Pasa           Refactor JWTService.

 **************************************************************************/


package com.paw.project.Components.AccessManagerComponent.Services.JWT;

import io.jsonwebtoken.Claims;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Map;
import java.util.function.Function;

public interface JWTService {

     String extractUserEmail(String token);
     <T> T extractClaim(String token, Function<Claims, T> claimResolver);
     String generateToken(UserDetails userDetails);
     String generateToken(Map<String, Object> extraClaims, UserDetails userDetails);
     String generateRefreshToken(UserDetails userDetails);
      boolean isTokenValid(String token, UserDetails userDetails);

}
