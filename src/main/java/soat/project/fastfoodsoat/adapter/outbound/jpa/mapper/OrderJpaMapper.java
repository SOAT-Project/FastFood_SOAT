package soat.project.fastfoodsoat.adapter.outbound.jpa.mapper;

import soat.project.fastfoodsoat.adapter.outbound.jpa.entity.OrderJpaEntity;
import soat.project.fastfoodsoat.adapter.outbound.jpa.entity.OrderProductJpaEntity;
import soat.project.fastfoodsoat.adapter.outbound.jpa.entity.PaymentJpaEntity;
import soat.project.fastfoodsoat.adapter.outbound.jpa.entity.ProductJpaEntity;
import soat.project.fastfoodsoat.domain.order.Order;
import soat.project.fastfoodsoat.domain.order.OrderId;
import soat.project.fastfoodsoat.domain.order.OrderPublicId;
import soat.project.fastfoodsoat.domain.order.OrderStatus;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public final class OrderJpaMapper {

    private OrderJpaMapper() {
        // Private constructor to prevent instantiation
    }

    public static Order fromJpa(final OrderJpaEntity orderJpa) {
        return Order.with(
                OrderId.of(orderJpa.getId()),
                OrderPublicId.of(orderJpa.getPublicId()),
                orderJpa.getValue(),
                orderJpa.getOrderNumber(),
                OrderStatus.valueOf(orderJpa.getStatus()),
                orderJpa.getOrderProducts() != null ?orderJpa.getOrderProducts().stream()
                        .filter(Objects::nonNull)
                        .map(OrderProductJpaMapper::fromJpa)
                        .toList() : null,
                orderJpa.getPayment() != null ? PaymentJpaMapper.fromJpa(orderJpa.getPayment()) : null,
                orderJpa.getCreatedAt(),
                orderJpa.getUpdatedAt(),
                orderJpa.getDeletedAt()
        );
    }

    public static Order fromJpaWithoutPayment(final OrderJpaEntity orderJpa) {
        return Order.with(
                OrderId.of(orderJpa.getId()),
                OrderPublicId.of(orderJpa.getPublicId()),
                orderJpa.getValue(),
                orderJpa.getOrderNumber(),
                OrderStatus.valueOf(orderJpa.getStatus()),
                orderJpa.getOrderProducts() != null ?orderJpa.getOrderProducts().stream()
                        .filter(Objects::nonNull)
                        .map(OrderProductJpaMapper::fromJpa)
                        .toList() : null,
                null,
                orderJpa.getCreatedAt(),
                orderJpa.getUpdatedAt(),
                orderJpa.getDeletedAt()
        );
    }

    public static OrderJpaEntity toJpa(final Order order) {
        if (Objects.isNull(order)) return new OrderJpaEntity();

        return new OrderJpaEntity(
                Objects.isNull(order.getId()) ? null : order.getId().getValue(),
                Objects.isNull(order.getPublicId()) ? null : order.getPublicId().getValue(),
                order.getValue(),
                order.getOrderNumber(),
                order.getStatus().toString(),
                null,
                null,
                order.getCreatedAt(),
                order.getUpdatedAt(),
                order.getDeletedAt()
        );
    }

    public static OrderJpaEntity toJpa(final Order order, final Map<Integer, ProductJpaEntity> productsJpaMap) {
        if (Objects.isNull(order)) return new OrderJpaEntity();

        final OrderJpaEntity orderJpa = new OrderJpaEntity(
                Objects.isNull(order.getId()) ? null : order.getId().getValue(),
                Objects.isNull(order.getPublicId()) ? null : order.getPublicId().getValue(),
                order.getValue(),
                order.getOrderNumber(),
                order.getStatus().toString(),
                null,
                null,
                order.getCreatedAt(),
                order.getUpdatedAt(),
                order.getDeletedAt()
        );

        final List<OrderProductJpaEntity> orderProducts = OrderProductJpaMapper.toJpa(order.getOrderProducts(), orderJpa, productsJpaMap);
        orderJpa.setOrderProducts(orderProducts);

        return orderJpa;
    }


}
