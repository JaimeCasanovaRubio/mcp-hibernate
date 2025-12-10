package com.dam.accesodatos.ra3;

import com.dam.accesodatos.model.Npc;
import com.dam.accesodatos.model.Pedido;
import com.dam.accesodatos.model.NpcCreateDto;
import com.dam.accesodatos.model.NpcUpdateDto;
import com.dam.accesodatos.model.NpcQueryDto;
import com.dam.accesodatos.model.PedidoCreateDto;
import org.springframework.ai.mcp.server.annotation.Tool;

import java.util.List;

/**
 * Interface de servicio para operaciones Hibernate/JPA con NPCs
 *
 * RA3: Gestiona la persistencia de los datos identificando herramientas de
 * mapeo objeto relacional (ORM)
 *
 * VERSIÓN MÍNIMOS ESTRICTOS
 * Esta interface define herramientas MCP (métodos @Tool) enfocadas en los
 * criterios esenciales del RA3
 * usando Hibernate/JPA (EntityManager, JPQL, @Transactional, etc.)
 *
 * DIFERENCIAS vs RA2 (JDBC):
 * - RA2 usa: Connection, PreparedStatement, ResultSet, SQL puro
 * - RA3 usa: EntityManager, JPQL, @Transactional, Hibernate ORM
 *
 * Métodos organizados por criterios de evaluación RA3:
 * - CE3.a: Instalación y configuración ORM (1 método)
 * - CE3.d, CE3.e: Operaciones CRUD con Hibernate (6 métodos)
 * - CE3.f: Consultas JPQL (3 métodos)
 * - CE3.g: Gestión de transacciones (1 método)
 *
 * Total: 11 métodos (7 ejemplos + 4 TODOs para estudiantes)
 */
public interface HibernateNpcService {

    // ========== CE3.a: Configuración y Conexión ORM ==========

    /**
     * CE3.a: Prueba el EntityManager de Hibernate/JPA
     *
     * Implementación requerida:
     * - Verificar que EntityManager esté abierto con entityManager.isOpen()
     * - Ejecutar una query nativa simple: SELECT 1, DATABASE()
     * - Retornar información de conexión
     *
     * Clases JPA requeridas:
     * - jakarta.persistence.EntityManager (inyectado con @PersistenceContext)
     * - jakarta.persistence.Query para queries nativas
     *
     * DIFERENCIAS vs RA2:
     * - RA2: Connection.isClosed(), Statement.executeQuery()
     * - RA3: EntityManager.isOpen(), createNativeQuery()
     *
     * @return Mensaje indicando si EntityManager está activo
     * @throws RuntimeException si EntityManager está cerrado
     */
    @Tool(name = "test_entity_manager", description = "Prueba el EntityManager de Hibernate/JPA")
    String testEntityManager();

    // ========== CE3.d, CE3.e: Operaciones CRUD con Hibernate ==========

    /**
     * CE3.d, CE3.e: Persiste un nuevo NPC usando EntityManager.persist()
     *
     * Implementación requerida:
     * - Crear objeto Npc desde DTO
     * - Usar entityManager.persist(npc)
     * - Anotar método con @Transactional
     * - Hibernate genera el SQL INSERT automáticamente
     * - Hibernate setea el ID autogenerado en el objeto
     *
     * Clases JPA requeridas:
     * - jakarta.persistence.EntityManager
     * - @org.springframework.transaction.annotation.Transactional
     *
     * DIFERENCIAS vs RA2:
     * - RA2: PreparedStatement con INSERT, setString(), getGeneratedKeys()
     * - RA3: entityManager.persist(npc), Hibernate genera SQL automáticamente
     *
     * @param dto DTO con datos del NPC a crear
     * @return NPC creado con ID generado
     * @throws RuntimeException si hay error
     */
    @Tool(name = "create_npc", description = "Persiste un nuevo NPC usando EntityManager.persist() y @Transactional")
    Npc createNpc(NpcCreateDto dto);

    /**
     * CE3.e: Busca un NPC por su ID usando EntityManager.find()
     *
     * Implementación requerida:
     * - Usar entityManager.find(Npc.class, id)
     * - Hibernate genera SELECT automáticamente
     * - Hibernate mapea ResultSet a objeto Npc automáticamente
     * - Retorna null si no existe
     *
     * Clases JPA requeridas:
     * - jakarta.persistence.EntityManager
     * - Método: find(Class<T> entityClass, Object primaryKey)
     *
     * DIFERENCIAS vs RA2:
     * - RA2: PreparedStatement SELECT, rs.next(), mapeo manual con rs.getLong(),
     * rs.getString()
     * - RA3: entityManager.find(Npc.class, id), mapeo automático por Hibernate
     *
     * @param id ID del NPC a buscar
     * @return NPC encontrado o null si no existe
     * @throws RuntimeException si hay error de BD
     */
    @Tool(name = "find_npc_by_id", description = "Busca un NPC por ID usando EntityManager.find()")
    Npc findNpcById(Long id);

