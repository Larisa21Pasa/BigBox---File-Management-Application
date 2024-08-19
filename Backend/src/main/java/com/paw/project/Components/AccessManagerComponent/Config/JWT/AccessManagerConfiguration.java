/**************************************************************************

 File: AccessManagerConfiguration.java
 Copyright: (c) 2023 NazImposter
 Description: Configuration class for the Access Manager component, defining beans and security settings.
 Designed by: Pasa Larisa

 Module-History:
 Date           Author          Reason
 11.11.2023     Pasa Larisa     Initial creation with bean definitions and security settings.
 **************************************************************************/
package com.paw.project.Components.AccessManagerComponent.Config.JWT;

import com.paw.project.Utils.Others.Classes.AccessManagerMacros;
import com.paw.project.Utils.Repositories.UsersRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.paw.project.Utils.Repositories.UsersRepository;

@Log4j2
@Configuration
@RequiredArgsConstructor
public class AccessManagerConfiguration {

    private final UsersRepository usersRepository;

    /**
     * Provides a custom implementation of UserDetailsService.
     *
     * @return Custom UserDetailsService implementation.
     */
    @Bean
    public UserDetailsService userDetailsService() throws UsernameNotFoundException {
        return email -> usersRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(AccessManagerMacros.NOT_FOUND_MESSAGE));
    }

    /**
     * Provides an AuthenticationProvider using DaoAuthenticationProvider.
     * Configures it with the custom UserDetailsService and a password encoder.
     *
     * @return AuthenticationProvider configured with custom UserDetailsService and password encoder.
     */
    @Bean
    public AuthenticationProvider authenticationProvider() throws UsernameNotFoundException {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setUserDetailsService(userDetailsService());
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
        return daoAuthenticationProvider;
    }


    /**
     * Provides an AuthenticationManager based on the provided AuthenticationConfiguration.
     *
     * @param authenticationConfiguration The authentication configuration to obtain the AuthenticationManager.
     * @return AuthenticationManager based on the provided AuthenticationConfiguration.
     * @throws Exception If an exception occurs while obtaining the AuthenticationManager.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        AuthenticationManager auth = null;
        try {
            auth = authenticationConfiguration.getAuthenticationManager();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return auth;
    }

    /**
     * Provides a PasswordEncoder using BCryptPasswordEncoder.
     *
     * @return PasswordEncoder using BCryptPasswordEncoder.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
