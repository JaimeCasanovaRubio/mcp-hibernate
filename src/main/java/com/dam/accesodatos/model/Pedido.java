package com.dam.accesodatos.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Entidad JPA Pedido para RA3 - Hibernate/JPA ORM
 *
 * RA3 - CRITERIO c) Definición de ficheros de mapeo:
 * Esta clase representa un pedido que pertenece a un NPC.
 * Cada pedido tiene un comentario y una lista de ingredientes.
 *
 * RELACIONES JPA:
 * - Pedido → NPC: @ManyToOne (muchos pedidos pertenecen a un NPC)
 * - Pedido → Ingredientes: @OneToMany (un pedido tiene muchos ingredientes)
 *
 * ANOTACIONES JPA UTILIZADAS:
 * - @ManyToOne: Relación muchos a uno con NPC
 * - @JoinColumn: Define la columna FK en la tabla pedidos
 * - @OneToMany: Relación uno a muchos con Ingrediente
 *
 * NOTA PEDAGÓGICA:
 * El lado "Many" de una relación @ManyToOne es el "dueño" de la relación.
 * Esto significa que la tabla 'pedidos' contiene la FK (npc_id).
 */
@Entity
@Table(name = "pedidos")
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "comentario", length = 255)
    @Size(max = 255, message = "El comentario no puede exceder 255 caracteres")
    private String comentario;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    /**
     * Relación ManyToOne con NPC.
     * 
     * ANOTACIONES:
     * - @ManyToOne: Muchos pedidos pertenecen a un NPC
     * - @JoinColumn: Define la columna FK 'npc_id' en la tabla pedidos
     * - @JsonIgnore: Evita recursión infinita al serializar a JSON
     *
     * DIFERENCIAS vs RA2 (JDBC):
     * - RA2: JOIN manual "SELECT * FROM pedidos p JOIN npcs n ON p.npc_id = n.id"
     * - RA3: Hibernate maneja el JOIN automáticamente, accedemos con
     * pedido.getNpc()
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "npc_id", nullable = false)
    @JsonIgnore
    private Npc npc;

    /**
     * Relación OneToMany con Ingrediente.
     * 
     * ANOTACIONES:
     * - mappedBy = "pedido": El campo 'pedido' en Ingrediente es el dueño
     * - cascade = CascadeType.ALL: Operaciones se propagan a ingredientes
     * - orphanRemoval = true: Ingredientes huérfanos se eliminan de la BD
     */
    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Ingrediente> ingredientes = new ArrayList<>();

    // ===== CONSTRUCTOR SIN ARGUMENTOS (OBLIGATORIO PARA JPA) =====

    /**
     * Constructor sin argumentos requerido por JPA.
     */
    public Pedido() {
        this.createdAt = LocalDateTime.now();
    }

    // ===== CONSTRUCTOR CON PARÁMETROS =====

    public Pedido(String comentario) {
        this();
        this.comentario = comentario;
    }

    public Pedido(String comentario, Npc npc) {
        this();
        this.comentario = comentario;
        this.npc = npc;
    }

    // ===== MÉTODOS HELPER PARA GESTIÓN DE RELACIONES =====

    /**
     * Añade un ingrediente al pedido.
     * Mantiene la bidireccionalidad de la relación.
     */
    public void addIngrediente(Ingrediente ingrediente) {
        ingredientes.add(ingrediente);
        ingrediente.setPedido(this);
    }

    /**
     * Elimina un ingrediente del pedido.
     */
    public void removeIngrediente(Ingrediente ingrediente) {
        ingredientes.remove(ingrediente);
        ingrediente.setPedido(null);
    }

    // ===== GETTERS Y SETTERS =====

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Npc getNpc() {
        return npc;
    }

    public void setNpc(Npc npc) {
        this.npc = npc;
    }

    public List<Ingrediente> getIngredientes() {
        return ingredientes;
    }

    public void setIngredientes(List<Ingrediente> ingredientes) {
        this.ingredientes = ingredientes;
    }

    // ===== EQUALS Y HASHCODE =====

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Pedido pedido = (Pedido) o;
        return Objects.equals(id, pedido.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    // ===== TOSTRING =====

    @Override
    public String toString() {
        return "Pedido{" +
                "id=" + id +
                ", comentario='" + comentario + '\'' +
                ", npcId=" + (npc != null ? npc.getId() : null) +
                ", numeroIngredientes=" + (ingredientes != null ? ingredientes.size() : 0) +
                ", createdAt=" + createdAt +
                '}';
    }
}
