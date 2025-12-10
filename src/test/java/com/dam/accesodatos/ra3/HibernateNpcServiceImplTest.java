package com.dam.accesodatos.ra3;

import com.dam.accesodatos.model.Npc;
import com.dam.accesodatos.model.Pedido;
import com.dam.accesodatos.model.NpcCreateDto;
import com.dam.accesodatos.model.NpcUpdateDto;
import com.dam.accesodatos.model.NpcQueryDto;
import com.dam.accesodatos.model.PedidoCreateDto;
import com.dam.accesodatos.model.IngredienteDto;
import com.dam.accesodatos.repository.NpcRepository;
import com.dam.accesodatos.repository.PedidoRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Tests unitarios para los métodos IMPLEMENTADOS de HibernateNpcServiceImpl
 *
 * Estos tests cubren los 7 métodos de ejemplo implementados.
 * Los estudiantes pueden usarlos como guía para testear sus propias
 * implementaciones.
 *
 * COBERTURA: 12 tests que validan los 7 métodos implementados:
 * 1. testEntityManager() - 2 tests
 * 2. createNpc() - 1 test
 * 3. findNpcById() - 2 tests
 * 4. updateNpc() - 2 tests
 * 5. findAll() - 1 test
 * 6. addPedidoToNpc() - 2 tests
 * 7. findActiveNpcs() - 2 tests
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Tests Unitarios - Métodos Implementados NPC")
class HibernateNpcServiceImplTest {

    @Mock
    private EntityManager entityManager;

    @Mock
    private NpcRepository npcRepository;

    @Mock
    private PedidoRepository pedidoRepository;

    @InjectMocks
    private HibernateNpcServiceImpl service;

    private Npc testNpc;
    private NpcCreateDto createDto;
    private NpcUpdateDto updateDto;

    @BeforeEach
    void setUp() {
        // NPC de prueba
        testNpc = new Npc();
        testNpc.setId(1L);
        testNpc.setNombre("Chef Marco");
        testNpc.setActivo(true);
        testNpc.setCreatedAt(LocalDateTime.now());
        testNpc.setUpdatedAt(LocalDateTime.now());

        // DTO para crear
        createDto = new NpcCreateDto();
        createDto.setNombre("Nuevo NPC");

        // DTO para actualizar
        updateDto = new NpcUpdateDto();
        updateDto.setNombre("Nombre Actualizado");
        updateDto.setActivo(false);
    }

    // ========== Tests para testEntityManager() ==========

    @Test
    @DisplayName("testEntityManager() - Verifica EntityManager activo")
    void testEntityManager_Success() {
        // Given
        when(entityManager.isOpen()).thenReturn(true);
        Query query = mock(Query.class);
        when(entityManager.createNativeQuery(anyString())).thenReturn(query);
        when(query.getSingleResult()).thenReturn(new Object[] { 1, "H2" });

        // When
        String result = service.testEntityManager();

        // Then
        assertNotNull(result);
        assertTrue(result.contains("EntityManager activo"));
        assertTrue(result.contains("H2"));
        verify(entityManager).isOpen();
        verify(entityManager).createNativeQuery(anyString());
    }

    @Test
    @DisplayName("testEntityManager() - Falla si EntityManager cerrado")
    void testEntityManager_ClosedEntityManager() {
        // Given
        when(entityManager.isOpen()).thenReturn(false);

        // When & Then
        assertThrows(RuntimeException.class, () -> service.testEntityManager());
        verify(entityManager).isOpen();
        verify(entityManager, never()).createNativeQuery(anyString());
    }

    // ========== Tests para createNpc() ==========

    @Test
    @DisplayName("createNpc() - Crea NPC correctamente")
    void createNpc_Success() {
        // Given
        doNothing().when(entityManager).persist(any(Npc.class));

        // When
        Npc result = service.createNpc(createDto);

        // Then
        assertNotNull(result);
        assertEquals(createDto.getNombre(), result.getNombre());
        assertTrue(result.getActivo());
        assertNotNull(result.getCreatedAt());
        assertNotNull(result.getUpdatedAt());
        verify(entityManager).persist(any(Npc.class));
    }

    // ========== Tests para findNpcById() ==========

