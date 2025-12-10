package com.dam.accesodatos.repository;

import com.dam.accesodatos.model.Ingrediente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository JPA para la entidad Ingrediente
 *
 * RA3 - CRITERIOS d) y f):
 * - CE3.d: Mecanismos de persistencia
 * - CE3.f: Consultas JPQL
 */
@Repository
public interface IngredienteRepository extends JpaRepository<Ingrediente, Long> {

    // ===== MÉTODOS DERIVADOS (Query Methods) =====

    /**
     * Busca ingredientes de un pedido específico.
     *
     * @param pedidoId ID del pedido
     * @return Lista de ingredientes del pedido
     */
    List<Ingrediente> findByPedidoId(Long pedidoId);

    /**
     * Busca ingredientes por nombre.
     *
     * @param nombre Nombre del ingrediente
     * @return Lista de ingredientes con ese nombre
     */
    List<Ingrediente> findByNombre(String nombre);

    // ===== CONSULTAS JPQL PERSONALIZADAS =====

    /**
     * Busca ingredientes cuyo nombre contiene texto (case-insensitive).
     *
     * @param nombre Texto a buscar
     * @return Lista de ingredientes que coinciden
     */
    @Query("SELECT i FROM Ingrediente i WHERE LOWER(i.nombre) LIKE LOWER(CONCAT('%', :nombre, '%'))")
    List<Ingrediente> findByNombreContainingIgnoreCase(@Param("nombre") String nombre);

    /**
     * Cuenta el total de ingredientes en un pedido.
     *
     * @param pedidoId ID del pedido
     * @return Número de ingredientes
     */
    @Query("SELECT COUNT(i) FROM Ingrediente i WHERE i.pedido.id = :pedidoId")
    long countByPedidoId(@Param("pedidoId") Long pedidoId);
}
