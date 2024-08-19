/**************************************************************************

 File:        SecurityConfiguration.java
 Copyright:   (c) 2023 NazImposter
 Description: Configuration class for security settings in the Access Manager component.
 Designed by: Larisa Pasa

 Module-History:
 Date        Author                Reason
 01.01.2024  Larisa Pasa           Refactor
 11.11.2023  Larisa Pasa           Created SecurityConfiguration
 21.11.2023  Larisa Pasa           Refactor to manage paths from project
 27.11.2023  Matei Rares           Update access to file manager
 02.12.2023  Larisa Pasa           Delete permission management , added ADMIN to Users resource
 10.12.2023  Larisa Pasa          Commented logout logic -> now logout will be processed by enpoint controller.
 25.12.2023  Tudor Toporas         New logic for authorization

 **************************************************************************/
package com.paw.project.Components.AccessManagerComponent.Config.Security;

import com.paw.project.Utils.Models.Enums.RoleEnum;
import com.paw.project.Components.AccessManagerComponent.Config.JWT.JWTAuthenticationFilter;
import com.paw.project.Utils.Others.Classes.SpecificUserAccessManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;

import static org.springframework.http.HttpMethod.*;
import static org.springframework.security.authorization.AuthorityAuthorizationManager.hasRole;
import static org.springframework.security.authorization.AuthorizationManagers.allOf;
import static org.springframework.security.authorization.AuthorizationManagers.anyOf;
import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;
@Log4j2
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfiguration extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {

    /**
     * An array of URLs exempt from security checks, allowing unrestricted access.
     * These URLs typically include endpoints related to authentication, registration, API documentation,
     * and other public resources. For our case, the included URLs define the following capabilities:
     * authentication, user registration, plans/subscription view, and Swagger API documentation.
     *
     * Note: The Swagger-related URLs facilitate the display and interaction with the API documentation
     * during development and testing. Excluding them from security checks allows public access,
     * aiding developers and testing teams in exploring and understanding the API.
     */
    private static final String[] WHITE_LIST_URL = {
            "/api/auth/**",
            "/api/file_manager_test/**", //just for test limited admin access
            "/api/welcome/**",
            "/swagger-resources",
            "/swagger-resources/**",
            "/configuration/ui",
            "/configuration/security",
            "/swagger-ui/**",
            "/webjars/**",
            "/swagger-ui.html",
            "/api/file_manager/populateDB"
    };
    private final JWTAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;
    private final LogoutHandler logoutHandler;
    private final SpecificUserAccessManager fileSystemAccessManager;

    /**
     * Configures security settings for the application, including authorization rules,
     * authentication providers, and filters.
     *
     * @param http The HttpSecurity object to configure security settings.
     * @return The configured SecurityFilterChain.
     * @throws Exception If an exception occurs during configuration.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

            http
            .csrf(AbstractHttpConfigurer::disable)

            .authorizeHttpRequests(req -> req
                            .requestMatchers(WHITE_LIST_URL).permitAll()
                            .requestMatchers("/api/admin/**").hasAnyRole(RoleEnum.ADMIN.name())
                            .requestMatchers(GET, "/api/admin/**").hasAnyRole(RoleEnum.ADMIN.name())

                            /* Admin will have access to users Table for Angular Logic*/
                            .requestMatchers("/api/users/**" ).hasAnyRole(RoleEnum.USER.name(),RoleEnum.ADMIN.name())
                            .requestMatchers("/api/file_manager/**").access(
                                    anyOf(
                                            hasRole(RoleEnum.ADMIN.name()),
                                            allOf(
                                                    hasRole(RoleEnum.USER.name()),
                                                    fileSystemAccessManager
                                            )
                                    )
                            )

                            .anyRequest()
                            .authenticated()
            )
            .sessionManagement(session -> session.sessionCreationPolicy(STATELESS))

            .authenticationProvider(authenticationProvider)
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

            log.info("securityFilterChain() with http {} ", http);
            return http.build();
        }

}
