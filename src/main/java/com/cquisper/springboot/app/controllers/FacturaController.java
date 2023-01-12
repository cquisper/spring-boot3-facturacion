package com.cquisper.springboot.app.controllers;

import com.cquisper.springboot.app.models.dto.UsuarioSecurity;
import com.cquisper.springboot.app.models.entities.Cliente;
import com.cquisper.springboot.app.models.entities.Factura;
import com.cquisper.springboot.app.models.entities.ItemFactura;
import com.cquisper.springboot.app.models.entities.Producto;
import com.cquisper.springboot.app.service.ClienteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
@Secured({"ROLE_ADMIN"})
@Controller
@RequestMapping("/factura")
@SessionAttributes({"titulo","factura"})
public class FacturaController {
    @Autowired
    private ClienteService clienteService;
    private Logger log = LoggerFactory.getLogger(FacturaController.class);
    @Value("${format.date.complete}")
    private String formatDate;

    @GetMapping("/ver/{id}")
    public String ver(@PathVariable Long id, Model model, RedirectAttributes flash){
        Optional<Factura> facturaOptional = clienteService.fetchbyIdFactura(id); //clienteService.porIdFactura(id);
        if(facturaOptional.isEmpty()){
            flash.addFlashAttribute("error", "La factura no existe en la base de datos!");
            return "redirect:/clientes/listar";
        }
        model.addAttribute("factura", facturaOptional.get());
        model.addAttribute("titulo", "Factura: ".concat(facturaOptional.get().getDescripcion()));
        return "factura/ver";
    }

    @GetMapping("/form/{clienteId}")
    public String form(@PathVariable Long clienteId, Model model, RedirectAttributes flash){
        Optional<Cliente> clienteOptional = clienteService.porId(clienteId);
        if(clienteOptional.isEmpty()){
            flash.addFlashAttribute("error", "El cliente no existe en la base de datos!");
            return "redirect:/clientes/home";
        }
        Factura factura = new Factura();
        factura.setCliente(clienteOptional.get());
        model.addAttribute("factura", factura);
        model.addAttribute("titulo", "Crear Factura");
        return "factura/form";
    }

    @PostMapping("/form")
    public String guardar(@Validated Factura factura, BindingResult result, Model model
            , @RequestParam(value = "item_id[]", required = false) Long[] idsProductos, @RequestParam(value = "cantidad[]", required = false) Integer[] cantidad
            , RedirectAttributes flash ,SessionStatus status){
        if(result.hasErrors()){
            return "factura/form";
        }
        if(Objects.isNull(idsProductos) || idsProductos.length == 0){
            model.addAttribute("error", "Error: No se ah agregado ningún detalle para la factura ");
            return "factura/form";
        }
        log.info("Factura: " + factura.getDescripcion() + " ," + factura.getObservacion());
        ItemFactura itemFactura = new ItemFactura();
        AtomicInteger i = new AtomicInteger();
        Arrays.stream(idsProductos).forEach(id -> clienteService.porIdProducto(id).ifPresent(producto -> {
            log.info("ID: " + id.toString() + ", CANTIDAD: " + cantidad[i.get()].toString());
            itemFactura.setProducto(producto);
            itemFactura.setCantidad(cantidad[i.getAndIncrement()]);
            factura.addItemFactura(itemFactura);
        }));

        clienteService.saveFactura(factura);
        status.setComplete();
        flash.addFlashAttribute("success", "Factura creada con éxito!");

        return "redirect:/clientes/ver/" + factura.getCliente().getId();
    }

    @GetMapping(value = "/cargar-productos/{nombre}", produces = {"application/json"})
    public @ResponseBody List<Producto> cargarProductos(@PathVariable String nombre){
        return clienteService.findAllByNombreProducto(nombre);
    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id, RedirectAttributes flash){
        Optional<Factura> facturaOptional = clienteService.porIdFactura(id);
        if(facturaOptional.isPresent()){
            clienteService.eliminarFactura(facturaOptional.get());
            flash.addFlashAttribute("success", "Factura eliminada con éxito!");
            return "redirect:/clientes/ver/" + facturaOptional.get().getCliente().getId();
        }
        flash.addFlashAttribute("error", "La factura no existe en la base de datos, no se pudo eliminar!");

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
}
