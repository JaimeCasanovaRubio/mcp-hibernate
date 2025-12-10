package com.dam.accesodatos.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Entidad JPA Npc para RA3 - Hibernate/JPA ORM
 *
 * RA3 - CRITERIO c) Definición de ficheros de mapeo:
 * Esta clase usa anotaciones JPA para mapear el objeto Java a la tabla 'npcs'
 * de la BD.
 *
 * DIFERENCIAS vs RA2 (JDBC):
 * - RA2: POJO simple sin anotaciones, mapeo manual con ResultSet.getLong(),
 * getString(), etc.
 * - RA3: Clase anotada con @Entity, @Table, @Column - Hibernate mapea
 * automáticamente
 *
 * ANOTACIONES JPA UTILIZADAS:
 * - @Entity: Marca la clase como entidad JPA gestionada por Hibernate
 * - @Table: Mapea explícitamente a la tabla 'npcs' de la BD
 * - @Id: Marca el campo 'id' como clave primaria
 * - @GeneratedValue: El ID es autogenerado por la BD (IDENTITY strategy)
 * - @Column: Mapeo explícito de campos a columnas con restricciones
 * - @OneToMany: Relación uno a muchos con Pedido (un NPC tiene muchos pedidos)
 * - @NotBlank: Validaciones de Bean Validation
 *
 * RELACIONES JPA:
 * - NPC → Pedidos: @OneToMany con cascade ALL (operaciones en cascada)
 * - mappedBy indica que Pedido es el dueño de la relación
 *
 * NOTA PEDAGÓGICA:
 * El constructor sin argumentos es OBLIGATORIO para JPA. Hibernate lo usa
 * para crear instancias mediante reflection al recuperar datos de la BD.
 */
@Entity
@Table(name = "npcs")
public class Npc {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "nombre", nullable = false, length = 50)
    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 2, max = 50, message = "El nombre debe tener entre 2 y 50 caracteres")
    private String nombre;

    @Column(name = "activo")
    private Boolean activo;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * Relación OneToMany con Pedido.
     * 
     * ANOTACIONES:
     * - mappedBy = "npc": Indica que el campo 'npc' en Pedido es el dueño de la
     * relación
     * - cascade = CascadeType.ALL: Operaciones (persist, merge, remove) se propagan
     * a pedidos
     * - orphanRemoval = true: Si se elimina un pedido de la lista, se elimina de la
     * BD
     * - fetch = FetchType.LAZY: Los pedidos se cargan bajo demanda (mejor
     * rendimiento)
     *
     * DIFERENCIAS vs RA2 (JDBC):
     * - RA2: JOIN manual con SQL, iterar ResultSet, crear objetos manualmente
     * - RA3: Hibernate carga automáticamente los pedidos cuando se accede a la
     * lista
     */
    @OneToMany(mappedBy = "npc", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Pedido> pedidos = new ArrayList<>();

    // ===== CONSTRUCTOR SIN ARGUMENTOS (OBLIGATORIO PARA JPA) =====

    /**
     * Constructor sin argumentos requerido por JPA.
     * Hibernate lo usa para instanciar objetos al recuperar datos de la BD.
     */
    public Npc() {
        this.activo = true;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // ===== CONSTRUCTOR CON PARÁMETROS (OPCIONAL) =====

    public Npc(String nombre) {
        this(); // Llama al constructor sin argumentos
        this.nombre = nombre;
    }

    // ===== MÉTODOS HELPER PARA GESTIÓN DE RELACIONES =====

    /**
     * Añade un pedido al NPC.
     * 
     * NOTA PEDAGÓGICA:
     * Es importante mantener la bidireccionalidad de la relación.
     * Al añadir un pedido, también seteamos el NPC en el pedido.
     */
    public void addPedido(Pedido pedido) {
        pedidos.add(pedido);
        pedido.setNpc(this);
    }

    /**
     * Elimina un pedido del NPC.
     */
    public void removePedido(Pedido pedido) {
        pedidos.remove(pedido);
        pedido.setNpc(null);
    }

    // ===== GETTERS Y SETTERS =====

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<Pedido> getPedidos() {
        return pedidos;
    }

    public void setPedidos(List<Pedido> pedidos) {
        this.pedidos = pedidos;
    }

    // ===== EQUALS Y HASHCODE =====

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Npc npc = (Npc) o;
        return Objects.equals(id, npc.id) && Objects.equals(nombre, npc.nombre);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, nombre);
    }

    // ===== TOSTRING =====

    @Override
    public String toString() {
        return "Npc{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", activo=" + activo +
                ", numeroPedidos=" + (pedidos != null ? pedidos.size() : 0) +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
