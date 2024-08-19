/**************************************************************************

 File: AuthenticationResponse.java
 Copyright: (c) 2023 NazImposter
 Description: DTO class for handling authentication response with token .
 Designed by: Pasa Larisa

 Module-History:
 Date        Author                Reason
 21.11.2023  Pasa Larisa           Added refreshToken.
 11.11.2023  Pasa Larisa           Initial creation for managing authentication response.

 **************************************************************************/
package com.paw.project.Components.AccessManagerComponent.Authentication;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationResponse {
   @JsonProperty("access_token")
   private String accessToken;
   @JsonProperty("refresh_token")
   private String refreshToken;
}
