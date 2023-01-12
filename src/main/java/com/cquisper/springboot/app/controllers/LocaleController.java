package com.cquisper.springboot.app.controllers;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LocaleController {
    @GetMapping("/locale")
    public String locale(HttpServletRequest request){
        String ultimoUrl = request.getHeader("referer");
        System.out.println("Ultimo url: ".concat(ultimoUrl));
        return "redirect:".concat(ultimoUrl);
    }
}
