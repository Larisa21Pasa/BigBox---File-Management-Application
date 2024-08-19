/**************************************************************************

 File:       LoggingInterceptor.java
 Copyright:   (c) 2023 NazImposter
 Description: Class for logging requests and responses.
 Designed by: Sebastian Pitica

 Module-History:
 Date        Author                Reason
 23.11.2023  Sebastian Pitica      Basic structure with methods overridden from HandlerInterceptor
 29.11.2023  Sebastian Pitica      Added description

 **************************************************************************/


package com.paw.project.Utils.Others.Classes;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
public class LoggingInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        log.info("Received request: {} : {}", request.getMethod(), request.getRequestURL());
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        String requestInfo = String.format("Request: %s : %s response: %d", request.getMethod(), request.getRequestURL(), response.getStatus());
        if (response.getStatus() < HttpStatus.BAD_REQUEST.value()) {
            log.info("Successful " + requestInfo);
        } else {
            String errorMessage = ex != null ? requestInfo + " Raised: " + ex : requestInfo;
            log.error("Failed " + errorMessage);
        }
        log.info("Sent response for " + requestInfo);
    }
}