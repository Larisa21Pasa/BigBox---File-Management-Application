/**************************************************************************

 File: AuthenticationController.java
 Copyright: (c) 2023 NazImposter
 Description: Controller for managing user authentication and registration via URL mapping.
 Designed by: Pasa Larisa
 Module-History:
 Date        Author                Reason
 22.12.2023  Pasa Larisa          Added try-catch blocks for errors that were not caught by advices.
 11.11.2023  Pasa Larisa          Initial creation of the User Authentication controller.
 28.11.2023  Pasa Larisa          Added logout logic.

 **************************************************************************/
package com.paw.project.Components.AccessManagerComponent.Controllers;

import com.paw.project.Components.AccessManagerComponent.Authentication.AuthenticationRequest;
import com.paw.project.Components.AccessManagerComponent.Authentication.RegisterRequest;
import com.paw.project.Components.AccessManagerComponent.Services.Authentication.AuthenticationService;
import com.paw.project.Components.AccessManagerComponent.Authentication.AuthenticationResponse;
import com.paw.project.Components.AccessManagerComponent.Services.EmailService;
import com.paw.project.Components.AccessManagerComponent.Services.JWT.JWTServiceImplementation;
import com.paw.project.Components.AccessManagerComponent.Services.Logout.LogoutServiceImplementation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Log4j2
public class AuthenticationController {

    private  final AuthenticationService authenticationService;
    private final LogoutServiceImplementation logoutService;

    /**
     * Handles user registration requests.
     *
     * @param registerRequest The registration request payload.
     * @return ResponseEntity containing the authentication response.
     */
    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(
            @RequestBody RegisterRequest registerRequest
    ) {
        log.info("A client try to register {}... ", registerRequest);
        return ResponseEntity.ok(authenticationService.register(registerRequest));
    }

    /**
     * Handles user authentication requests.
     *
     * @param authenticationRequest The authentication request payload.
     * @return ResponseEntity containing the authentication response.
     */
    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(
            @RequestBody AuthenticationRequest authenticationRequest
    )
    {
        log.info("A client try to authenticate {}   ... ", authenticationRequest);
        return  ResponseEntity.ok(authenticationService.authenticate(authenticationRequest));
    }

    @GetMapping("/token/{tokenString}")
    public ResponseEntity<Boolean> getValidityToken(
            @PathVariable String tokenString
    )
    {
        log.info("A client try to check token validity ... ");
        return ResponseEntity.ok(authenticationService.getValidityToken(tokenString));

    }

    /**
     * Handles requests to refresh an access token.
     *
     * @param request  The HttpServletRequest.
     * @param response The HttpServletResponse.
     * @throws IOException If an I/O exception occurs during token refresh.
     */
    @PostMapping("/refresh-token")
    public void refreshToken(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        log.info("Handling token refresh request at {}", new Date());

        try {
            authenticationService.refreshToken(request, response);
            log.info("Token refresh successful");
        } catch (IOException e) {
            log.error("Token refresh failed: {}", e.getMessage());
            throw new IOException("Token refresh failed", e);
        }
    }
    /**
     * Handles user logout requests.
     *
     * @param request        The HttpServletRequest.
     * @param response       The HttpServletResponse.
     * @param authentication The authentication object containing user details.
     */
    @PostMapping("/logout")
    public ResponseEntity<String> logout(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) {
        log.info("A client is logging out...");

        try {
            logoutService.logout(request, response, authentication);
            return ResponseEntity.ok("Logout successful");
        } catch (Exception e) {
            log.error("Logout failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Logout failed");
        }
    }
    @Autowired
    private JavaMailSender javaMailSender;


    @PostMapping("/contact")
    public ResponseEntity<?> contact(@RequestBody String message){
        System.out.println(message);
        String[] lines = message.split("\n");
        for (String line : lines) {
            System.out.println(line);
        }



        SimpleMailMessage messager = new SimpleMailMessage();
        messager.setTo(lines[1]);
        messager.setSubject( "Hello "+lines[0]);
        messager.setText("Thank you for contacting us! We will get back to you as soon as possible.");
        javaMailSender.send(messager);
        return new ResponseEntity<>("It's okay!",HttpStatus.OK);
    }

}
