package com.cquisper.springboot.app.exceptions;

import com.cquisper.springboot.app.models.dto.UsuarioDto;

public class RegisterException extends RuntimeException{
    private UsuarioDto usuarioDto;

    public RegisterException(String message, UsuarioDto usuarioDto) {
        super(message);
        this.usuarioDto = usuarioDto;
    }

    public RegisterException(String message) {
        super(message);
    }

    public UsuarioDto getUsuarioDto() {
        return usuarioDto;
    }

    public void setUsuarioDto(UsuarioDto usuarioDto) {
        this.usuarioDto = usuarioDto;
    }
}
