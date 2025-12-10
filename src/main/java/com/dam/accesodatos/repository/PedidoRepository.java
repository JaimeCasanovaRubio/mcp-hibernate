package com.dam.accesodatos.repository;

import com.dam.accesodatos.model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository JPA para la entidad Pedido
 *
 * RA3 - CRITERIOS d) y f):
 * - CE3.d: Mecanismos de persistencia
 * - CE3.f: Consultas JPQL
 *
 * NOTA PEDAGÓGICA:
 * Este repository muestra cómo hacer consultas que involucran relaciones.
 * Spring Data JPA permite navegar entre entidades en las consultas.
 */
@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    // ===== MÉTODOS DERIVADOS (Query Methods) =====

    /**
     * Busca pedidos de un NPC específico.
     * Spring Data genera: WHERE npc.id = ?
     *
     * @param npcId ID del NPC
     * @return Lista de pedidos del NPC
     */
    List<Pedido> findByNpcId(Long npcId);

    /**
     * Busca pedidos por comentario que contenga texto.
     *
     * @param texto Texto a buscar en el comentario
     * @return Lista de pedidos que coinciden
     */
    List<Pedido> findByComentarioContaining(String texto);

    // ===== CONSULTAS JPQL PERSONALIZADAS =====

    /**
     * Busca pedidos de NPCs activos.
     *
     * NOTA PEDAGÓGICA:
     * Esta consulta muestra cómo navegar relaciones en JPQL:
     * p.npc.activo accede al campo activo de la entidad NPC relacionada.
     *
     * @return Lista de pedidos de NPCs activos
     */
    @Query("SELECT p FROM Pedido p WHERE p.npc.activo = true ORDER BY p.createdAt DESC")
    List<Pedido> findPedidosOfActiveNpcs();

    /**
     * Cuenta pedidos por NPC.
     *
     * @param npcId ID del NPC
     * @return Número de pedidos
     */
    @Query("SELECT COUNT(p) FROM Pedido p WHERE p.npc.id = :npcId")
    long countByNpcId(@Param("npcId") Long npcId);

    /**
     * Busca pedidos con sus ingredientes (fetch join para evitar N+1).
     *
     * NOTA PEDAGÓGICA:
     * JOIN FETCH carga los ingredientes en la misma consulta,
     * evitando el problema N+1 de queries.
     *
     * @param pedidoId ID del pedido
     * @return Pedido con ingredientes cargados
     */
    @Query("SELECT p FROM Pedido p LEFT JOIN FETCH p.ingredientes WHERE p.id = :pedidoId")
    Pedido findByIdWithIngredientes(@Param("pedidoId") Long pedidoId);
}
