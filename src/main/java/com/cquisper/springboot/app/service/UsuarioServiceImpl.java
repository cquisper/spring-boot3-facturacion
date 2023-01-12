package com.cquisper.springboot.app.service;

import com.cquisper.springboot.app.models.dto.UsuarioDto;
import com.cquisper.springboot.app.models.entities.Usuario;
import com.cquisper.springboot.app.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UsuarioServiceImpl implements UsuarioService{
    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public Optional<Usuario> findByUsername(String username) {
        return usuarioRepository.findByUsername(username);
    }

    @Override
    public void guardar(UsuarioDto usuarioDto) {
        usuarioRepository.save(buildUserWithUserDto(usuarioDto));
    }

    private Usuario buildUserWithUserDto(UsuarioDto usuarioDto){
        Usuario usuario = new Usuario();
        usuario.setUsername(usuarioDto.getUsername());
        usuario.setEmail(usuarioDto.getEmail());
        usuario.setPassword(usuarioDto.getPassword());
        usuario.setEnabled(usuarioDto.getEnabled());
        usuario.setAvatar(usuarioDto.getAvatar());
        usuario.setRoles(usuarioDto.getRoles());

        return usuario;
    }
}
