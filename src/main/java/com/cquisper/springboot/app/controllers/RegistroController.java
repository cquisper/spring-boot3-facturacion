package com.cquisper.springboot.app.controllers;

import com.cquisper.springboot.app.exceptions.RegisterException;
import com.cquisper.springboot.app.exceptions.ResourcesNotFoundException;
import com.cquisper.springboot.app.models.dto.UsuarioDto;
import com.cquisper.springboot.app.models.entities.Role;
import com.cquisper.springboot.app.service.UploadFileService;
import com.cquisper.springboot.app.service.UsuarioService;
import org.hibernate.internal.util.StringHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

@Controller
@RequestMapping("/registro")
public class RegistroController {
    @Autowired
    private UsuarioService usuarioService;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    @Autowired
    private UploadFileService fileService;
    private Logger log = LoggerFactory.getLogger(RegistroController.class);

    @GetMapping({"/form"})
    public String form(Model model){
        model.addAttribute("titulo", "Registro de nuevo usuario");
        model.addAttribute("usuarioDto", new UsuarioDto());
        return "registro";
    }

    @PostMapping("/form")
    public String registro(@Validated UsuarioDto usuarioDto, BindingResult result, Model model,
                           @RequestParam("foto") MultipartFile archivo,
                           RedirectAttributes flash, SessionStatus session) {
        if(result.hasErrors()){
            log.warn("Errores en el registro:");
            model.addAttribute("titulo", "Registro de nuevo usuario");
            result.getFieldErrors().forEach(fieldError -> log.warn(fieldError.getField()
                    .concat(": ").concat(Objects.requireNonNull(fieldError.getDefaultMessage()))));
            return "registro";
        }
        String nombreImagen;
        try {
            nombreImagen = fileService.guardar(archivo);
            if(!nombreImagen.isEmpty()) {
                log.info("Imagen guardada: " + nombreImagen);
                usuarioDto.setAvatar(nombreImagen);
            }
        } catch (IOException e) {
            throw new ResourcesNotFoundException("Lo sentimos ah ocurrido un error al cargar tu avatar :(, intentalo de nuevo dentro de un rato!");
        }
        usuarioDto.setRoles(buildRolesDefault(usuarioDto.getUsername()));
        usuarioDto.setPassword(passwordEncoder.encode(usuarioDto.getPassword()));//Encryptando la contraseña
        try{
            usuarioService.guardar(usuarioDto);
        }catch (DataAccessException e){
            log.error(e.getMessage());
            if (StringHelper.isNotEmpty(nombreImagen)){
                fileService.eliminar(nombreImagen);
            }
            throw new RegisterException(e.getMessage(), usuarioDto);
        }
        flash.addFlashAttribute("success", usuarioDto.getUsername().concat(", se ah registrado correctamente :)"));
        session.setComplete();

        return "redirect:/login";
    }

    private List<Role> buildRolesDefault(String username){
        return username.toLowerCase().contains("auquenex") ?
                List.of(new Role("ROLE_USER"), new Role("ROLE_ADMIN")) :
                List.of(new Role("ROLE_USER"));
    }
}
