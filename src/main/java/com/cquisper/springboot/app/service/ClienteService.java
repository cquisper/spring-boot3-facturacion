package com.cquisper.springboot.app.service;

import com.cquisper.springboot.app.models.entities.Cliente;
import com.cquisper.springboot.app.models.entities.Factura;
import com.cquisper.springboot.app.models.entities.Producto;

import java.util.List;
import java.util.Optional;

public interface ClienteService {
    List<Cliente> listar();

    List<Cliente> findAllByNombreCompleto(String nombreCompleto);

    List<Producto> findAllByNombreProducto(String nombre);

    Optional<Cliente> porId(Long id);

    Optional<Producto> porIdProducto(Long id);

    Optional<Factura> porIdFactura(Long id);

    Optional<Cliente> fetchbyIdWithFacturas(Long id);

    Optional<Cliente> joinByIdWithFacturasNativeQuery(Integer id);

    Optional<Factura> fetchbyIdFactura(Long id);

    void guardar(Cliente cliente);

    void eliminar(Long id);

    void eliminarFactura(Factura factura);

    void saveFactura(Factura factura);
}
