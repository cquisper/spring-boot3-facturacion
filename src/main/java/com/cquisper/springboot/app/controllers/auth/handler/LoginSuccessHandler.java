package com.cquisper.springboot.app.controllers.auth.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.DelegatingAuthenticationFailureHandler;
import org.springframework.security.web.authentication.ExceptionMappingAuthenticationFailureHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.FlashMap;
import org.springframework.web.servlet.support.SessionFlashMapManager;

import java.io.IOException;
import java.util.Objects;

@Component
public class LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler{

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        SessionFlashMapManager flashMapManager = new SessionFlashMapManager();
        FlashMap flashMap = new FlashMap();

        logger.info("Parameter: " + request.getParameter("username"));

        flashMap.put("success", "Hola " + request.getParameter("username") + ", haz iniciado sesión con exito!");

        super.setDefaultTargetUrl("/clientes/listar");

        flashMapManager.saveOutputFlashMap(flashMap, request, response);

        if(Objects.nonNull(authentication)){
            logger.info("El usuario '" + authentication.getName() + "' ha iniciado sesión con exitó");
        }

        super.onAuthenticationSuccess(request, response, authentication);
    }
}
