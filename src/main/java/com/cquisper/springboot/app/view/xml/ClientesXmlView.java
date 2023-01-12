package com.cquisper.springboot.app.view.xml;

import com.cquisper.springboot.app.models.entities.Cliente;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.view.xml.MarshallingView;

import java.util.List;
import java.util.Map;

@Component("lista.xml")
public class ClientesXmlView extends MarshallingView {

    @Autowired
    public ClientesXmlView(Jaxb2Marshaller marshaller) {
        super(marshaller);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
        model.remove("titulo");
        List<Cliente> clientes = (List<Cliente>) model.get("clientes");
        model.remove("clientes");

        model.put("clienteList", new ClienteList(clientes));

        super.renderMergedOutputModel(model, request, response);
    }
}
