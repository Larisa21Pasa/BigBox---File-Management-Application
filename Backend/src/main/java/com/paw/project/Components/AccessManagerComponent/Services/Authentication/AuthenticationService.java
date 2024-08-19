/**************************************************************************

 File:        AuthenticationService.java
 Copyright:   (c) 2023 NazImposter
 Description:Service for Authentication handling, including register and authenticate options.
 Designed by: Pasa Larisa

 Module-History:
 Date        Author                Reason
 11.12.2023  Pasa Larisa           Check user account to not be blocked before register.
 10.12.2023  Pasa Larisa           Added subscription when build user. It is necessary to MyAccount from Angular.
 02.12.2023  Pasa Larisa           Manage cases: ( NOT registered and try login) and (try register and ALREADY registere)
 21.11.2023  Pasa Larisa           Added refresh token logic + change logic for roles managering.
 11.11.2023  Pasa Larisa           Added register & authenticate methods.
 12.01.2023  Toporas Tudor         Added FileManager context creation and refactored auth and register
 16.01.2024  Toporas Tudor         Fixed login

 **************************************************************************/
package com.paw.project.Components.AccessManagerComponent.Services.Authentication;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.paw.project.Components.AccessManagerComponent.Authentication.AuthenticationRequest;
import com.paw.project.Components.AccessManagerComponent.Authentication.RegisterRequest;
import com.paw.project.Components.FileManager.FileManagerService;
import com.paw.project.Utils.Exceptions.ConflictException;
import com.paw.project.Utils.Exceptions.NotAcceptableException;
import com.paw.project.Utils.Exceptions.TokenExpiredException;
import com.paw.project.Utils.Exceptions.UnprocessableContentException;
import com.paw.project.Utils.Models.*;
import com.paw.project.Utils.Exceptions.*;
import com.paw.project.Utils.Models.Enums.RoleEnum;
import com.paw.project.Utils.Models.Enums.TokenType;
import com.paw.project.Utils.Repositories.PlansRepository;
import com.paw.project.Utils.Repositories.SubscriptionsRepository;
import com.paw.project.Utils.Repositories.TokenRepository;
import com.paw.project.Components.AccessManagerComponent.Authentication.AuthenticationResponse;
import com.paw.project.Components.AccessManagerComponent.Services.JWT.JWTService;
import com.paw.project.Utils.Repositories.UsersRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Optional;

import static com.paw.project.Utils.Others.Classes.AccessManagerMacros.*;

@Log4j2
@Service
@RequiredArgsConstructor
public class AuthenticationService {

    /*------------------------------------------  VARIABLES ----------------------------------------------------------*/

    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;
    private final JWTService jwtService;
    private final FileManagerService fileManagerService;
    private final TokenRepository tokenRepository;
    private final AuthenticationManager authenticationManager;
    private final SubscriptionsRepository subscriptionsRepository;
    private final PlansRepository plansRepository;


    /*------------------------------------------  REGISTER -----------------------------------------------------------*/

    /**
     * Registers a new user based on the provided registration request.
     *
     * @param registerRequest The registration request payload.
     * @return AuthenticationResponse containing access and refresh tokens.
     */
    public AuthenticationResponse register(RegisterRequest registerRequest)
    {
        /*
            Manages user roles during registration, including administrators in maintenance.
            Defaults to "USER" role; if a different role is specified, it configures the user accordingly.
            If the role is "ADMIN," it creates an administrator, allowing flexibility in role management.
        */
        registerRequest.setRoleIfNull(RoleEnum.USER);

        log.info("RegisterRequest => register{}",registerRequest);
        if(usersRepository.findByEmail(registerRequest.getEmail()).isPresent())
        {
            log.info("already exist user with this email");
            throw new ConflictException(CONFLIC_EXCEPTION_ALREADY_REGISTERED);
        }
        if(registerRequest.hasNullProperties()){
            throw new NotAcceptableException(NOT_ACCEPTABLE_EXCEPTION);
        }
        if(registerRequest.HasInvalidProperties()){
            throw new UnprocessableContentException(UNPROCESSABLE_CONTENT_EXCEPTION_FIELDS);
        }

        try {
            /* I don't know where to add subscription creation, but I think is necessary when user is created.
             Then will be modified by admin */
            Subscription subscription = Subscription.builder()
                    .plan(plansRepository.findByPlanId(1))
                    .build();

            var user = User.builder()
                    .name(registerRequest.getName())
                    .hashedPassword(passwordEncoder.encode(registerRequest.getPassword()))
                    .email(registerRequest.getEmail())
                    .isBlocked(false)
                    .roleEnum(registerRequest.getRole())
                    .subscription(subscription)
                    .build();

            var jwtToken = jwtService.generateToken(user);
            log.info("REGISTER SAVE ACCESS_TOKEN {}", jwtToken);
            var refreshToken = jwtService.generateRefreshToken(user);
            log.info("REGISTER SAVE REFRESH_TOKEN {}", refreshToken);

            subscriptionsRepository.save(subscription);
            var savedUser = usersRepository.save(user);
            saveUserToken(savedUser, jwtToken);
            log.info("Saving user {} ...  " , user);

            //Create Drive and root entry
            fileManagerService.createContextFor(user);

            return AuthenticationResponse.builder()
                    .accessToken(jwtToken)
                    .refreshToken(refreshToken)
                    .build();
        }catch (JwtSignatureException e){
            throw new JwtSignatureException("Signature exception. Your token seems corrupted");
        }
    }


