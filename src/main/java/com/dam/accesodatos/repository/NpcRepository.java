package com.dam.accesodatos.repository;

import com.dam.accesodatos.model.Npc;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository JPA para la entidad Npc
 *
 * RA3 - CRITERIOS d) y f):
 * - CE3.d: Mecanismos de persistencia - Spring Data JPA proporciona métodos
 * CRUD automáticos
 * - CE3.f: Consultas JPQL - Permite definir consultas con @Query
 *
 * DIFERENCIAS vs RA2 (JDBC):
 * - RA2: Implementación manual con PreparedStatement, ResultSet, mapeo manual
 * - RA3: Interface que extiende JpaRepository - Spring genera implementación
 * automáticamente
 *
 * FUNCIONALIDADES AUTOMÁTICAS (sin implementar):
 * - save(Npc npc) - INSERT o UPDATE
 * - findById(Long id) - SELECT por ID
 * - findAll() - SELECT * FROM npcs
 * - deleteById(Long id) - DELETE
 * - count() - COUNT(*)
 * - existsById(Long id) - Verifica si existe
 *
 * MÉTODOS DERIVADOS:
 * Spring Data JPA genera la implementación automáticamente basándose en el
 * nombre del método:
 * - findByNombre → SELECT n FROM Npc n WHERE n.nombre = ?
 * - findByActivo → WHERE activo = ?
 *
 * CONSULTAS JPQL PERSONALIZADAS:
 * Se pueden definir consultas JPQL con @Query cuando la convención de nombres
 * no es suficiente.
 */
@Repository
public interface NpcRepository extends JpaRepository<Npc, Long> {

    // ===== MÉTODOS DERIVADOS (Query Methods) =====

    /**
     * Busca NPCs por nombre.
     * Spring Data genera automáticamente: WHERE nombre = ?
     *
     * @param nombre Nombre del NPC
     * @return Lista de NPCs con ese nombre
     */
    List<Npc> findByNombre(String nombre);

    /**
     * Busca NPCs por estado activo.
     * Spring Data genera: WHERE activo = ?
     *
     * @param activo Estado activo
     * @return Lista de NPCs activos/inactivos
     */
    List<Npc> findByActivo(Boolean activo);

    // ===== CONSULTAS JPQL PERSONALIZADAS =====

    /**
     * Busca NPCs activos ordenados por nombre.
     *
     * NOTA PEDAGÓGICA:
     * JPQL usa nombres de entidades y atributos, NO nombres de tablas y columnas:
     * - Correcto: "FROM Npc n" (entidad)
     * - Incorrecto: "FROM npcs n" (tabla)
     *
     * @return Lista de NPCs activos ordenados
     */
    @Query("SELECT n FROM Npc n WHERE n.activo = true ORDER BY n.nombre")
    List<Npc> findActiveNpcsOrderedByName();

    /**
     * Cuenta NPCs activos.
     *
     * Ejemplo de JPQL con agregación (COUNT).
     *
     * @return Número de NPCs activos
     */
    @Query("SELECT COUNT(n) FROM Npc n WHERE n.activo = true")
    long countActiveNpcs();

    /**
     * Busca NPCs cuyo nombre contiene un texto (case-insensitive).
     *
     * Ejemplo de búsqueda con LIKE en JPQL.
     *
     * @param nombre Texto a buscar en el nombre
     * @return Lista de NPCs que coinciden
     */
    @Query("SELECT n FROM Npc n WHERE LOWER(n.nombre) LIKE LOWER(CONCAT('%', :nombre, '%'))")
    List<Npc> findByNombreContainingIgnoreCase(@Param("nombre") String nombre);
}
