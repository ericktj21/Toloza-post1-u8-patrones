package com.example.cleanpedidos.adapter.out.persistence;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "pedidos")
public class PedidoJpaEntity {
    @Id
    private String id;

    @Column(nullable = false)
    private String clienteNombre;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private EstadoPedidoJpa estado;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "pedido_id")
    private List<LineaPedidoJpaEntity> lineas;

    public PedidoJpaEntity() {}

    public PedidoJpaEntity(String id, String clienteNombre, EstadoPedidoJpa estado, List<LineaPedidoJpaEntity> lineas) {
        this.id = id;
        this.clienteNombre = clienteNombre;
        this.estado = estado;
        this.lineas = lineas;
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getClienteNombre() {
        return clienteNombre;
    }

    public EstadoPedidoJpa getEstado() {
        return estado;
    }

    public List<LineaPedidoJpaEntity> getLineas() {
        return lineas;
    }

    // Setters
    public void setId(String id) {
        this.id = id;
    }

    public void setClienteNombre(String clienteNombre) {
        this.clienteNombre = clienteNombre;
    }

    public void setEstado(EstadoPedidoJpa estado) {
        this.estado = estado;
    }

    public void setLineas(List<LineaPedidoJpaEntity> lineas) {
        this.lineas = lineas;
    }

    @Embeddable
    public static class LineaPedidoJpaEntity {
        @Column(nullable = false)
        private String productoNombre;

        @Column(nullable = false)
        private int cantidad;

        @Column(nullable = false)
        private BigDecimal precioUnitario;

        public LineaPedidoJpaEntity() {}

        public LineaPedidoJpaEntity(String productoNombre, int cantidad, BigDecimal precioUnitario) {
            this.productoNombre = productoNombre;
            this.cantidad = cantidad;
            this.precioUnitario = precioUnitario;
        }

        public String getProductoNombre() {
            return productoNombre;
        }

        public int getCantidad() {
            return cantidad;
        }

        public BigDecimal getPrecioUnitario() {
            return precioUnitario;
        }
    }

    public enum EstadoPedidoJpa {
        BORRADOR, CONFIRMADO, ENTREGADO, CANCELADO
    }
}
