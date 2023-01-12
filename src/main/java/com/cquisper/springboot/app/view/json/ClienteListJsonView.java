package com.cquisper.springboot.app.view.json;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import java.util.Map;

@Component("lista.json")
public class ClienteListJsonView extends MappingJackson2JsonView {
    @Override
    protected Object filterModel(Map<String, Object> model) {
        model.remove("titulo");
        model.remove("formatDateComplete");
        model.remove("usuarioSecurity");
        return super.filterModel(model);
    }

}
