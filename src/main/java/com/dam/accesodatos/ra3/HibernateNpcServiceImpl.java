package com.dam.accesodatos.ra3;

import com.dam.accesodatos.model.*;
import com.dam.accesodatos.repository.NpcRepository;
import com.dam.accesodatos.repository.PedidoRepository;
import jakarta.persistence.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Implementación del servicio Hibernate/JPA para gestión de NPCs
 *
 * VERSIÓN MÍNIMOS ESTRICTOS
 *
 * ESTRUCTURA DE IMPLEMENTACIÓN:
 * - ✅ 7 MÉTODOS IMPLEMENTADOS (ejemplos para estudiantes)
 * - ❌ 4 MÉTODOS TODO (estudiantes deben implementar - mínimo para aprobar RA3)
 *
 * MÉTODOS IMPLEMENTADOS (Ejemplos):
 * 1. testEntityManager() - Ejemplo básico de EntityManager
 * 2. createNpc() - INSERT con persist() y @Transactional
 * 3. findNpcById() - SELECT con find()
 * 4. updateNpc() - UPDATE con merge()
 * 5. findAll() - SELECT all con Repository
 * 6. addPedidoToNpc() - Operación con relaciones
 * 7. findActiveNpcs() - JPQL básico
 *
 * MÉTODOS TODO (Estudiantes implementan - MÍNIMOS):
 * 1. deleteNpc() - EntityManager.remove()
 * 2. searchNpcs() - JPQL dinámico (simplificado, sin Criteria API)
 * 3. transferData() - Transacción múltiple
 * 4. countActiveNpcs() - JPQL COUNT
 */
@Service
@Transactional(readOnly = true) // Transacciones de solo lectura por defecto
public class HibernateNpcServiceImpl implements HibernateNpcService {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private NpcRepository npcRepository;

    @Autowired
    private PedidoRepository pedidoRepository;

    // ========== CE3.a: Configuración y Conexión ORM ==========

    /**
     * ✅ EJEMPLO IMPLEMENTADO 1/7: Prueba EntityManager
     *
     * Este método muestra el patrón fundamental de JPA:
     * 1. Verificar que EntityManager esté abierto
     * 2. Ejecutar una query nativa simple
     * 3. Procesar resultados
     *
     * DIFERENCIAS vs RA2 (JDBC):
     * - RA2: Connection.isClosed(), Statement.executeQuery("SELECT 1")
     * - RA3: EntityManager.isOpen(), createNativeQuery()
     */
    @Override
    public String testEntityManager() {
        if (!entityManager.isOpen()) {
            throw new RuntimeException("EntityManager está cerrado");
        }

        // Ejecutar query nativa simple (SQL directo, no JPQL)
        Query query = entityManager.createNativeQuery("SELECT 1 as test, DATABASE() as db_name");
        Object[] result = (Object[]) query.getSingleResult();

        return String.format("✓ EntityManager activo | Base de datos: %s | Test: %s",
                result[1], result[0]);
    }

    // ========== CE3.d, CE3.e: Operaciones CRUD ==========

    /**
     * ✅ EJEMPLO IMPLEMENTADO 2/7: INSERT con persist()
     *
     * Muestra cómo Hibernate simplifica INSERT:
     * - NO necesitas escribir SQL INSERT
     * - NO necesitas mapear parámetros manualmente
     * - NO necesitas getGeneratedKeys()
     * - Hibernate lo hace todo automáticamente
     *
     * DIFERENCIAS vs RA2 (JDBC):
     * - RA2: PreparedStatement.setString(), executeUpdate(), getGeneratedKeys()
     * - RA3: entityManager.persist(npc), todo automático
     *
     * IMPORTANTE: @Transactional es obligatorio para operaciones que modifican BD
     */
    @Override
    @Transactional // ← CRÍTICO: Modifica BD, necesita transacción
    public Npc createNpc(NpcCreateDto dto) {
        // Crear entidad desde DTO
        Npc npc = new Npc();
        npc.setNombre(dto.getNombre());
        npc.setActivo(true);
        npc.setCreatedAt(LocalDateTime.now());
        npc.setUpdatedAt(LocalDateTime.now());

        // persist() guarda la entidad en el contexto de persistencia
        // Hibernate genera automáticamente:
        // INSERT INTO npcs (nombre, activo, created_at, updated_at)
        // VALUES (?, ?, ?, ?)
        entityManager.persist(npc);

        // Al finalizar el método, Spring hace commit automáticamente
        // Hibernate ejecuta el INSERT y setea el ID generado
        return npc; // El ID ya está seteado
    }

