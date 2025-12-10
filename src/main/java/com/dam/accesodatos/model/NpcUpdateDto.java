package com.dam.accesodatos.model;

import jakarta.validation.constraints.*;

/**
 * DTO para actualización de NPCs
 * Todos los campos son opcionales (solo se actualizan los que se envían)
 */
public class NpcUpdateDto {

    @Size(min = 2, max = 50, message = "El nombre debe tener entre 2 y 50 caracteres")
    private String nombre;

    private Boolean activo;

    public NpcUpdateDto() {
    }

    public NpcUpdateDto(String nombre, Boolean activo) {
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
        return "NpcUpdateDto{" +
                "nombre='" + nombre + '\'' +
                ", activo=" + activo +
                '}';
    }
}
