package soat.project.fastfoodsoat.domain.payment;

import soat.project.fastfoodsoat.domain.AggregateRoot;
import soat.project.fastfoodsoat.domain.exception.NotificationException;
import soat.project.fastfoodsoat.domain.order.Order;
import soat.project.fastfoodsoat.domain.validation.ValidationHandler;
import soat.project.fastfoodsoat.domain.validation.handler.Notification;

import java.math.BigDecimal;
import java.time.Instant;

public class Payment extends AggregateRoot<PaymentId>  {

    private BigDecimal value;
    private String externalReference;
    private String qrCode;
    private PaymentStatus status;
    private Order order;

    protected Payment(
            PaymentId paymentId,
            BigDecimal value,
            String externalReference,
            String qrCode,
            PaymentStatus status,
            Order order,
            Instant createdAt,
            Instant updatedAt,
            Instant deletedAt
    ) {
        super(
                paymentId,
                createdAt,
                updatedAt,
                deletedAt
        );
        this.value = value;
        this.externalReference = externalReference;
        this.qrCode = qrCode;
        this.status = status;
        this.order = order;
        this.selfValidation();
    }

    public static Payment newPayment(
            final BigDecimal value,
            final String externalReference,
            final String QRCode,
            final PaymentStatus status,
            final Order order
    ) {
        final PaymentId paymentId = null;
        final Instant now = Instant.now();
        return new Payment(
                paymentId,
                value,
                externalReference,
                QRCode,
                status,
                order,
                now,
                now,
                null
        );
    }

    public static Payment with(
            final PaymentId paymentId,
            final BigDecimal value,
            final String externalReference,
            final String QRCode,
            final PaymentStatus status,
            final Order order,
            final Instant createdAt,
            final Instant updatedAt,
            final Instant deletedAt
    ) {
        return new Payment(
                paymentId,
                value,
                externalReference,
                QRCode,
                status,
                order,
                createdAt,
                updatedAt,
                deletedAt
        );
    }

    public static Payment from(
            final Payment payment
    ) {
        return new Payment(
                payment.getId(),
                payment.value,
                payment.externalReference,
                payment.qrCode,
                payment.status,
                payment.order,
                payment.getCreatedAt(),
                payment.getUpdatedAt(),
                payment.getDeletedAt()
        );
    }

    public Payment update(
            final BigDecimal value,
            final String externalReference,
            final String qrCode,
            final PaymentStatus status
    ) {
        this.value = value;
        this.externalReference = externalReference;
        this.qrCode = qrCode;
        this.status = status;
        this.selfValidation();
        return this;
    }

    @Override
    public void validate(ValidationHandler handler) {
        new PaymentValidator(this, handler).validate();
    }

    private void selfValidation() {
        final Notification notification = Notification.create();

        this.validate(notification);

        if (notification.hasError()) {
            throw new NotificationException("failed to create payment", notification);
        }
    }

    public BigDecimal getValue() {
        return value;
    }

    public String getExternalReference() {
        return externalReference;
    }

    public String getQrCode() {
        return qrCode;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public Order getOrder() {
        return order;
    }
}
