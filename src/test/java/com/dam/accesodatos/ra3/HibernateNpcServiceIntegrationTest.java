package com.dam.accesodatos.ra3;

import com.dam.accesodatos.model.*;
import com.dam.accesodatos.repository.NpcRepository;
import com.dam.accesodatos.repository.PedidoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests de integración para los métodos IMPLEMENTADOS de
 * HibernateNpcServiceImpl
 *
 * @SpringBootTest carga el contexto completo de Spring con base de datos H2
 *                 real.
 *                 Estos tests validan que los métodos de ejemplo funcionan
 *                 correctamente end-to-end.
 *
 *                 COBERTURA: 9 tests que validan los 7 métodos implementados:
 *                 1. testEntityManager() - 1 test
 *                 2. createNpc() + findNpcById() + updateNpc() - 1 test de
 *                 flujo completo
 *                 3. findAll() - 1 test
 *                 4. addPedidoToNpc() - 2 tests
 *                 5. findActiveNpcs() - 2 tests
 *                 6. Flujo CRUD completo - 1 test integrado
 *                 7. Relaciones NPC -> Pedidos -> Ingredientes - 1 test
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DisplayName("Tests Integración - Métodos Implementados NPC")
class HibernateNpcServiceIntegrationTest {

    @Autowired
    private HibernateNpcService service;

    @Autowired
    private NpcRepository npcRepository;

    @Autowired
    private PedidoRepository pedidoRepository;

    @BeforeEach
    void setUp() {
        // Limpiar BD antes de cada test
        pedidoRepository.deleteAll();
        npcRepository.deleteAll();
    }

    // ========== Tests de conexión y configuración ==========

    @Test
    @DisplayName("testEntityManager() - Conexión real funciona")
    void testEntityManager_RealConnection_Success() {
        // When
        String result = service.testEntityManager();

        // Then
        assertNotNull(result);
        assertTrue(result.contains("EntityManager activo"));
        // Verificar que hay algún nombre de base de datos en el resultado
        assertTrue(result.length() > 20, "El resultado debe contener información de la BD");
    }

    // ========== Tests de flujo CRUD completo ==========

    @Test
    @DisplayName("Flujo CRUD completo - Create, Read, Update con métodos implementados")
    void crudFlow_CompleteLifecycle_Success() {
        // 1. CREATE - Crear NPC con createNpc()
        NpcCreateDto createDto = new NpcCreateDto();
        createDto.setNombre("NPC de Integración");

        Npc created = service.createNpc(createDto);
        assertNotNull(created.getId());
        assertEquals("NPC de Integración", created.getNombre());
        assertTrue(created.getActivo());

        // 2. READ - Buscar NPC con findNpcById()
        Npc found = service.findNpcById(created.getId());
        assertNotNull(found);
        assertEquals(created.getId(), found.getId());
        assertEquals("NPC de Integración", found.getNombre());

        // 3. UPDATE - Actualizar NPC con updateNpc()
        NpcUpdateDto updateDto = new NpcUpdateDto();
        updateDto.setNombre("NPC Actualizado");
        updateDto.setActivo(false);

        Npc updated = service.updateNpc(created.getId(), updateDto);
        assertEquals("NPC Actualizado", updated.getNombre());
        assertFalse(updated.getActivo());
        assertEquals(created.getId(), updated.getId());

        // 4. VERIFY - Verificar que los cambios persisten
        Npc verified = service.findNpcById(created.getId());
        assertEquals("NPC Actualizado", verified.getNombre());
        assertFalse(verified.getActivo());
    }

    // ========== Tests de findAll() ==========

    @Test
    @DisplayName("findAll() - Retorna todos los NPCs creados")
    void findAll_ReturnsAllNpcs() {
        // Given - Crear varios NPCs
        createTestNpc("Chef Marco");
        createTestNpc("Cocinera Elena");
        createTestNpc("Pastelero Luis");

        // When
        List<Npc> allNpcs = service.findAll();

        // Then
        assertNotNull(allNpcs);
        assertEquals(3, allNpcs.size());
    }

    // ========== Tests de addPedidoToNpc() ==========

