/**************************************************************************

 File:        AppConfig.java
 Copyright:   (c) 2023 NazImposter
 Description: Class for adding interceptors for logging purposes.
 Designed by: Sebastian Pitica

 Module-History:
 Date        Author                Reason
 23.11.2023  Sebastian Pitica      Basic structure with addInterceptors method
 29.11.2023  Sebastian Pitica      Added description

 **************************************************************************/


package com.paw.project.Utils.Others.Classes;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class AppConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LoggingInterceptor());
    }
}