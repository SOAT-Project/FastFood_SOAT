package soat.project.fastfoodsoat.domain.order;

import soat.project.fastfoodsoat.domain.AggregateRoot;
import soat.project.fastfoodsoat.domain.PublicIdentifier;
import soat.project.fastfoodsoat.domain.exception.NotificationException;
import soat.project.fastfoodsoat.domain.order.orderproduct.OrderProduct;
import soat.project.fastfoodsoat.domain.validation.ValidationHandler;
import soat.project.fastfoodsoat.domain.validation.handler.Notification;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class Order extends AggregateRoot<OrderId> {

    private OrderPublicId publicId;
    private BigDecimal value;
    private Integer orderNumber;
    private OrderStatus status;
    private final List<OrderProduct> orderProducts = new ArrayList<>();

    private Order(
            final OrderId orderId,
            final OrderPublicId publicId,
            final BigDecimal value,
            final Integer orderNumber,
            final OrderStatus status,
            final List<OrderProduct> orderProducts,
            final Instant createdAt,
            final Instant updatedAt,
            final Instant deletedAt
    ) {
        super(
                orderId,
                createdAt,
                updatedAt,
                deletedAt
        );
        this.publicId = publicId;
        this.value = value;
        this.orderNumber = orderNumber;
        this.status = status;
        this.orderProducts.addAll(orderProducts);
        this.selfValidation();
    }

    public static Order newOrder(
            final BigDecimal value,
            final Integer orderNumber,
            final OrderStatus status,
            final List<OrderProduct> orderProducts
    ) {
        final OrderId orderId = null;
        final OrderPublicId publicId = null;
        final Instant now = Instant.now();
        return new Order(
                orderId,
                publicId,
                value,
                orderNumber,
                status,
                orderProducts,
                now,
                now,
                null
        );
    }

    public static Order with(
            final OrderId orderId,
            final OrderPublicId publicId,
            final BigDecimal value,
            final Integer orderNumber,
            final OrderStatus status,
            final List<OrderProduct> orderProducts,
            final Instant createdAt,
            final Instant updatedAt,
            final Instant deletedAt
    ) {
        return new Order(
                orderId,
                publicId,
                value,
                orderNumber,
                status,
                orderProducts,
                createdAt,
                updatedAt,
                deletedAt
        );
    }

    public static Order from(Order order) {
        return new Order(
                order.id,
                order.publicId,
                order.value,
                order.orderNumber,
                order.status,
                order.orderProducts,
                order.createdAt,
                order.updatedAt,
                order.deletedAt
        );
    }

    public Order update(
            final BigDecimal value,
            final Integer orderNumber,
            final OrderStatus status
    ) {
        this.value = value;
        this.orderNumber = orderNumber;
        this.status = status;
        this.selfValidation();
        return this;
    }

    @Override
    public void validate(final ValidationHandler handler) {
        new OrderValidator(this, handler).validate();
    }

    private void selfValidation() {
        final var notification = Notification.create();

        this.validate(notification);

        if (notification.hasError())
            throw new NotificationException("failed to create order", notification);
    }

    public BigDecimal getValue() {
        return value;
    }

    public Integer getOrderNumber() {
        return orderNumber;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public List<OrderProduct> getOrderProducts() {
        return orderProducts;
    }

    public PublicIdentifier getPublicId() {
        return publicId;
    }
}
