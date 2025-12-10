package com.dam.accesodatos.mcp;

import com.dam.accesodatos.ra3.HibernateNpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.mcp.server.annotation.Tool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Registro de herramientas MCP disponibles para LLMs.
 * 
 * Esta clase escanea automáticamente todos los métodos anotados con @Tool
 * y los registra como herramientas disponibles para el protocolo MCP.
 * 
 * CAMBIO: Ahora registra herramientas de HibernateNpcService en lugar de
 * HibernateUserService
 */
@Component
public class McpToolRegistry {

    private static final Logger logger = LoggerFactory.getLogger(McpToolRegistry.class);

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private HibernateNpcService hibernateNpcService;

    private final List<McpToolInfo> registeredTools = new ArrayList<>();

    @PostConstruct
    public void registerTools() {
        logger.info("Registrando herramientas MCP Hibernate/JPA para NPCs...");

        // Registrar herramientas del HibernateNpcService
        registerToolsFromService(hibernateNpcService, HibernateNpcService.class);

        logger.info("Total de herramientas MCP registradas: {}", registeredTools.size());

        // Mostrar herramientas registradas
        for (McpToolInfo tool : registeredTools) {
            logger.info("  - {}: {}", tool.getName(), tool.getDescription());
        }
    }

    private void registerToolsFromService(Object service, Class<?> serviceClass) {
        Method[] methods = serviceClass.getMethods();

        for (Method method : methods) {
            Tool toolAnnotation = method.getAnnotation(Tool.class);
            if (toolAnnotation != null) {
                String toolName = toolAnnotation.name().isEmpty() ? method.getName() : toolAnnotation.name();
                String description = toolAnnotation.description();

                McpToolInfo toolInfo = new McpToolInfo(toolName, description, method, service);
                registeredTools.add(toolInfo);

                logger.debug("Registrada herramienta MCP: {} - {}", toolName, description);
            }
        }
    }

    public List<McpToolInfo> getRegisteredTools() {
        return new ArrayList<>(registeredTools);
    }

    /**
     * Información sobre una herramienta MCP registrada
     */
    public static class McpToolInfo {
        private final String name;
        private final String description;
        private final Method method;
        private final Object service;

        public McpToolInfo(String name, String description, Method method, Object service) {
            this.name = name;
            this.description = description;
            this.method = method;
            this.service = service;
        }

        public String getName() {
            return name;
        }

        public String getDescription() {
            return description;
        }

        public Method getMethod() {
            return method;
        }

        public Object getService() {
            return service;
        }
    }
}
