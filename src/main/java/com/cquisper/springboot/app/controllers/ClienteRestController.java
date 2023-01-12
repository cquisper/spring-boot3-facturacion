package com.cquisper.springboot.app.controllers;

import com.cquisper.springboot.app.exceptions.RegisterException;
import com.cquisper.springboot.app.service.ClienteService;
import org.hibernate.internal.util.StringHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/clientes")
public class ClienteRestController {
    @Autowired
    private ClienteService clienteService;

    @GetMapping("/listar")
    public ResponseEntity<?> index() {//Si se quiere tener tbm para xml se tiene que usar la clase Wrapper (ClienteList)
        Map<String, Object> contentBody = new HashMap<>();
        try{
            contentBody.put("clientes", clienteService.listar());
        }catch (DataAccessException e){
            contentBody.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            contentBody.put("status",HttpStatus.INTERNAL_SERVER_ERROR.value());

            return ResponseEntity.internalServerError().body(contentBody);
        }
        return new ResponseEntity<>(contentBody, HttpStatus.OK);
    }
}
