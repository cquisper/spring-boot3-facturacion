package com.cquisper.springboot.app.controllers;

import com.cquisper.springboot.app.exceptions.RegisterException;
import com.cquisper.springboot.app.exceptions.ResourcesNotFoundException;
import com.cquisper.springboot.app.models.dto.UsuarioDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Objects;

@ControllerAdvice
public class ErrorHandlerController {
    @Value("${format.date.complete}")
    private String formatDate;
    private Logger log = LoggerFactory.getLogger(ErrorHandlerController.class);

    @ExceptionHandler({ResourcesNotFoundException.class})
    public String resourcesNotFoundException(ResourcesNotFoundException exception, Model model){
        if(Objects.nonNull(exception)){
            log.error(exception.getMessage());
            model.addAttribute("mensaje", exception.getMessage());
            model.addAttribute("fecha", LocalDateTime.now(ZoneId.systemDefault()));
            model.addAttribute("titulo", "Error con los recursos!");
        }
        return "error/exception";
    }

    @ExceptionHandler({RegisterException.class})
    public String registerException(RegisterException exception, Model model){
        model.addAttribute("error", exception.getMessage());
        if (Objects.nonNull(exception.getUsuarioDto())) {
            model.addAttribute("usuarioDto", exception.getUsuarioDto());
        }else{
            model.addAttribute("usuarioDto", new UsuarioDto());
        }
        model.addAttribute("titulo", "Registro de nuevo usuario");
        return "registro";
    }

    @ModelAttribute("formatDateComplete")
    public String patternFullFecha(){
        return this.formatDate;
    }
}