    @Test
    @DisplayName("addPedidoToNpc() - Añade pedido con ingredientes correctamente")
    void addPedidoToNpc_WithIngredients_Success() {
        // Given - Crear NPC
        Npc npc = createTestNpc("Chef de Prueba");

        // Crear DTO de pedido con ingredientes
        PedidoCreateDto pedidoDto = new PedidoCreateDto("Sopa del día");
        pedidoDto.setIngredientes(Arrays.asList(
                new IngredienteDto("Zanahoria", 3),
                new IngredienteDto("Cebolla", 2),
                new IngredienteDto("Apio", 1)));

        // When
        Pedido pedido = service.addPedidoToNpc(npc.getId(), pedidoDto);

        // Then
        assertNotNull(pedido);
        assertEquals("Sopa del día", pedido.getComentario());
        assertEquals(3, pedido.getIngredientes().size());

        // Verificar que los ingredientes están correctos
        assertTrue(pedido.getIngredientes().stream()
                .anyMatch(i -> "Zanahoria".equals(i.getNombre()) && i.getCantidad() == 3));
    }

    @Test
    @DisplayName("addPedidoToNpc() - Pedido sin ingredientes")
    void addPedidoToNpc_WithoutIngredients_Success() {
        // Given
        Npc npc = createTestNpc("Chef Simple");
        PedidoCreateDto pedidoDto = new PedidoCreateDto("Pedido básico");

        // When
        Pedido pedido = service.addPedidoToNpc(npc.getId(), pedidoDto);

        // Then
        assertNotNull(pedido);
        assertEquals("Pedido básico", pedido.getComentario());
        assertTrue(pedido.getIngredientes().isEmpty());
    }

    // ========== Tests de findActiveNpcs() ==========

    @Test
    @DisplayName("findActiveNpcs() - Solo retorna NPCs activos")
    void findActiveNpcs_OnlyActiveNpcs() {
        // Given - Crear NPCs activos e inactivos
        Npc active1 = createTestNpc("Activo 1");
        Npc active2 = createTestNpc("Activo 2");
        Npc inactive = createTestNpc("Inactivo");

        // Desactivar uno
        NpcUpdateDto deactivate = new NpcUpdateDto();
        deactivate.setActivo(false);
        service.updateNpc(inactive.getId(), deactivate);

        // When
        List<Npc> activeNpcs = service.findActiveNpcs();

        // Then
        assertNotNull(activeNpcs);
        assertEquals(2, activeNpcs.size());
        assertTrue(activeNpcs.stream().allMatch(Npc::getActivo));
    }

