package com.cquisper.springboot.app.repositories;

import com.cquisper.springboot.app.models.entities.Factura;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface FacturaRepository extends JpaRepository<Factura, Long> {
    @Query("select f from Factura f left outer join fetch f.cliente c" +
            " left outer join fetch f.itemFacturas if left outer join fetch if.producto where f.id=:id")
    Optional<Factura> fetchbyIdWithClienteWithItemFacturaWithProducto(@Param(value = "id") Long id);
}
