package com.cquisper.springboot.app.service;

import com.cquisper.springboot.app.models.entities.Cliente;
import com.cquisper.springboot.app.models.entities.Factura;
import com.cquisper.springboot.app.models.entities.Producto;
import com.cquisper.springboot.app.repositories.ClienteRepository;
import com.cquisper.springboot.app.repositories.FacturaRepository;
import com.cquisper.springboot.app.repositories.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ClienteServiceImpl implements ClienteService {
    @Autowired
    private ClienteRepository clienteRepository;
    @Autowired
    private ProductoRepository productoRepository;
    @Autowired
    private FacturaRepository facturaRepository;

    @Override
    @Transactional(readOnly = true) //PATRON DE DISEÑO DE J2EE o FASCADE
    public List<Cliente> listar() {
        return clienteRepository.findAll();
    }

    @Override
    public List<Cliente> findAllByNombreCompleto(String nombreCompleto) {
        return clienteRepository.findByNombreLikeIgnoreCaseOrApellidoLikeIgnoreCase(nombreCompleto, nombreCompleto);
    }

    @Override
    public List<Producto> findAllByNombreProducto(String nombre) {
        return productoRepository.findByNombreLikeIgnoreCase("%".concat(nombre).concat("%"));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Cliente> porId(Long id) {
        return clienteRepository.findById(id);
    }

    @Override
    public Optional<Producto> porIdProducto(Long id) {
        return productoRepository.findById(id);
    }

    @Override
    public Optional<Factura> porIdFactura(Long id) {
        return facturaRepository.findById(id);
    }

    @Override
    public Optional<Cliente> fetchbyIdWithFacturas(Long id) {
        return clienteRepository.fetchbyIdWithFacturas(id);
    }

    @Override
    public Optional<Cliente> joinByIdWithFacturasNativeQuery(Integer  id) {
        return clienteRepository.joinByIdWithFacturasNativeQuery(id);
    }

    @Override
    public Optional<Factura> fetchbyIdFactura(Long id) {
        return facturaRepository.fetchbyIdWithClienteWithItemFacturaWithProducto(id);
    }

    @Override
    @Transactional
    public void guardar(Cliente cliente) {
        clienteRepository.save(cliente);
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        clienteRepository.deleteById(id);
    }

    @Override
    public void eliminarFactura(Factura factura) {
        facturaRepository.delete(factura);
    }

    @Override
    public void saveFactura(Factura factura) {
        facturaRepository.save(factura);
    }
}