    @Test
    @DisplayName("findActiveNpcs() - Retorna lista vacía si no hay NPCs activos")
    void findActiveNpcs_NoActive_EmptyList() {
        // Given - Crear NPC y desactivarlo
        Npc npc = createTestNpc("Único NPC");
        NpcUpdateDto deactivate = new NpcUpdateDto();
        deactivate.setActivo(false);
        service.updateNpc(npc.getId(), deactivate);

        // When
        List<Npc> result = service.findActiveNpcs();

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    // ========== Tests de relaciones ==========

    @Test
    @DisplayName("Relaciones completas NPC -> Pedidos -> Ingredientes")
    void fullRelationship_NpcWithMultiplePedidosAndIngredientes() {
        // Given - Crear NPC
        Npc npc = createTestNpc("Chef Completo");

        // Añadir primer pedido
        PedidoCreateDto pedido1 = new PedidoCreateDto("Primer plato");
        pedido1.setIngredientes(Arrays.asList(
                new IngredienteDto("Tomate", 2),
                new IngredienteDto("Lechuga", 1)));
        service.addPedidoToNpc(npc.getId(), pedido1);

        // Añadir segundo pedido
        PedidoCreateDto pedido2 = new PedidoCreateDto("Segundo plato");
        pedido2.setIngredientes(Arrays.asList(
                new IngredienteDto("Arroz", 1),
                new IngredienteDto("Pollo", 1),
                new IngredienteDto("Verduras", 3)));
        service.addPedidoToNpc(npc.getId(), pedido2);

        // When - Obtener pedidos del NPC
        List<Pedido> pedidos = service.findPedidosByNpc(npc.getId());

        // Then
        assertNotNull(pedidos);
        assertEquals(2, pedidos.size());

        // Verificar totales de ingredientes
        int totalIngredientes = pedidos.stream()
                .mapToInt(p -> p.getIngredientes().size())
                .sum();
        assertEquals(5, totalIngredientes);
    }

    // ========== Tests de casos límite ==========

    @Test
    @DisplayName("findNpcById() - Retorna null para ID inexistente")
    void findNpcById_NonExistent_ReturnsNull() {
        // When
        Npc result = service.findNpcById(999L);

        // Then
        assertNull(result);
    }

    @Test
    @DisplayName("updateNpc() - Falla con ID inexistente")
    void updateNpc_NonExistent_ThrowsException() {
        // Given
        NpcUpdateDto updateDto = new NpcUpdateDto();
        updateDto.setNombre("Nuevo Nombre");

        // When & Then
        assertThrows(RuntimeException.class, () -> {
            service.updateNpc(999L, updateDto);
        });
    }

    // ==========================================================================
    // TESTS PARA MÉTODOS TODO - DEBEN FALLAR HASTA QUE ESTUDIANTES IMPLEMENTEN
    // ==========================================================================

    // Los siguientes tests validan la funcionalidad que los estudiantes deben
    // implementar. Actualmente fallarán porque lanzan
    // UnsupportedOperationException.
    // Una vez implementados correctamente, estos tests deberían pasar.

    @Test
    @DisplayName("TODO CE3.e: deleteNpc() - Elimina NPC correctamente")
    void deleteNpc_ExistingNpc_ReturnsTrue() {
        // Given - Crear NPC
        Npc npc = createTestNpc("NPC a eliminar");
        Long npcId = npc.getId();

        // When - Eliminar NPC
        boolean result = service.deleteNpc(npcId);

        // Then - Debe retornar true y el NPC no debe existir
        assertTrue(result, "deleteNpc() debe retornar true cuando elimina un NPC existente");
        assertNull(service.findNpcById(npcId), "El NPC no debe existir después de eliminarlo");
    }

    @Test
    @DisplayName("TODO CE3.e: deleteNpc() - Retorna false si NPC no existe")
    void deleteNpc_NonExistentNpc_ReturnsFalse() {
        // When - Intentar eliminar NPC inexistente
        boolean result = service.deleteNpc(999L);

        // Then - Debe retornar false
        assertFalse(result, "deleteNpc() debe retornar false cuando el NPC no existe");
    }

    @Test
    @DisplayName("TODO CE3.e: deleteNpc() - Elimina pedidos e ingredientes en cascada")
    void deleteNpc_WithPedidos_DeletesCascade() {
        // Given - Crear NPC con pedido e ingredientes
        Npc npc = createTestNpc("NPC con pedidos");
        PedidoCreateDto pedidoDto = new PedidoCreateDto("Pedido de prueba");
        pedidoDto.setIngredientes(Arrays.asList(
                new IngredienteDto("Ingrediente 1", 1),
                new IngredienteDto("Ingrediente 2", 2)));
        service.addPedidoToNpc(npc.getId(), pedidoDto);

        Long npcId = npc.getId();

        // When - Eliminar NPC
        boolean result = service.deleteNpc(npcId);

        // Then - NPC y sus pedidos deben estar eliminados
        assertTrue(result);
        assertNull(service.findNpcById(npcId));
        assertTrue(service.findPedidosByNpc(npcId).isEmpty(),
                "Los pedidos deben eliminarse en cascada");
    }

    @Test
    @DisplayName("TODO CE3.f: searchNpcs() - Busca por nombre")
    void searchNpcs_ByNombre_ReturnsMatches() {
        // Given - Crear NPCs
        createTestNpc("Chef Marco");
        createTestNpc("Chef Elena");
        createTestNpc("Pastelero Luis");

        NpcQueryDto query = new NpcQueryDto();
        query.setNombre("Chef");

        // When - Buscar NPCs
        List<Npc> result = service.searchNpcs(query);

        // Then - Debe retornar los 2 Chefs
        assertNotNull(result);
        assertEquals(2, result.size(), "Debe encontrar 2 NPCs con 'Chef' en el nombre");
        assertTrue(result.stream().allMatch(n -> n.getNombre().contains("Chef")));
    }

    @Test
    @DisplayName("TODO CE3.f: searchNpcs() - Busca por estado activo")
    void searchNpcs_ByActivo_ReturnsMatches() {
        // Given - Crear NPCs activos e inactivos
        Npc active = createTestNpc("NPC Activo");
        Npc inactive = createTestNpc("NPC Inactivo");

        NpcUpdateDto deactivate = new NpcUpdateDto();
        deactivate.setActivo(false);
        service.updateNpc(inactive.getId(), deactivate);

        NpcQueryDto query = new NpcQueryDto();
        query.setActivo(true);

        // When - Buscar solo activos
        List<Npc> result = service.searchNpcs(query);

        // Then - Solo debe retornar el activo
        assertNotNull(result);
        assertEquals(1, result.size(), "Debe encontrar 1 NPC activo");
        assertTrue(result.get(0).getActivo());
    }

    @Test
    @DisplayName("TODO CE3.f: searchNpcs() - Busca con múltiples filtros")
    void searchNpcs_MultipleFilters_ReturnsMatches() {
        // Given - Crear NPCs variados
        createTestNpc("Chef Marco");
        Npc chefInactivo = createTestNpc("Chef Inactivo");
        createTestNpc("Pastelero Luis");

        NpcUpdateDto deactivate = new NpcUpdateDto();
        deactivate.setActivo(false);
        service.updateNpc(chefInactivo.getId(), deactivate);

        NpcQueryDto query = new NpcQueryDto();
        query.setNombre("Chef");
        query.setActivo(true);

        // When - Buscar Chefs activos
        List<Npc> result = service.searchNpcs(query);

        // Then - Solo Chef Marco
        assertNotNull(result);
        assertEquals(1, result.size(), "Debe encontrar 1 Chef activo");
        assertEquals("Chef Marco", result.get(0).getNombre());
    }

    @Test
    @DisplayName("TODO CE3.g: transferData() - Inserta múltiples NPCs en transacción")
    void transferData_MultipleNpcs_AllPersisted() {
        // Given - Lista de NPCs a insertar
        List<Npc> npcsToInsert = Arrays.asList(
                new Npc("NPC Batch 1"),
                new Npc("NPC Batch 2"),
                new Npc("NPC Batch 3"));

        // When - Transferir datos
        boolean result = service.transferData(npcsToInsert);

        // Then - Todos deben estar persistidos
        assertTrue(result, "transferData() debe retornar true si la transacción es exitosa");

        List<Npc> allNpcs = service.findAll();
        assertEquals(3, allNpcs.size(), "Deben haberse insertado 3 NPCs");
    }

    @Test
    @DisplayName("TODO CE3.f: countActiveNpcs() - Cuenta NPCs activos")
    void countActiveNpcs_ReturnsCorrectCount() {
        // Given - Crear NPCs activos e inactivos
        createTestNpc("Activo 1");
        createTestNpc("Activo 2");
        createTestNpc("Activo 3");
        Npc inactive = createTestNpc("Inactivo");

        NpcUpdateDto deactivate = new NpcUpdateDto();
        deactivate.setActivo(false);
        service.updateNpc(inactive.getId(), deactivate);

        // When - Contar activos
        long count = service.countActiveNpcs();

        // Then - Debe ser 3
        assertEquals(3L, count, "Debe contar 3 NPCs activos");
    }

    @Test
    @DisplayName("TODO CE3.f: countActiveNpcs() - Retorna 0 si no hay activos")
    void countActiveNpcs_NoActive_ReturnsZero() {
        // Given - Crear NPC y desactivarlo
        Npc npc = createTestNpc("Único NPC");
        NpcUpdateDto deactivate = new NpcUpdateDto();
        deactivate.setActivo(false);
        service.updateNpc(npc.getId(), deactivate);

        // When
        long count = service.countActiveNpcs();

        // Then
        assertEquals(0L, count, "Debe retornar 0 cuando no hay NPCs activos");
    }

    // ========== Métodos auxiliares ==========

    private Npc createTestNpc(String nombre) {
        NpcCreateDto dto = new NpcCreateDto();
        dto.setNombre(nombre);
        return service.createNpc(dto);
    }
}
