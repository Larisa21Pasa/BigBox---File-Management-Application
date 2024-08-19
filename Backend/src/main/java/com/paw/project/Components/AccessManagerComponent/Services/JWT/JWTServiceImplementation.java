/**************************************************************************

 File:        JWTServiceImplementation.java
 Copyright:   (c) 2023 NazImposter
 Description:  Service for JWT handling, including token extraction, generation, and validation.
 Designed by:  Larisa Pasa

 Module-History:
 Date        Author                Reason
 21.11.2023  Larisa Pasa           Added logic for refresh token
 11.11.2023  Larisa Pasa           Created Jwt Service for : generate token, check token validity,extract User Details (user email from token),

 **************************************************************************/
package com.paw.project.Components.AccessManagerComponent.Services.JWT;

import com.paw.project.Utils.Exceptions.JwtSignatureException;
import com.paw.project.Utils.Others.Classes.AccessManagerMacros;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import static com.paw.project.Utils.Others.Classes.AccessManagerMacros.TOKEN_EXPIRED_EXCEPTION;

@Log4j2
@Service
public class JWTServiceImplementation implements JWTService{

    /*------------------------------------------  VARIABLES -----------------------------------------------------------*/
    @Value(AccessManagerMacros.SECRET_KEY)
    private String secretKey;
    @Value("" + AccessManagerMacros.TOKEN_ACCESS_VALIDITY_TIME)
    private long jwtExpiration;
    @Value("" + AccessManagerMacros.TOKEN_REFRESH_VALIDITY_TIME)
    private long refreshExpiration;


    /*------------------------------------------  EXTRACT EMAIL -----------------------------------------------------------*/
    /**
     * Extracts the user email from the given JWT token.
     *
     * @param token The JWT token from which to extract the user email.
     * @return The user email extracted from the token.
     */
    public String extractUserEmail(String token)
    {
        try {
            log.info("Extracting user email from token: {}", token);
            return extractClaim(token, Claims::getSubject);
        }catch (SignatureException e){
            throw new JwtSignatureException("Signature exception. Your token seems corrupted");
        }
    }

    /*------------------------------------------ PROCESSING CLAIMS -----------------------------------------------------------*/
    /**
     * Generic method to extract a specific claim from a JWT token.
     *
     * @param token         The JWT token from which to extract the claim.
     * @param claimResolver A function to resolve the desired claim from the token's claims.
     * @param <T>           The type of the extracted claim.
     * @return The extracted claim value.
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimResolver)
    {
        /* Commented above for handling token expired exception below*/
        log.info("Extracting all claims from token: {}", token);

        try {
            final Claims claims = extractAllClaims(token);
            return claimResolver.apply(claims);
        } catch (ExpiredJwtException ex) {
            throw new ExpiredJwtException(null, null, TOKEN_EXPIRED_EXCEPTION + ex.getMessage());
        }
        catch (SignatureException e){
            throw new JwtSignatureException("Signature exception. Your token seems corrupted");
        }
    }

    /**
     * Extracts all claims from the JWT token.
     *
     * @param token The JWT token from which to extract all claims.
     * @return All claims contained in the token.
     */
    private Claims  extractAllClaims(String token)
    {
        try {
            return Jwts
                    .parserBuilder()
                    .setSigningKey(getSignInKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        }catch (SignatureException e){
            throw new JwtSignatureException("Signature exception. Your token seems corrupted");
        }
    }

    /*------------------------------------------  ENCRYPT-----------------------------------------------------------*/
    /**
     * Retrieves the signing key for JWT verification.
     *
     * @return The signing key for JWT verification.
     */
    private Key getSignInKey() {
        try {
            log.info("Retrieving signing key for JWT verification.");
            byte[] keyBytes = Decoders.BASE64.decode(AccessManagerMacros.SECRET_KEY);
            return Keys.hmacShaKeyFor(keyBytes);
        }catch (JwtSignatureException e){
            throw new JwtSignatureException("Signature exception. Your token seems corrupted");
        }
    }

    /*------------------------------------------ TOKEN GENERATING -----------------------------------------------------------*/
    /**
     * Generates a JWT token for the given UserDetails.
     *
     * @param userDetails The UserDetails for which to generate the JWT token.
     * @return The generated JWT token.
     */

    public String generateToken(UserDetails userDetails)
    {
        log.info("Generating JWT token for user: {}", userDetails.getUsername());
        return generateToken(new HashMap<>(), userDetails);
    }

    /**
     * Generates a JWT token with additional claims for the given UserDetails.
     *
     * @param extraClaims  Additional claims to be included in the JWT token.
     * @param userDetails  The UserDetails for which to generate the JWT token.
     * @return The generated JWT token.
     */
    public String generateToken(
            Map<String, Object> extraClaims,
            UserDetails userDetails
    ){
        log.info("Generating JWT token with extra claims for user: {}", userDetails.getUsername());
        return buildToken(extraClaims, userDetails, jwtExpiration);

    }

    /**
     * Generates a JWT refresh token for the given UserDetails.
     *
     * @param userDetails  The UserDetails for which to generate the refresh token.
     * @return The generated refresh token.
     */
    public String generateRefreshToken(
            UserDetails userDetails
    ) {
        log.info("Generating Refresh Token for user: {}", userDetails.getUsername());
        return buildToken(new HashMap<>(), userDetails, refreshExpiration);
    }

    /*------------------------------------------ BUILD TOKEN -----------------------------------------------------------*/
    /**
     * Builds a JWT token with the specified extra claims, user details, and expiration time.
     *
     * @param extraClaims  Additional claims to be included in the JWT token.
     * @param userDetails  The UserDetails for which to generate the JWT token.
     * @param expiration   The expiration time for the JWT token in milliseconds.
     * @return The built JWT token.
     */
    private String buildToken(
            Map<String, Object> extraClaims,
            UserDetails userDetails,
            long expiration
    ) {
        log.info("Building token for user: {}", userDetails.getUsername());
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /*------------------------------------------ CHECK TOKEN -----------------------------------------------------------*/
    /**
     * Checks if the given token is valid for the provided UserDetails.
     *
     * @param token        The JWT token to be validated.
     * @param userDetails  The UserDetails against which to validate the token.
     * @return True if the token is valid for the provided UserDetails, false otherwise.
     */

    public boolean isTokenValid(String token, UserDetails userDetails) {
        log.info("Validating token for user: {}", userDetails.getUsername());


        try {
            final String userEmail = extractUserEmail(token);
            Jws<Claims> claimsJws = Jwts.parserBuilder().setSigningKey(getSignInKey()).build().parseClaimsJws(token);

            return userEmail.equals(userDetails.getUsername()) && !isTokenExpired(token);
        }
        catch (SignatureException e) {
            throw new JwtSignatureException("Signature exception. Your token seems corrupted");
        }
    }


    /**
     * Checks if the given token has expired.
     *
     * @param token The JWT token to be checked for expiration.
     * @return True if the token has expired, false otherwise.
     */
    private boolean isTokenExpired(String token) {
        log.info("Checking if token is expired: {}", token);
        Date expirationDate = extractExpiration(token);

        if (expirationDate.before(new Date())) {
            log.error("Token is expired: {}", token);
            return true;
        }


        return extractExpiration(token).before(new Date());
    }
    /**
     * Extracts the expiration date from the JWT token.
     *
     * @param token The JWT token from which to extract the expiration date.
     * @return The expiration date extracted from the token.
     */
    private Date extractExpiration(String token) {
        log.info("Extracting expiration date from token: {}", token);
        return extractClaim(token, Claims::getExpiration);
    }
}
