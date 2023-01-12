package com.cquisper.springboot.app.controllers.auth.handler;

import com.cquisper.springboot.app.models.entities.Usuario;
import com.cquisper.springboot.app.repositories.UsuarioRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.FlashMap;
import org.springframework.web.servlet.support.SessionFlashMapManager;

import java.io.IOException;
import java.util.Optional;

@Component
public class LoginFailureHandler extends SimpleUrlAuthenticationFailureHandler {
    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        SessionFlashMapManager flashMapManager = new SessionFlashMapManager();

        FlashMap flashMap = new FlashMap();

        String mensajeError = getMensajeError(request);

        logger.info(exception.getClass().getSimpleName());

        logger.info(exception.getMessage());

        flashMap.put("errorLogin", mensajeError);

        flashMap.put("username", request.getParameter("username"));

        setDefaultFailureUrl("/login");

        flashMapManager.saveOutputFlashMap(flashMap, request, response);

        super.onAuthenticationFailure(request, response, exception);
    }

    private String getMensajeError(HttpServletRequest request){

        String password = request.getParameter("password");

        String username = request.getParameter("username");

        String mensaje = "Credenciales incorrectas";

        if(StringUtils.hasText(password) && StringUtils.hasLength(username)){
            Optional<Usuario> usuarioOptional = usuarioRepository.findByUsername(username);
            if(usuarioOptional.isPresent()){
                if (passwordEncoder.matches(password, usuarioOptional.get().getPassword())){
                    mensaje = "No tienes roles asignados!, intentalo de nuevo dentro de un rato";
                }else{
                    mensaje = "La contraseña es incorrecta";
                }
            }else {
                mensaje = "El nombre de usuario es incorrecto";
            }
        }

        return mensaje;
    }
}
