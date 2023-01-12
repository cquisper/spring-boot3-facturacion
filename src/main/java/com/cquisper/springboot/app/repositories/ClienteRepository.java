package com.cquisper.springboot.app.repositories;

import com.cquisper.springboot.app.models.entities.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    @Query("select c from Cliente c left outer join fetch c.facturas f where c.id=:id")
    Optional<Cliente> fetchbyIdWithFacturas(@Param("id") Long id);

    @Query(value = "SELECT * FROM clientes as c LEFT OUTER JOIN facturas AS f ON f.cliente_id = c.id " +
            "LEFT OUTER JOIN facturas_items AS fi ON fi.factura_id = f.id " +
            "LEFT OUTER JOIN productos AS p ON fi.producto_id = p.id WHERE c.id=?1id", nativeQuery = true)
    Optional<Cliente> joinByIdWithFacturasNativeQuery(Integer id);

    List<Cliente> findByNombreLikeIgnoreCaseOrApellidoLikeIgnoreCase(String nombre, String apellido);
}
