package com.dam.accesodatos.model;

import jakarta.validation.constraints.*;

/**
 * DTO para creación de NPCs
 * Usado en herramientas MCP para validar entrada de datos
 */
public class NpcCreateDto {

    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 2, max = 50, message = "El nombre debe tener entre 2 y 50 caracteres")
    private String nombre;

    public NpcCreateDto() {
    }

    public NpcCreateDto(String nombre) {
        this.nombre = nombre;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Convierte el DTO a entidad Npc
     */
    public Npc toNpc() {
        return new Npc(this.nombre);
    }

    @Override
    public String toString() {
        return "NpcCreateDto{" +
                "nombre='" + nombre + '\'' +
                '}';
    }
}
