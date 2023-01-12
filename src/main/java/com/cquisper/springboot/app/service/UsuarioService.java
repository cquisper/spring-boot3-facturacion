package com.cquisper.springboot.app.service;

import com.cquisper.springboot.app.models.dto.UsuarioDto;
import com.cquisper.springboot.app.models.entities.Usuario;

import java.util.Optional;

public interface UsuarioService {

    Optional<Usuario> findByUsername(String username);

    void guardar(UsuarioDto usuarioDto);

}
