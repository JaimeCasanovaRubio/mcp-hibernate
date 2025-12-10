package com.dam.accesodatos.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.Objects;

/**
 * Entidad JPA Ingrediente para RA3 - Hibernate/JPA ORM
 *
 * RA3 - CRITERIO c) Definición de ficheros de mapeo:
 * Esta clase representa un ingrediente que forma parte de un pedido.
 *
 * RELACIONES JPA:
 * - Ingrediente → Pedido: @ManyToOne (muchos ingredientes pertenecen a un
 * pedido)
 *
 * ANOTACIONES JPA UTILIZADAS:
 * - @ManyToOne: Relación muchos a uno con Pedido
 * - @JoinColumn: Define la columna FK en la tabla ingredientes
 *
 * NOTA PEDAGÓGICA:
 * Esta es la entidad más simple de la jerarquía. Solo tiene una relación
 * hacia arriba (Pedido) y no tiene colecciones propias.
 */
@Entity
@Table(name = "ingredientes")
public class Ingrediente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "nombre", nullable = false, length = 100)
    @NotBlank(message = "El nombre del ingrediente es obligatorio")
    @Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
    private String nombre;

    @Column(name = "cantidad")
    @Min(value = 1, message = "La cantidad mínima es 1")
    private Integer cantidad;

    /**
     * Relación ManyToOne con Pedido.
     * 
     * ANOTACIONES:
     * - @ManyToOne: Muchos ingredientes pertenecen a un pedido
     * - @JoinColumn: Define la columna FK 'pedido_id' en la tabla ingredientes
     * - @JsonIgnore: Evita recursión infinita al serializar a JSON
     *
     * DIFERENCIAS vs RA2 (JDBC):
     * - RA2: JOIN manual "SELECT * FROM ingredientes i JOIN pedidos p ON
     * i.pedido_id = p.id"
     * - RA3: Hibernate maneja el JOIN automáticamente
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pedido_id", nullable = false)
    @JsonIgnore
    private Pedido pedido;

    // ===== CONSTRUCTOR SIN ARGUMENTOS (OBLIGATORIO PARA JPA) =====

    /**
     * Constructor sin argumentos requerido por JPA.
     */
    public Ingrediente() {
        this.cantidad = 1; // Cantidad por defecto
    }

    // ===== CONSTRUCTORES CON PARÁMETROS =====

    public Ingrediente(String nombre) {
        this();
        this.nombre = nombre;
    }

    public Ingrediente(String nombre, Integer cantidad) {
        this();
        this.nombre = nombre;
        this.cantidad = cantidad;
    }

    public Ingrediente(String nombre, Integer cantidad, Pedido pedido) {
        this();
        this.nombre = nombre;
        this.cantidad = cantidad;
        this.pedido = pedido;
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

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public Pedido getPedido() {
        return pedido;
    }

    public void setPedido(Pedido pedido) {
        this.pedido = pedido;
    }

    // ===== EQUALS Y HASHCODE =====

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Ingrediente that = (Ingrediente) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    // ===== TOSTRING =====

    @Override
    public String toString() {
        return "Ingrediente{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", cantidad=" + cantidad +
                ", pedidoId=" + (pedido != null ? pedido.getId() : null) +
                '}';
    }
}
