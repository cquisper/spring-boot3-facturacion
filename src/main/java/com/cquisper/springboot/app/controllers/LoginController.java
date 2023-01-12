package com.cquisper.springboot.app.controllers;

import com.cquisper.springboot.app.exceptions.ResourcesNotFoundException;
import com.cquisper.springboot.app.service.UploadFileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.net.MalformedURLException;
import java.security.Principal;

@Controller
public class LoginController {
    @Autowired
    private UploadFileService fileService;
    private final Logger log = LoggerFactory.getLogger(LoginController.class);

    @GetMapping("/login")
    public String login(@RequestParam(value = "logout", required = false) String logout,
                        Model model, Principal principal, RedirectAttributes flash) {
        if(principal != null){
            flash.addFlashAttribute("info", "Ya ha iniciado sesión anteriormente");
            log.info(principal.getName());
            return "redirect:/clientes/";
        }
        if(logout != null){
            log.info("Logout: " + logout);
            flash.addFlashAttribute("info", "Ha cerrado sesión correctamente");
            return "redirect:/clientes/home";
        }
        model.addAttribute("titulo", "Inicio de Sesión");
        return "login";
    }

    @GetMapping("/error-access")
    public String forbidden(Model model){
        model.addAttribute("titulo", "Error: Acceso Denegado");
        return "error/forbidden";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/uploads/{nombreImagen:.+}")
    public ResponseEntity<Resource> verImagen(@PathVariable String nombreImagen){
        Resource recurso;
        try {
            recurso = fileService.cargar(nombreImagen);
        } catch (MalformedURLException e) {
            throw new ResourcesNotFoundException("Error no se pudo cargar la imagen: " + nombreImagen);
        }
        HttpHeaders cabecera = new HttpHeaders();
        cabecera.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + recurso.getFilename() + "\"");
        return new ResponseEntity<>(recurso, cabecera, HttpStatus.OK);
    }
}