    /**
     * ✅ EJEMPLO IMPLEMENTADO 3/7: SELECT por ID con find()
     *
     * Muestra la forma más simple de recuperar una entidad por ID.
     *
     * DIFERENCIAS vs RA2 (JDBC):
     * - RA2: PreparedStatement con "SELECT * FROM npcs WHERE id = ?", mapeo manual
     * de ResultSet
     * - RA3: entityManager.find(Npc.class, id), todo automático
     *
     * NOTA: find() retorna null si no existe (no lanza excepción)
     */
    @Override
    public Npc findNpcById(Long id) {
        // find() es la forma más directa de buscar por ID
        // Hibernate genera: SELECT ... FROM npcs WHERE id = ?
        // y mapea automáticamente las columnas a los atributos de Npc
        return entityManager.find(Npc.class, id);
    }

    /**
     * ✅ EJEMPLO IMPLEMENTADO 4/7: UPDATE con merge()
     *
     * Muestra cómo actualizar una entidad existente.
     *
     * DIFERENCIAS vs RA2 (JDBC):
     * - RA2: PreparedStatement con "UPDATE npcs SET ... WHERE id = ?"
     * - RA3: entityManager.merge(npc), Hibernate detecta cambios automáticamente
     *
     * PATRÓN IMPORTANTE:
     * 1. Buscar entidad existente
     * 2. Modificar atributos
     * 3. merge() sincroniza cambios con BD
     */
    @Override
    @Transactional // ← Modifica BD
    public Npc updateNpc(Long id, NpcUpdateDto dto) {
        // 1. Buscar entidad existente
        Npc existing = findNpcById(id);
        if (existing == null) {
            throw new RuntimeException("No se encontró NPC con ID " + id);
        }

        // 2. Aplicar cambios del DTO
        if (dto.getNombre() != null) {
            existing.setNombre(dto.getNombre());
        }
        if (dto.getActivo() != null) {
            existing.setActivo(dto.getActivo());
        }
        existing.setUpdatedAt(LocalDateTime.now());

        // 3. merge() actualiza la entidad
        // Hibernate detecta qué campos cambiaron y genera UPDATE solo de esos campos
        return entityManager.merge(existing);
        // Al finalizar, Spring hace commit y Hibernate ejecuta el UPDATE
    }

    @Override
    @Transactional
    public boolean deleteNpc(Long id) {
        // TODO CE3.e: Implementar deleteNpc()
        //
        // Guía de implementación:
        // 1. Buscar NPC: Npc npc = findNpcById(id);
        //
        // 2. Verificar si existe:
        // if (npc == null) return false;
        //
        // 3. Eliminar con remove():
        // entityManager.remove(npc);
        //
        // 4. Retornar true
        //
        // IMPORTANTE: remove() requiere que la entidad esté managed (en contexto de
        // persistencia)
        // Por eso primero la buscamos con find()
        //
        // NOTA: Los pedidos e ingredientes se eliminan automáticamente por cascade
        //
        // DIFERENCIA vs RA2:
        // - RA2: DELETE FROM npcs WHERE id = ?
        // - RA3: entityManager.remove(npc)

        throw new UnsupportedOperationException("TODO CE3.e: Implementar deleteNpc() - " +
                "Usar find() para buscar y remove() para eliminar");
    }

