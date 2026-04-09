package com.example.cleanpedidos.domain.entity;

import com.example.cleanpedidos.domain.valueobject.Dinero;
import com.example.cleanpedidos.domain.valueobject.EstadoPedido;
import com.example.cleanpedidos.domain.valueobject.LineaPedido;
import com.example.cleanpedidos.domain.valueobject.PedidoId;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Pedido {
    private final PedidoId id;
    private final String clienteNombre;
    private final List<LineaPedido> lineas = new ArrayList<>();
    private EstadoPedido estado = EstadoPedido.BORRADOR;

    public Pedido(PedidoId id, String clienteNombre) {
        Objects.requireNonNull(id, "ID del pedido no puede ser nulo");
        if (clienteNombre == null || clienteNombre.isBlank()) {
            throw new IllegalArgumentException("El cliente es obligatorio");
        }
        this.id = id;
        this.clienteNombre = clienteNombre;
    }

    // Factory method para reconstruir desde persistencia
    public static Pedido reconstruir(PedidoId id, String clienteNombre, List<LineaPedido> lineas, EstadoPedido estado) {
        Pedido pedido = new Pedido(id, clienteNombre);
        pedido.lineas.addAll(lineas);
        pedido.estado = estado;
        return pedido;
    }

    public void agregarLinea(String producto, int cantidad, Dinero precio) {
        if (estado != EstadoPedido.BORRADOR) {
            throw new IllegalStateException("Solo se pueden agregar líneas en estado BORRADOR");
        }
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a cero");
        }
        lineas.add(new LineaPedido(producto, cantidad, precio));
    }

    public void confirmar() {
        if (lineas.isEmpty()) {
            throw new IllegalStateException("No se puede confirmar un pedido sin líneas");
        }
        this.estado = EstadoPedido.CONFIRMADO;
    }

    public Dinero calcularTotal() {
        return lineas.stream()
                .map(LineaPedido::subtotal)
                .reduce(Dinero.CERO, Dinero::sumar);
    }

    // Getters
    public PedidoId getId() {
        return id;
    }

    public String getClienteNombre() {
        return clienteNombre;
    }

    public List<LineaPedido> getLineas() {
        return Collections.unmodifiableList(lineas);
    }

    public EstadoPedido getEstado() {
        return estado;
    }
}