    /**
     * CE3.e: Actualiza un NPC existente usando EntityManager.merge()
     *
     * Implementación requerida:
     * - Buscar NPC existente con find()
     * - Aplicar cambios del DTO
     * - Usar entityManager.merge(npc)
     * - Anotar con @Transactional
     * - Hibernate genera SQL UPDATE automáticamente
     *
     * Clases JPA requeridas:
     * - jakarta.persistence.EntityManager
     * - Método: merge(T entity)
     *
     * DIFERENCIAS vs RA2:
     * - RA2: PreparedStatement UPDATE npcs SET ... WHERE id = ?
     * - RA3: entityManager.merge(npc), Hibernate detecta cambios y genera UPDATE
     *
     * @param id  ID del NPC a actualizar
     * @param dto DTO con datos a actualizar (campos opcionales)
     * @return NPC actualizado
     * @throws RuntimeException si el NPC no existe o hay error
     */
    @Tool(name = "update_npc", description = "Actualiza un NPC existente usando EntityManager.merge() y @Transactional")
    Npc updateNpc(Long id, NpcUpdateDto dto);

    /**
     * CE3.e: Elimina un NPC usando EntityManager.remove()
     *
     * Implementación requerida:
     * - Buscar NPC con find()
     * - Usar entityManager.remove(npc)
     * - Anotar con @Transactional
     * - Hibernate genera SQL DELETE automáticamente
     * - Los pedidos e ingredientes se eliminan en cascada
     *
     * Clases JPA requeridas:
     * - jakarta.persistence.EntityManager
     * - Método: remove(Object entity)
     *
     * DIFERENCIAS vs RA2:
     * - RA2: PreparedStatement DELETE FROM npcs WHERE id = ?
     * - RA3: entityManager.remove(npc), Hibernate genera DELETE
     *
     * @param id ID del NPC a eliminar
     * @return true si se eliminó, false si no existía
     * @throws RuntimeException si hay error de BD
     */
    @Tool(name = "delete_npc", description = "Elimina un NPC usando EntityManager.remove() y @Transactional")
    boolean deleteNpc(Long id);

    /**
     * CE3.e: Obtiene todos los NPCs usando Spring Data JPA Repository
     *
     * Implementación requerida:
     * - Usar npcRepository.findAll()
     * - Spring Data genera "SELECT n FROM Npc n" automáticamente
     *
     * Clases JPA requeridas:
     * - org.springframework.data.jpa.repository.JpaRepository
     * - Método: findAll()
     *
     * DIFERENCIAS vs RA2:
     * - RA2: SELECT * FROM npcs, while(rs.next()), mapeo manual
     * - RA3: npcRepository.findAll(), todo automático
     *
     * @return Lista de todos los NPCs
     * @throws RuntimeException si hay error
     */
    @Tool(name = "find_all_npcs", description = "Obtiene todos los NPCs usando JPA Repository.findAll()")
    List<Npc> findAll();

    // ========== OPERACIONES DE PEDIDOS ==========

    /**
     * CE3.d, CE3.e: Añade un pedido a un NPC
     *
     * Implementación requerida:
     * - Buscar NPC existente
     * - Crear Pedido desde DTO
     * - Usar npc.addPedido(pedido) para mantener bidireccionalidad
     * - Los ingredientes se crean en cascada si vienen en el DTO
     * - Anotar con @Transactional
     *
     * NOTA PEDAGÓGICA:
     * Este método muestra cómo Hibernate maneja relaciones bidireccionales
     * y operaciones en cascada (los ingredientes se guardan automáticamente).
     *
     * @param npcId ID del NPC
     * @param dto   DTO con datos del pedido e ingredientes
     * @return Pedido creado con ingredientes
     * @throws RuntimeException si el NPC no existe
     */
    @Tool(name = "add_pedido_to_npc", description = "Añade un pedido con ingredientes a un NPC")
    Pedido addPedidoToNpc(Long npcId, PedidoCreateDto dto);

    /**
     * CE3.f: Obtiene los pedidos de un NPC usando JPQL
     *
     * @param npcId ID del NPC
     * @return Lista de pedidos del NPC
     */
    @Tool(name = "find_pedidos_by_npc", description = "Obtiene los pedidos de un NPC")
    List<Pedido> findPedidosByNpc(Long npcId);