    /**
     * ✅ EJEMPLO IMPLEMENTADO 5/7: SELECT all con Repository
     *
     * Muestra cómo usar Spring Data JPA Repository.
     * La forma más simple de obtener todas las entidades.
     *
     * DIFERENCIAS vs RA2 (JDBC):
     * - RA2: while(rs.next()) { mapResultSetToNpc(rs); }
     * - RA3: npcRepository.findAll(), todo automático
     */
    @Override
    public List<Npc> findAll() {
        // Spring Data JPA genera automáticamente:
        // SELECT n FROM Npc n
        // y mapea resultados a List<Npc>
        return npcRepository.findAll();
    }

    // ========== OPERACIONES DE PEDIDOS ==========

    /**
     * ✅ EJEMPLO IMPLEMENTADO 6/7: Operación con relaciones
     *
     * Muestra cómo Hibernate maneja relaciones bidireccionales y cascadas.
     *
     * NOTA PEDAGÓGICA:
     * - Al usar npc.addPedido(), mantenemos la bidireccionalidad
     * - Los ingredientes se guardan automáticamente por cascade = ALL
     * - No hay que hacer INSERT manual de ingredientes
     */
    @Override
    @Transactional
    public Pedido addPedidoToNpc(Long npcId, PedidoCreateDto dto) {
        // 1. Buscar NPC
        Npc npc = findNpcById(npcId);
        if (npc == null) {
            throw new RuntimeException("No se encontró NPC con ID " + npcId);
        }

        // 2. Crear Pedido desde DTO
        Pedido pedido = new Pedido();
        pedido.setComentario(dto.getComentario());
        pedido.setCreatedAt(LocalDateTime.now());

        // 3. Añadir ingredientes si vienen en el DTO
        if (dto.getIngredientes() != null) {
            for (IngredienteDto ingDto : dto.getIngredientes()) {
                Ingrediente ingrediente = new Ingrediente();
                ingrediente.setNombre(ingDto.getNombre());
                ingrediente.setCantidad(ingDto.getCantidad() != null ? ingDto.getCantidad() : 1);
                pedido.addIngrediente(ingrediente); // Mantiene bidireccionalidad
            }
        }

        // 4. Añadir pedido al NPC (mantiene bidireccionalidad)
        npc.addPedido(pedido);

        // 5. merge() propaga cambios al pedido e ingredientes por cascade
        entityManager.merge(npc);

        return pedido;
    }

    /**
     * Obtiene los pedidos de un NPC.
     */
    @Override
    public List<Pedido> findPedidosByNpc(Long npcId) {
        return pedidoRepository.findByNpcId(npcId);
    }

    // ========== CE3.f: Consultas JPQL ==========

    /**
     * ✅ EJEMPLO IMPLEMENTADO 7/7: JPQL básico
     *
     * Muestra cómo escribir consultas JPQL (Java Persistence Query Language).
     *
     * DIFERENCIAS vs RA2 (JDBC):
     * - RA2: SQL "SELECT * FROM npcs WHERE activo = true"
     * - RA3: JPQL "SELECT n FROM Npc n WHERE n.activo = true"
     *
     * IMPORTANTE: JPQL usa nombres de entidades y atributos, NO tablas y columnas
     * - Correcto: "FROM Npc n" (entidad), "n.activo" (atributo)
     * - Incorrecto: "FROM npcs n" (tabla), "n.activo_flag" (columna)
     */
    @Override
    public List<Npc> findActiveNpcs() {
        // JPQL: Query language orientado a objetos
        // - Npc (entidad) en lugar de npcs (tabla)
        // - n.activo (atributo) en lugar de activo (columna)
        String jpql = "SELECT n FROM Npc n WHERE n.activo = true ORDER BY n.nombre";

        // TypedQuery garantiza type-safety
        TypedQuery<Npc> query = entityManager.createQuery(jpql, Npc.class);

        // getResultList() retorna List<Npc>
        return query.getResultList();
    }