    @Test
    @DisplayName("findNpcById() - Encuentra NPC existente")
    void findNpcById_Found() {
        // Given
        when(entityManager.find(Npc.class, 1L)).thenReturn(testNpc);

        // When
        Npc result = service.findNpcById(1L);

        // Then
        assertNotNull(result);
        assertEquals(testNpc.getId(), result.getId());
        assertEquals(testNpc.getNombre(), result.getNombre());
        verify(entityManager).find(Npc.class, 1L);
    }

    @Test
    @DisplayName("findNpcById() - Retorna null si no existe")
    void findNpcById_NotFound() {
        // Given
        when(entityManager.find(Npc.class, 999L)).thenReturn(null);

        // When
        Npc result = service.findNpcById(999L);

        // Then
        assertNull(result);
        verify(entityManager).find(Npc.class, 999L);
    }

    // ========== Tests para updateNpc() ==========

    @Test
    @DisplayName("updateNpc() - Actualiza NPC existente")
    void updateNpc_Success() {
        // Given
        when(entityManager.find(Npc.class, 1L)).thenReturn(testNpc);
        when(entityManager.merge(any(Npc.class))).thenReturn(testNpc);

        // When
        Npc result = service.updateNpc(1L, updateDto);

        // Then
        assertNotNull(result);
        assertEquals(updateDto.getNombre(), testNpc.getNombre());
        assertEquals(updateDto.getActivo(), testNpc.getActivo());
        verify(entityManager).find(Npc.class, 1L);
        verify(entityManager).merge(any(Npc.class));
    }

    @Test
    @DisplayName("updateNpc() - Lanza excepción si NPC no existe")
    void updateNpc_NotFound() {
        // Given
        when(entityManager.find(Npc.class, 999L)).thenReturn(null);

        // When & Then
        assertThrows(RuntimeException.class, () -> service.updateNpc(999L, updateDto));
        verify(entityManager).find(Npc.class, 999L);
        verify(entityManager, never()).merge(any(Npc.class));
    }

    // ========== Tests para findAll() ==========

