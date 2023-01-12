package com.cquisper.springboot.app.controllers;

import com.cquisper.springboot.app.editors.LocalDateEditor;
import com.cquisper.springboot.app.models.dto.UsuarioSecurity;
import com.cquisper.springboot.app.models.entities.Cliente;
import com.cquisper.springboot.app.models.entities.Usuario;
import com.cquisper.springboot.app.service.ClienteService;
import com.cquisper.springboot.app.service.UploadFileService;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestWrapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.net.MalformedURLException;
import java.time.LocalDate;
import java.util.*;

@Controller
@RequestMapping("/clientes")
@SessionAttributes({"cliente", "titulo"})
public class ClienteController {
    @Autowired
    private ClienteService clienteService;
    @Autowired
    private LocalDateEditor dateEditor;
    @Autowired
    private UploadFileService fileService;
    @Value("${format.date.complete}")
    private String formatDate;
    @Autowired
    private MessageSource messageSource;
    private final Logger log = LoggerFactory.getLogger(ClienteController.class);

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(LocalDate.class, "createAt", dateEditor);
    }

    @GetMapping("/api/listar")
    public ResponseEntity<?> listarRest() {//Si se quiere tener tbm para xml se tiene que usar la clase Wrapper (ClienteList)
        return new ResponseEntity<>(clienteService.listar(), HttpStatus.OK);
    }

    @GetMapping({"/listar", "/", "", "/home"})
    public String listar(Model model, Authentication authentication,
                         HttpServletRequest request, Locale locale) {
        if(Objects.nonNull(authentication)){
            log.info("HoLa usuario autenticado, tu username es: ".concat(authentication.getName()));
        }
        //Otra forma con metodo estatico
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if(Objects.nonNull(auth)){
            log.info(("Utilizando forma estática SecurityContextHolder.getContext().getAuthentication():" +
                    "\nUsuario Autenticado , tu username es: ").concat(auth.getName()));
        }

        if(hasRole("ROLE_ADMIN")){
            log.info("Hola ".concat(auth.getName()).concat(" tienes acceso"));
        }else{
            log.info("Hola ".concat(auth.getName()).concat(" no tienes acceso"));
        }

        SecurityContextHolderAwareRequestWrapper securityContext = new SecurityContextHolderAwareRequestWrapper(request, "ROLE_");

        if(securityContext.isUserInRole("ADMIN")){
            log.info("Forma usando SecurityContextHolderAwareRequestWrapper: ".concat("\n")
                    .concat(securityContext.getRemoteUser()).concat( " tienes acceso"));
        }else {
            log.info("Forma usando SecurityContextHolderAwareRequestWrapper: ".concat("\n")
                    .concat(auth.getName()).concat( " NO tienes acceso"));
        }

        if(request.isUserInRole("ROLE_ADMIN")){
            log.info("Forma usando HttpServletRequest: ".concat("\n")
                    .concat(request.getRemoteUser()).concat( " tienes acceso"));
        }else {
            log.info("Forma usando HttpServletRequest: ".concat("\n")
                    .concat(auth.getName()).concat( " NO tienes acceso"));
        }

        model.addAttribute("titulo", messageSource.getMessage("text.cliente.titulo",null, locale));
        model.addAttribute("clientes", clienteService.listar());
        return "lista";
    }

    @GetMapping(value = "/listar/{nombre}", produces = {"application/json"})
    public @ResponseBody List<Cliente> listarPorNombreCompleto(@PathVariable String nombre){
        return clienteService.findAllByNombreCompleto(nombre);
    }

    @Secured("ROLE_USER")
    @GetMapping("/ver/{id}")
    public String verCliente(@PathVariable Long id, Model model, RedirectAttributes flash){
        Optional<Cliente> clienteOptional = clienteService.fetchbyIdWithFacturas(id);//clienteService.fetchbyIdWithFacturas(id);//clienteService.porId(id);
        if(clienteOptional.isPresent()){
            model.addAttribute("cliente", clienteOptional.get());
            model.addAttribute("titulo", "Detalle del cliente");
            return "ver";
        }
        flash.addFlashAttribute("info", "El cliente no se encuentra en la base de datos!");
        return "redirect:/clientes/home";
    }

    @Secured("ROLE_ADMIN")
    @GetMapping({"/form/{id}", "/form"})
    public String form(@PathVariable(required = false) Long id, Model model) {
        id = Objects.requireNonNullElse(id, 0).longValue();
        clienteService.porId(id).ifPresentOrElse(cliente -> {
            model.addAttribute("cliente", cliente);
            model.addAttribute("titulo", "Editar Cliente");
        }, () -> {
            model.addAttribute("cliente", new Cliente());
            model.addAttribute("titulo", "Crear Cliente");
        });
        System.out.println(model.getAttribute("titulo"));
        return "form";
    }

    @Secured("ROLE_ADMIN")
    @PostMapping("/form")
    public String guardar(@Validated Cliente cliente, BindingResult result, @RequestParam("foto") MultipartFile archivo,
                          RedirectAttributes flash, SessionStatus session) {
        if (result.hasErrors()) {
            log.info("Hay errores compa");
            return "form";
        }
        try {
            String nombreImagen = fileService.guardar(archivo);
            if(!nombreImagen.isEmpty()) {
                log.info("Imagen guardada: " + nombreImagen);
                flash.addFlashAttribute("info", "Imagen guardada " + archivo.getOriginalFilename() + " con éxito");
            }
            String nombreImagenCliente = cliente.getImagen();
            if(!nombreImagen.isEmpty() && nombreImagenCliente != null){ // si es un cliente nuevo creado en la plataforma
                cliente.setImagen(nombreImagen);
                log.info("Borrando imagen anterior: " + nombreImagenCliente);
                fileService.eliminar(nombreImagenCliente);
            }else if(nombreImagenCliente == null){
                cliente.setImagen(nombreImagen);
            }
        } catch (IOException e) {
            throw new RuntimeException("Error: No es posible guardar la imagen ");
        }
        String message = "Cliente ".concat(cliente.getId() != null && cliente.getId() > 0 ? "Editado" : "Creado").concat(" con éxito!");
        clienteService.guardar(cliente);
        flash.addFlashAttribute("success", message);
        session.setComplete();
        return "redirect:/clientes/listar";
    }

    @Secured("ROLE_ADMIN")
    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id, RedirectAttributes flash) {
            clienteService.porId(id).ifPresent(cliente -> {
            clienteService.eliminar(id);
            flash.addFlashAttribute("success", "Cliente eliminado con éxito!");
            if(fileService.eliminar(cliente.getImagen())){
                flash.addFlashAttribute("info", "Imagen " + cliente.getImagen() + " eliminada con éxito!");
            }
        });
        return "redirect:/clientes/listar";
    }

    @ModelAttribute("formatDateComplete")
    public String patternFullFecha(){
        return this.formatDate;
    }

    @ModelAttribute
    public UsuarioSecurity usuarioSecurity(Authentication authentication){
        if(Objects.nonNull(authentication)){
            if (authentication.isAuthenticated()){
                return (UsuarioSecurity) authentication.getPrincipal();
            }
        }
        return null;
    }

    private boolean hasRole(String role){
        SecurityContext context = SecurityContextHolder.getContext();

        if(Objects.isNull(context)){
            return false;
        }

        Authentication auth = context.getAuthentication();

        if(Objects.isNull(auth)){
            return false;
        }

        return auth.getAuthorities().contains(new SimpleGrantedAuthority(role));

        /*
        for (GrantedAuthority authority : auth.getAuthorities()) {
            log.info("ROLES: " + authority.getAuthority());
            if(authority.getAuthority().equalsIgnoreCase(role)) {
                log.info("ROLES DEL USUARIO : ".concat(auth.getName()).concat("\n").concat(authority.getAuthority()));

                return true;
            }
        }

        return false;*/
    }
}