    @Override
    public List<Npc> searchNpcs(NpcQueryDto queryDto) {
        // TODO CE3.f: Implementar searchNpcs() con JPQL dinámico
        //
        // VERSIÓN SIMPLIFICADA: Usa JPQL en lugar de Criteria API
        //
        // Guía de implementación:
        // 1. Construir JPQL dinámicamente:
        // StringBuilder jpql = new StringBuilder("SELECT n FROM Npc n WHERE 1=1");
        //
        // 2. Añadir condiciones según filtros presentes:
        // if (queryDto.getNombre() != null) {
        // jpql.append(" AND LOWER(n.nombre) LIKE LOWER(:nombre)");
        // }
        // if (queryDto.getActivo() != null) {
        // jpql.append(" AND n.activo = :activo");
        // }
        //
        // 3. Crear TypedQuery:
        // TypedQuery<Npc> query = entityManager.createQuery(jpql.toString(),
        // Npc.class);
        //
        // 4. Setear parámetros solo para filtros presentes:
        // if (queryDto.getNombre() != null) {
        // query.setParameter("nombre", "%" + queryDto.getNombre() + "%");
        // }
        // // ... repetir para activo
        //
        // 5. Ejecutar y retornar:
        // return query.getResultList();
        //
        // VENTAJA vs RA2: Parámetros nombrados evitan SQL injection

        throw new UnsupportedOperationException("TODO CE3.f: Implementar searchNpcs() - " +
                "Usar JPQL dinámico con parámetros nombrados para filtros opcionales");
    }

    // ========== CE3.g: Transacciones ==========

    @Override
    @Transactional
    public boolean transferData(List<Npc> npcs) {
        // TODO CE3.g: Implementar transferData()
        //
        // Guía de implementación:
        // 1. Iterar sobre NPCs:
        // for (Npc npc : npcs) {
        // entityManager.persist(npc);
        // }
        //
        // 2. Si todo OK, Spring hace commit automáticamente al finalizar el método
        //
        // 3. Si hay error (excepción), Spring hace rollback automáticamente
        //
        // 4. Retornar true
        //
        // DIFERENCIA vs RA2:
        // - RA2: conn.setAutoCommit(false), try-catch con commit()/rollback() manual
        // - RA3: @Transactional maneja todo automáticamente
        //
        // NOTA PEDAGÓGICA:
        // Esto demuestra la potencia de @Transactional de Spring:
        // - No necesitas setAutoCommit(false)
        // - No necesitas commit() manual
        // - No necesitas rollback() manual en catch
        // - Spring lo hace automáticamente según el resultado del método

        throw new UnsupportedOperationException("TODO CE3.g: Implementar transferData() - " +
                "Usar @Transactional con múltiples persist(), Spring maneja commit/rollback automáticamente");
    }

    @Override
    public long countActiveNpcs() {
        // TODO CE3.f: Implementar countActiveNpcs()
        //
        // Guía de implementación:
        // 1. Crear JPQL COUNT query:
        // String jpql = "SELECT COUNT(n) FROM Npc n WHERE n.activo = true";
        //
        // 2. Crear TypedQuery<Long>:
        // TypedQuery<Long> query = entityManager.createQuery(jpql, Long.class);
        //
        // 3. Ejecutar y retornar:
        // return query.getSingleResult();
        //
        // DIFERENCIA vs RA2:
        // - RA2: CallableStatement para stored procedure o COUNT manual
        // - RA3: JPQL COUNT query directo (más simple)

        throw new UnsupportedOperationException("TODO CE3.f: Implementar countActiveNpcs() - " +
                "Usar JPQL 'SELECT COUNT(n) FROM Npc n WHERE n.activo = true'");
    }
}