    /*------------------------------------------  AUTHENTICATE -----------------------------------------------------------*/
    /**
     * Authenticates a user based on the provided authentication request.
     *
     * @param request The authentication request payload.
     * @return AuthenticationResponse containing access and refresh tokens.
     */
    public AuthenticationResponse authenticate(AuthenticationRequest request) {

        if(request.hasNullProperties()){
            throw new NotAcceptableException(NOT_ACCEPTABLE_EXCEPTION);
        }

        Optional<User> isActiveUser = usersRepository.findByEmail(request.getEmail());
        /* IF IS NOT REGISTERED*/
        if(isActiveUser.isEmpty())
        {
            throw new ConflictException(CONFLIC_EXCEPTION_NOT_REGISTERED);
        }

        /* IF IS BLOCKED */
        if(isActiveUser.get().getIsBlocked())
        {
            throw new ConflictException(CONFLICT_EXCEPTION_USER_BLOCKED);
        }

        log.info("AuthenticationService => authenticate{}",request);

        if(request.HasInvalidProperties()){
            throw new UnprocessableContentException(UNPROCESSABLE_CONTENT_EXCEPTION_FIELDS);
        }
        try {

            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );
            var user = usersRepository.findByEmail(request.getEmail())
                    .orElseThrow();

            var jwtToken = jwtService.generateToken(user);
            var refreshToken = jwtService.generateRefreshToken(user);
            revokeAllUserTokens(user);
            saveUserToken(user, jwtToken);

            return AuthenticationResponse.builder()
                    .accessToken(jwtToken)
                    .refreshToken(refreshToken)
                    .build();
        }catch (JwtSignatureException e){
            throw new JwtSignatureException("Signature exception. Your token seems corrupted");

        }
    }

    /*------------------------------------------  SAVE TOKENS -----------------------------------------------------------*/
    /**
     * Saves a user's token in the repository.
     *
     * @param user     The user for whom the token is generated.
     * @param jwtToken The JWT token to be saved.
     */
    private void saveUserToken(User user, String jwtToken) {
        try {
            var token = Token.builder()
                    .user(user)
                    .token(jwtToken)
                    .tokenType(TokenType.BEARER)
                    .expired(false)
                    .revoked(false)
                    .build();
            tokenRepository.save(token);
        }catch (JwtSignatureException e){
            throw new JwtSignatureException("Signature exception. Your token seems corrupted");

        }
    }

    /*------------------------------------------ REVOKE TOKENS -----------------------------------------------------------*/
    /**
     * Revokes all valid tokens for a given user.
     *
     * @param user The user for whom tokens need to be revoked.
     */
    private void revokeAllUserTokens(User user) {
        var validUserTokens = tokenRepository.findAllByExpiredAndRevokedAndUserUserId(false, false, user.getUserId());
        if (validUserTokens.isEmpty())
            return;
        validUserTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });
        tokenRepository.saveAll(validUserTokens);
    }

    public boolean getValidityToken(String token) {
        final String userEmail = jwtService.extractUserEmail(token);
        if (userEmail != null) {
            var user = this.usersRepository.findByEmail(userEmail)
                    .orElseThrow();
            if (jwtService.isTokenValid(token, user)) {
                return true;
            } else {
                return true;
            }
        }
        else{
            return false;
        }
    }
    /*------------------------------------------  REFRESH TOKENS -----------------------------------------------------------*/
    /**
     * Refreshes the access token using the provided refresh token.
     *
     * @param request  The HttpServletRequest containing the refresh token.
     * @param response The HttpServletResponse for writing the new tokens.
     * @throws IOException If an I/O exception occurs during token refresh.
     */
    public void refreshToken(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        log.info("AuthenticationService => refreshToken");
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String refreshToken;
        final String userEmail;
            log.info("try...");
            if (authHeader == null ||!authHeader.startsWith("Bearer ")) {
                log.info("authhreader = null {}",authHeader);
                return;
            }

            refreshToken = authHeader.substring(7);
            log.info("refreshtoken = {}", refreshToken);
            try {
                userEmail = jwtService.extractUserEmail(refreshToken);
                if (userEmail != null) {
                    var user = this.usersRepository.findByEmail(userEmail)
                            .orElseThrow();
                    if (jwtService.isTokenValid(refreshToken, user)) {
                        log.info("refresh token valid {}", jwtService.isTokenValid(refreshToken, user));
                        var accessToken = jwtService.generateToken(user);
                        log.info("RT SAVE ACCESS_TOKEN {}", accessToken);
                        log.info("RT SAVE REFRESH_TOKEN {}", refreshToken);

                        revokeAllUserTokens(user);
                        saveUserToken(user, accessToken);
                        var authResponse = AuthenticationResponse.builder()
                                .accessToken(accessToken)
                                .refreshToken(refreshToken)
                                .build();
                        new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
                    } else {
                        throw new TokenExpiredException("Token exception. Your token seems expired");
                    }
                }
            }catch (JwtSignatureException e){
                throw new JwtSignatureException("Signature exception. Your token seems corrupted");
            }


    }
}
