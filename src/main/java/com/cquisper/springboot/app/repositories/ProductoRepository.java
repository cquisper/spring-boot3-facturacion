package com.cquisper.springboot.app.repositories;

import com.cquisper.springboot.app.models.entities.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductoRepository extends JpaRepository<Producto, Long> {
    @Query("select p from Producto p where p.nombre like %:nombre%")
    List<Producto> findByNombre(@Param("nombre") String nombre);
    List<Producto> findByNombreLikeIgnoreCase(String nombre);
}
