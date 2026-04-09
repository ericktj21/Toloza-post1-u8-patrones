package com.example.cleanpedidos.adapter.out.persistence;

import com.example.cleanpedidos.domain.entity.Pedido;
import com.example.cleanpedidos.domain.valueobject.Dinero;
import com.example.cleanpedidos.domain.valueobject.EstadoPedido;
import com.example.cleanpedidos.domain.valueobject.LineaPedido;
import com.example.cleanpedidos.domain.valueobject.PedidoId;
import com.example.cleanpedidos.usecase.port.PedidoRepositoryPort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class PedidoRepositoryAdapter implements PedidoRepositoryPort {
    private final PedidoJpaRepository jpaRepository;

    public PedidoRepositoryAdapter(PedidoJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public void guardar(Pedido pedido) {
        jpaRepository.save(toEntity(pedido));
    }

    @Override
    public Optional<Pedido> buscarPorId(PedidoId id) {
        return jpaRepository.findById(id.toString()).map(this::toDomain);
    }

    @Override
    public List<Pedido> buscarTodos() {
        return jpaRepository.findAll().stream()
                .map(this::toDomain)
                .toList();
    }

    private Pedido toDomain(PedidoJpaEntity entity) {
        Pedido pedido = new Pedido(new PedidoId(UUID.fromString(entity.getId())), entity.getClienteNombre());

        // Agregar líneas
        entity.getLineas().forEach(lineaJpa -> {
            try {
                pedido.agregarLinea(
                        lineaJpa.getProductoNombre(),
                        lineaJpa.getCantidad(),
                        new Dinero(lineaJpa.getPrecioUnitario())
                );
            } catch (Exception e) {
                // Silenciar validaciones en restauración
            }
        });

        // Restaurar estado (por reflexión)
        try {
            java.lang.reflect.Field estadoField = Pedido.class.getDeclaredField("estado");
            estadoField.setAccessible(true);
            estadoField.set(pedido, EstadoPedido.valueOf(entity.getEstado().name()));
        } catch (Exception e) {
            throw new RuntimeException("Error al restaurar estado del pedido", e);
        }

        return pedido;
    }

    private PedidoJpaEntity toEntity(Pedido pedido) {
        List<PedidoJpaEntity.LineaPedidoJpaEntity> lineasJpa = pedido.getLineas().stream()
                .map(linea -> new PedidoJpaEntity.LineaPedidoJpaEntity(
                        linea.productoNombre(),
                        linea.cantidad(),
                        linea.precioUnitario().cantidad()
                ))
                .toList();

        return new PedidoJpaEntity(
                pedido.getId().toString(),
                pedido.getClienteNombre(),
                PedidoJpaEntity.EstadoPedidoJpa.valueOf(pedido.getEstado().name()),
                lineasJpa
        );
    }
}
