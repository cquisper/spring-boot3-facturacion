package com.cquisper.springboot.app.service;

import com.cquisper.springboot.app.models.dto.UsuarioSecurity;
import com.cquisper.springboot.app.models.entities.Role;
import com.cquisper.springboot.app.models.entities.Usuario;
import com.cquisper.springboot.app.repositories.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
@Service
public class JpaUserDetailsService implements UserDetailsService{
    @Autowired
    private UsuarioRepository usuarioRepository;
    private Logger log = LoggerFactory.getLogger(JpaUserDetailsService.class);

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.error("Error login: no existe el usuario '" + username + "'");
                    throw new UsernameNotFoundException(("Error login: no existe el usuario '" + username + "'"));
                });

        List<GrantedAuthority> authorities = buildUserAuthorities(usuario.getRoles(), username);

        return buildCurrentMyUserForAuthentication(usuario, authorities);
    }

    private List<GrantedAuthority> buildUserAuthorities(List<Role> roles, String username) throws UsernameNotFoundException{
        List<GrantedAuthority> authorityList = new ArrayList<>();

        roles.forEach(role -> authorityList.add(new SimpleGrantedAuthority(role.getAuthority())));

        if(authorityList.isEmpty()){
            log.error("Error login: el usuario '".concat(username).concat("' no tiene roles asignados!"));

            throw new UsernameNotFoundException("Error login: el usuario '" + username + "' no tiene roles asignados!");
        }

        return authorityList;
    }

    private User buildCurrentMyUserForAuthentication(Usuario usuario, List<GrantedAuthority> authorities){
        String username = usuario.getUsername();
        String password = usuario.getPassword();
        String email = usuario.getEmail();
        String avatar = usuario.getAvatar();
        LocalDate fechaCreacion = usuario.getFechaCreacion();
        boolean enabled = true;
        boolean accountNonExpired = true;
        boolean credentialsNonExpired = true;
        boolean accountNonLocked = true;

        return new UsuarioSecurity(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked,
                authorities, email, avatar, fechaCreacion);
    }
}