    @Test
    @DisplayName("findAll() - Retorna todos los NPCs")
    void findAll_Success() {
        // Given
        List<Npc> npcs = Arrays.asList(testNpc);
        when(npcRepository.findAll()).thenReturn(npcs);

        // When
        List<Npc> result = service.findAll();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testNpc.getId(), result.get(0).getId());
        verify(npcRepository).findAll();
    }

    // ========== Tests para addPedidoToNpc() ==========

    @Test
    @DisplayName("addPedidoToNpc() - Añade pedido con ingredientes")
    void addPedidoToNpc_Success() {
        // Given
        when(entityManager.find(Npc.class, 1L)).thenReturn(testNpc);
        when(entityManager.merge(any(Npc.class))).thenReturn(testNpc);

        PedidoCreateDto pedidoDto = new PedidoCreateDto("Pedido de prueba");
        pedidoDto.setIngredientes(Arrays.asList(
                new IngredienteDto("Tomate", 2),
                new IngredienteDto("Cebolla", 1)));

        // When
        Pedido result = service.addPedidoToNpc(1L, pedidoDto);

        // Then
        assertNotNull(result);
        assertEquals("Pedido de prueba", result.getComentario());
        assertEquals(2, result.getIngredientes().size());
        verify(entityManager).find(Npc.class, 1L);
        verify(entityManager).merge(any(Npc.class));
    }

    @Test
    @DisplayName("addPedidoToNpc() - Lanza excepción si NPC no existe")
    void addPedidoToNpc_NpcNotFound() {
        // Given
        when(entityManager.find(Npc.class, 999L)).thenReturn(null);
        PedidoCreateDto pedidoDto = new PedidoCreateDto("Pedido");

        // When & Then
        assertThrows(RuntimeException.class, () -> service.addPedidoToNpc(999L, pedidoDto));
        verify(entityManager).find(Npc.class, 999L);
    }

    // ========== Tests para findActiveNpcs() ==========

    @Test
    @DisplayName("findActiveNpcs() - Busca NPCs activos con JPQL")
    void findActiveNpcs_Success() {
        // Given
        @SuppressWarnings("unchecked")
        TypedQuery<Npc> query = mock(TypedQuery.class);
        List<Npc> npcs = Arrays.asList(testNpc);
        when(entityManager.createQuery(anyString(), eq(Npc.class))).thenReturn(query);
        when(query.getResultList()).thenReturn(npcs);

        // When
        List<Npc> result = service.findActiveNpcs();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(entityManager).createQuery(anyString(), eq(Npc.class));
        verify(query).getResultList();
    }

    @Test
    @DisplayName("findActiveNpcs() - Retorna lista vacía si no hay resultados")
    void findActiveNpcs_EmptyResult() {
        // Given
        @SuppressWarnings("unchecked")
        TypedQuery<Npc> query = mock(TypedQuery.class);
        when(entityManager.createQuery(anyString(), eq(Npc.class))).thenReturn(query);
        when(query.getResultList()).thenReturn(Arrays.asList());

        // When
        List<Npc> result = service.findActiveNpcs();

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(query).getResultList();
    }

    // ==========================================================================
    // TESTS PARA MÉTODOS TODO - DEBEN FALLAR HASTA QUE ESTUDIANTES IMPLEMENTEN
    // ==========================================================================

    // Los siguientes tests validan la funcionalidad que los estudiantes deben
    // implementar. Actualmente fallarán porque lanzan
    // UnsupportedOperationException.
    // Una vez implementados correctamente, estos tests deberían pasar.

    @Test
    @DisplayName("TODO CE3.e: deleteNpc() - Usa find() y remove() para eliminar")
    void deleteNpc_ExistingNpc_CallsRemove() {
        // Given
        when(entityManager.find(Npc.class, 1L)).thenReturn(testNpc);
        doNothing().when(entityManager).remove(any(Npc.class));

        // When
        boolean result = service.deleteNpc(1L);

        // Then
        assertTrue(result, "deleteNpc() debe retornar true cuando elimina un NPC");
        verify(entityManager).find(Npc.class, 1L);
        verify(entityManager).remove(testNpc);
    }

    @Test
    @DisplayName("TODO CE3.e: deleteNpc() - Retorna false si NPC no existe")
    void deleteNpc_NonExistent_ReturnsFalse() {
        // Given
        when(entityManager.find(Npc.class, 999L)).thenReturn(null);

        // When
        boolean result = service.deleteNpc(999L);

        // Then
        assertFalse(result, "deleteNpc() debe retornar false cuando el NPC no existe");
        verify(entityManager).find(Npc.class, 999L);
        verify(entityManager, never()).remove(any());
    }

    @Test
    @DisplayName("TODO CE3.f: searchNpcs() - Construye JPQL dinámico")
    void searchNpcs_WithFilters_BuildsDynamicJPQL() {
        // Given
        @SuppressWarnings("unchecked")
        TypedQuery<Npc> query = mock(TypedQuery.class);
        when(entityManager.createQuery(anyString(), eq(Npc.class))).thenReturn(query);
        when(query.setParameter(anyString(), any())).thenReturn(query);
        when(query.getResultList()).thenReturn(Arrays.asList(testNpc));

        NpcQueryDto queryDto = new NpcQueryDto();
        queryDto.setNombre("Chef");
        queryDto.setActivo(true);

        // When
        List<Npc> result = service.searchNpcs(queryDto);

        // Then
        assertNotNull(result);
        verify(entityManager).createQuery(anyString(), eq(Npc.class));
        verify(query).getResultList();
    }

    @Test
    @DisplayName("TODO CE3.g: transferData() - Persiste múltiples NPCs")
    void transferData_MultipleNpcs_PersistsAll() {
        // Given
        List<Npc> npcsToInsert = Arrays.asList(
                new Npc("NPC 1"),
                new Npc("NPC 2"),
                new Npc("NPC 3"));
        doNothing().when(entityManager).persist(any(Npc.class));

        // When
        boolean result = service.transferData(npcsToInsert);

        // Then
        assertTrue(result, "transferData() debe retornar true si la transacción es exitosa");
        verify(entityManager, times(3)).persist(any(Npc.class));
    }

    @Test
    @DisplayName("TODO CE3.f: countActiveNpcs() - Ejecuta COUNT JPQL")
    void countActiveNpcs_ReturnsCount() {
        // Given
        @SuppressWarnings("unchecked")
        TypedQuery<Long> query = mock(TypedQuery.class);
        when(entityManager.createQuery(anyString(), eq(Long.class))).thenReturn(query);
        when(query.getSingleResult()).thenReturn(5L);

        // When
        long result = service.countActiveNpcs();

        // Then
        assertEquals(5L, result);
        verify(entityManager).createQuery(anyString(), eq(Long.class));
        verify(query).getSingleResult();
    }
}
