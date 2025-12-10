package com.dam.accesodatos.mcp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.dam.accesodatos.ra3.HibernateNpcService;
import com.dam.accesodatos.model.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controlador REST que expone las herramientas MCP via HTTP para operaciones
 * Hibernate/JPA.
 *
 * CAMBIOS vs versión anterior (User):
 * - Ahora gestiona NPCs en lugar de Users
 * - Incluye endpoints para Pedidos e Ingredientes
 *
 * Proporciona endpoints para que los LLMs puedan:
 * - Listar herramientas Hibernate/JPA disponibles
 * - Ejecutar operaciones ORM específicas con NPCs
 * - Obtener información sobre el servidor MCP
 */
@RestController
@RequestMapping("/mcp")
@CrossOrigin(origins = "*")
public class McpServerController {

    private static final Logger logger = LoggerFactory.getLogger(McpServerController.class);

    @Autowired
    private HibernateNpcService hibernateNpcService;

    @Autowired
    private McpToolRegistry toolRegistry;

    /**
     * Endpoint de health check
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> getHealth() {
        Map<String, String> health = new HashMap<>();
        health.put("status", "UP");
        health.put("service", "MCP Server RA3 Hibernate/JPA - NPCs");

        return ResponseEntity.ok(health);
    }

    /**
     * Endpoint para listar todas las herramientas MCP disponibles
     */
    @GetMapping("/tools")
    public ResponseEntity<Map<String, Object>> getTools() {
        logger.debug("Solicitadas herramientas MCP Hibernate/JPA disponibles");

        List<McpToolRegistry.McpToolInfo> tools = toolRegistry.getRegisteredTools();

        List<Map<String, String>> toolsList = tools.stream()
                .map(tool -> {
                    Map<String, String> toolMap = new HashMap<>();
                    toolMap.put("name", tool.getName());
                    toolMap.put("description", tool.getDescription());
                    return toolMap;
                })
                .collect(java.util.stream.Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("tools", toolsList);
        response.put("count", toolsList.size());
        response.put("server", "MCP Server - RA3 Hibernate/JPA NPCs DAM");
        response.put("version", "2.0.0");

        return ResponseEntity.ok(response);
    }

    // ========== HIBERNATE/JPA OPERATION ENDPOINTS ==========

    /**
     * Prueba el EntityManager de Hibernate/JPA
     */
    @PostMapping("/test_entity_manager")
    public ResponseEntity<Map<String, Object>> testEntityManager() {
        logger.debug("Probando EntityManager");

        try {
            String result = hibernateNpcService.testEntityManager();

            Map<String, Object> response = new HashMap<>();
            response.put("tool", "test_entity_manager");
            response.put("result", result);
            response.put("status", "success");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error probando EntityManager", e);

            Map<String, Object> error = new HashMap<>();
            error.put("error", "Error probando EntityManager: " + e.getMessage());
            error.put("tool", "test_entity_manager");
            error.put("status", "error");

            return ResponseEntity.status(500).body(error);
        }
    }

    /**
     * Crea un nuevo NPC usando persist()
     */
    @PostMapping("/create_npc")
    public ResponseEntity<Map<String, Object>> createNpc(@RequestBody Map<String, String> request) {
        logger.debug("Creando NPC con Hibernate");

        try {
            String nombre = request.get("nombre");

            NpcCreateDto dto = new NpcCreateDto(nombre);
            Npc npc = hibernateNpcService.createNpc(dto);

            Map<String, Object> response = new HashMap<>();
            response.put("tool", "create_npc");
            response.put("result", npc);
            response.put("status", "success");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error creando NPC", e);

            Map<String, Object> error = new HashMap<>();
            error.put("error", "Error creando NPC: " + e.getMessage());
            error.put("tool", "create_npc");
            error.put("status", "error");

            return ResponseEntity.status(500).body(error);
        }
    }

    /**
     * Busca un NPC por ID usando find()
     */
    @PostMapping("/find_npc_by_id")
    public ResponseEntity<Map<String, Object>> findNpcById(@RequestBody Map<String, Object> request) {
        logger.debug("Buscando NPC por ID");

        try {
            Long npcId = ((Number) request.get("npcId")).longValue();
            Npc npc = hibernateNpcService.findNpcById(npcId);

            Map<String, Object> response = new HashMap<>();
            response.put("tool", "find_npc_by_id");
            response.put("result", npc);
            response.put("status", "success");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error buscando NPC", e);

            Map<String, Object> error = new HashMap<>();
            error.put("error", "Error buscando NPC: " + e.getMessage());
            error.put("tool", "find_npc_by_id");
            error.put("status", "error");

            return ResponseEntity.status(500).body(error);
        }
    }

    /**
     * Obtiene todos los NPCs usando JPA Repository
     */
    @PostMapping("/find_all_npcs")
    public ResponseEntity<Map<String, Object>> findAllNpcs() {
        logger.debug("Obteniendo todos los NPCs");

        try {
            List<Npc> npcs = hibernateNpcService.findAll();

            Map<String, Object> response = new HashMap<>();
            response.put("tool", "find_all_npcs");
            response.put("result", npcs);
            response.put("count", npcs.size());
            response.put("status", "success");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error obteniendo NPCs", e);

            Map<String, Object> error = new HashMap<>();
            error.put("error", "Error obteniendo NPCs: " + e.getMessage());
            error.put("tool", "find_all_npcs");
            error.put("status", "error");

            return ResponseEntity.status(500).body(error);
        }
    }

    /**
     * Busca NPCs activos usando JPQL
     */
    @PostMapping("/find_active_npcs")
    public ResponseEntity<Map<String, Object>> findActiveNpcs() {
        logger.debug("Buscando NPCs activos");

        try {
            List<Npc> npcs = hibernateNpcService.findActiveNpcs();

            Map<String, Object> response = new HashMap<>();
            response.put("tool", "find_active_npcs");
            response.put("result", npcs);
            response.put("count", npcs.size());
            response.put("status", "success");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error buscando NPCs activos", e);

            Map<String, Object> error = new HashMap<>();
            error.put("error", "Error buscando NPCs activos: " + e.getMessage());
            error.put("tool", "find_active_npcs");
            error.put("status", "error");

            return ResponseEntity.status(500).body(error);
        }
    }

    /**
     * Añade un pedido a un NPC
     */
    @PostMapping("/add_pedido_to_npc")
    public ResponseEntity<Map<String, Object>> addPedidoToNpc(@RequestBody Map<String, Object> request) {
        logger.debug("Añadiendo pedido a NPC");

        try {
            Long npcId = ((Number) request.get("npcId")).longValue();
            String comentario = (String) request.get("comentario");

            @SuppressWarnings("unchecked")
            List<Map<String, Object>> ingredientesRaw = (List<Map<String, Object>>) request.get("ingredientes");

            PedidoCreateDto dto = new PedidoCreateDto(comentario);

            if (ingredientesRaw != null) {
                List<IngredienteDto> ingredientes = new ArrayList<>();
                for (Map<String, Object> ing : ingredientesRaw) {
                    String nombre = (String) ing.get("nombre");
                    Integer cantidad = ing.get("cantidad") != null ? ((Number) ing.get("cantidad")).intValue() : 1;
                    ingredientes.add(new IngredienteDto(nombre, cantidad));
                }
                dto.setIngredientes(ingredientes);
            }

            Pedido pedido = hibernateNpcService.addPedidoToNpc(npcId, dto);

            Map<String, Object> response = new HashMap<>();
            response.put("tool", "add_pedido_to_npc");
            response.put("result", pedido);
            response.put("status", "success");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error añadiendo pedido", e);

            Map<String, Object> error = new HashMap<>();
            error.put("error", "Error añadiendo pedido: " + e.getMessage());
            error.put("tool", "add_pedido_to_npc");
            error.put("status", "error");

            return ResponseEntity.status(500).body(error);
        }
    }

    /**
     * Obtiene los pedidos de un NPC
     */
    @PostMapping("/find_pedidos_by_npc")
    public ResponseEntity<Map<String, Object>> findPedidosByNpc(@RequestBody Map<String, Object> request) {
        logger.debug("Obteniendo pedidos de NPC");

        try {
            Long npcId = ((Number) request.get("npcId")).longValue();
            List<Pedido> pedidos = hibernateNpcService.findPedidosByNpc(npcId);

            Map<String, Object> response = new HashMap<>();
            response.put("tool", "find_pedidos_by_npc");
            response.put("result", pedidos);
            response.put("count", pedidos.size());
            response.put("status", "success");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error obteniendo pedidos", e);

            Map<String, Object> error = new HashMap<>();
            error.put("error", "Error obteniendo pedidos: " + e.getMessage());
            error.put("tool", "find_pedidos_by_npc");
            error.put("status", "error");

            return ResponseEntity.status(500).body(error);
        }
    }
}
