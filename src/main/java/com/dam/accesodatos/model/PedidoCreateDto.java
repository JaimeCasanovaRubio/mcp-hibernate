package com.dam.accesodatos.model;

import jakarta.validation.constraints.*;
import java.util.List;
import java.util.ArrayList;

/**
 * DTO para creación de Pedidos
 * Incluye el comentario y opcionalmente una lista de ingredientes
 */
public class PedidoCreateDto {

    @Size(max = 255, message = "El comentario no puede exceder 255 caracteres")
    private String comentario;

    private List<IngredienteDto> ingredientes = new ArrayList<>();

    public PedidoCreateDto() {
    }

    public PedidoCreateDto(String comentario) {
        this.comentario = comentario;
    }

    public PedidoCreateDto(String comentario, List<IngredienteDto> ingredientes) {
        this.comentario = comentario;
        this.ingredientes = ingredientes;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public List<IngredienteDto> getIngredientes() {
        return ingredientes;
    }

    public void setIngredientes(List<IngredienteDto> ingredientes) {
        this.ingredientes = ingredientes;
    }

    @Override
    public String toString() {
        return "PedidoCreateDto{" +
                "comentario='" + comentario + '\'' +
                ", ingredientes=" + ingredientes +
                '}';
    }
}
