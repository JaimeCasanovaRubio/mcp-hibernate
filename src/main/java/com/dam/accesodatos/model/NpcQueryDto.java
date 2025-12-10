package com.dam.accesodatos.model;

/**
 * DTO para búsqueda de NPCs con filtros opcionales
 * Usado en herramientas MCP para consultas dinámicas
 */
public class NpcQueryDto {

    private String nombre;
    private Boolean activo;

    public NpcQueryDto() {
    }

    public NpcQueryDto(String nombre, Boolean activo) {
        this.nombre = nombre;
        this.activo = activo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    @Override
    public String toString() {
        return "NpcQueryDto{" +
                "nombre='" + nombre + '\'' +
                ", activo=" + activo +
                '}';
    }
}
