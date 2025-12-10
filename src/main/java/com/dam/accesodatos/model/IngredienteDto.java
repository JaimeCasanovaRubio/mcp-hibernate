package com.dam.accesodatos.model;

import jakarta.validation.constraints.*;

/**
 * DTO para Ingredientes
 * Usado tanto para creación como para visualización
 */
public class IngredienteDto {

    @NotBlank(message = "El nombre del ingrediente es obligatorio")
    @Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
    private String nombre;

    @Min(value = 1, message = "La cantidad mínima es 1")
    private Integer cantidad = 1;

    public IngredienteDto() {
    }

    public IngredienteDto(String nombre) {
        this.nombre = nombre;
        this.cantidad = 1;
    }

    public IngredienteDto(String nombre, Integer cantidad) {
        this.nombre = nombre;
        this.cantidad = cantidad;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    /**
     * Convierte el DTO a entidad Ingrediente
     */
    public Ingrediente toIngrediente() {
        return new Ingrediente(this.nombre, this.cantidad);
    }

    @Override
    public String toString() {
        return "IngredienteDto{" +
                "nombre='" + nombre + '\'' +
                ", cantidad=" + cantidad +
                '}';
    }
}