    // ========== CE3.f: Consultas JPQL/HQL ==========

    /**
     * CE3.f: Busca NPCs activos usando JPQL
     *
     * Implementación requerida:
     * - Crear TypedQuery con JPQL: "SELECT n FROM Npc n WHERE n.activo = true"
     * - Ejecutar con getResultList()
     * - IMPORTANTE: JPQL usa nombres de entidades (Npc), no tablas (npcs)
     *
     * Clases JPA requeridas:
     * - jakarta.persistence.TypedQuery
     * - entityManager.createQuery(jpql, Npc.class)
     *
     * DIFERENCIAS vs RA2:
     * - RA2: SQL "SELECT * FROM npcs WHERE activo = true"
     * - RA3: JPQL "SELECT n FROM Npc n WHERE n.activo = true"
     *
     * @return Lista de NPCs activos
     * @throws RuntimeException si hay error
     */
    @Tool(name = "find_active_npcs", description = "Busca NPCs activos usando JPQL")
    List<Npc> findActiveNpcs();

    /**
     * CE3.f: Busca NPCs con filtros dinámicos usando JPQL
     *
     * VERSIÓN SIMPLIFICADA: Usa JPQL en lugar de Criteria API
     *
     * Implementación requerida:
     * - Construir JPQL dinámicamente según filtros presentes en queryDto
     * - Ejemplo: "SELECT n FROM Npc n WHERE 1=1" + condiciones dinámicas
     * - Si queryDto.getNombre() != null: añadir "AND n.nombre LIKE :nombre"
     * - Si queryDto.getActivo() != null: añadir "AND n.activo = :activo"
     * - Crear TypedQuery y setear parámetros solo para los filtros presentes
     *
     * Clases JPA requeridas:
     * - jakarta.persistence.TypedQuery
     * - entityManager.createQuery(jpql, Npc.class)
     *
     * DIFERENCIAS vs RA2:
     * - RA2: StringBuilder para construir SQL dinámico
     * - RA3: JPQL con parámetros nombrados
     *
     * @param query DTO con filtros opcionales
     * @return Lista de NPCs que cumplen los criterios
     * @throws RuntimeException si hay error
     */
    @Tool(name = "search_npcs", description = "Busca NPCs con filtros dinámicos usando JPQL")
    List<Npc> searchNpcs(NpcQueryDto query);

    // ========== CE3.g: Gestión de Transacciones ==========

    /**
     * CE3.g: Inserta múltiples NPCs en una transacción con @Transactional
     *
     * Implementación requerida:
     * - Anotar con @Transactional
     * - En bucle: entityManager.persist(npc)
     * - Si hay error, Spring hace rollback automáticamente
     * - Si todo OK, Spring hace commit automáticamente
     *
     * Clases JPA requeridas:
     * - @Transactional de Spring
     * - EntityManager
     *
     * DIFERENCIAS vs RA2:
     * - RA2: conn.setAutoCommit(false), commit(), rollback() manual
     * - RA3: @Transactional, todo automático
     *
     * NOTA PEDAGÓGICA:
     * Esto demuestra la potencia de @Transactional de Spring:
     * - No necesitas setAutoCommit(false)
     * - No necesitas commit() manual
     * - No necesitas rollback() manual en catch
     * - Spring lo hace automáticamente según el resultado del método
     *
     * @param npcs Lista de NPCs a insertar en transacción
     * @return true si la transacción fue exitosa
     * @throws RuntimeException si hay error y se hace rollback
     */
    @Tool(name = "transfer_data", description = "Inserta múltiples NPCs en una transacción usando @Transactional")
    boolean transferData(List<Npc> npcs);

    /**
     * CE3.f: Ejecuta consulta COUNT de NPCs activos usando JPQL
     *
     * Implementación requerida:
     * - Crear TypedQuery con JPQL: "SELECT COUNT(n) FROM Npc n WHERE n.activo =
     * true"
     * - Ejecutar con getSingleResult()
     * - Retornar Long con el count
     *
     * Clases JPA requeridas:
     * - jakarta.persistence.TypedQuery<Long>
     *
     * DIFERENCIAS vs RA2:
     * - RA2: CallableStatement para stored procedure
     * - RA3: JPQL COUNT query directo
     *
     * @return Número de NPCs activos
     * @throws RuntimeException si hay error
     */
    @Tool(name = "count_active_npcs", description = "Ejecuta consulta COUNT de NPCs activos usando JPQL")
    long countActiveNpcs();
}
