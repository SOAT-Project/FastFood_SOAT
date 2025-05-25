package soat.project.fastfoodsoat.domain.order;

import soat.project.fastfoodsoat.domain.AggregateRoot;
import soat.project.fastfoodsoat.domain.PublicIdentifier;
import soat.project.fastfoodsoat.domain.exception.NotificationException;
import soat.project.fastfoodsoat.domain.order.orderproduct.OrderProduct;
import soat.project.fastfoodsoat.domain.payment.Payment;
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
    private final Payment payment;

    private Order(
            final OrderId orderId,
            final OrderPublicId publicId,
            final BigDecimal value,
            final Integer orderNumber,
            final OrderStatus status,
            final List<OrderProduct> orderProducts,
            final Payment payment,
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
        this.orderNumber = orderNumber;
        this.status = status;
        this.payment = payment;
        if (orderProducts != null) this.orderProducts.addAll(orderProducts);

        if (value != null) {
            this.value = value;
        } else {
            this.value = calculateValue(orderProducts);
        }

        this.selfValidation();
    }

    public static Order newOrder(
            final OrderPublicId publicId,
            final Integer orderNumber,
            final OrderStatus status,
            final BigDecimal value,
            final List<OrderProduct> orderProducts,
            final Payment payment
    ) {
        final OrderId orderId = null;
        final Instant now = Instant.now();
        return new Order(
                orderId,
                publicId,
                value,
                orderNumber,
                status,
                orderProducts,
                payment,
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
            final Payment payment,
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
                payment,
                createdAt,
                updatedAt,
                deletedAt
        );
    }

    public static Order from(final Order order) {
        return new Order(
                order.id,
                order.publicId,
                order.value,
                order.orderNumber,
                order.status,
                order.orderProducts,
                order.payment,
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

    public Payment getPayment() {
        return payment;
    }

    public static BigDecimal calculateValue(final List<OrderProduct> orderProducts) {
        if (orderProducts == null || orderProducts.isEmpty()) return null;

        return orderProducts.stream()
                .map(OrderProduct::getValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
