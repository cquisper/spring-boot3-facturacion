package com.cquisper.springboot.app;

import com.cquisper.springboot.app.controllers.auth.handler.LoginFailureHandler;
import com.cquisper.springboot.app.controllers.auth.handler.LoginSuccessHandler;
import com.cquisper.springboot.app.service.JpaUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@EnableMethodSecurity(securedEnabled = true)
@Configuration
public class SpringSecurityConfig{
    @Autowired
    private LoginSuccessHandler successHandler;
    @Autowired
    private LoginFailureHandler failureHandler;
    @Autowired
    private JpaUserDetailsService userDetailsService;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public void configurerGlobal(AuthenticationManagerBuilder build) throws Exception{
        build.userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder);
    }

    /*@Bean
    public InMemoryUserDetailsManager userDetailsService(){

        UserDetails user = User.withUsername("cristhian")
                .password(passwordEncoder().encode("12345"))
                .roles("USER")
                .build();

        UserDetails admin = User.withUsername("shinbot")
                .password(passwordEncoder().encode("admin"))
                .roles("USER","ADMIN").build();
        return new AuthenticationManagerBuilder().userDetailsService();
        return new InMemoryUserDetailsManager(user, admin);
    }*/

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf().disable().cors().and()
                .authorizeHttpRequests()
                .requestMatchers("/clientes/", "/clientes/home"
                        , "/css/**", "/js/**", "/image/**", "/clientes/listar", "/locale/**")
                .permitAll().requestMatchers("/registro/**", "/clientes/api/**", "/api/clientes/**").permitAll()
                /*.requestMatchers("/clientes/ver/**").hasAnyRole("USER")
                .requestMatchers("/clientes/uploads/**").hasAnyRole("USER")
                .requestMatchers("uploads/**").hasAnyRole("USER")
                .requestMatchers("/clientes/form/**").hasAnyRole("ADMIN")
                .requestMatchers("/clientes/eliminar/**").hasAnyRole("ADMIN")
                .requestMatchers("/factura/**").hasAnyRole("ADMIN")*/
                .anyRequest().authenticated()
                .and().formLogin()
                .successHandler(successHandler)
                .loginPage("/login")
                .failureHandler(failureHandler)
                .permitAll()
                .and().logout()
                .permitAll()
                .and()
                .exceptionHandling().accessDeniedPage("/error-access");

        return http.build();
    }
}
