package soat.project.fastfoodsoat.adapter.outbound.jpa.mapper;

import soat.project.fastfoodsoat.adapter.outbound.jpa.entity.OrderJpaEntity;
import soat.project.fastfoodsoat.adapter.outbound.jpa.entity.PaymentJpaEntity;
import soat.project.fastfoodsoat.domain.payment.Payment;
import soat.project.fastfoodsoat.domain.payment.PaymentId;
import soat.project.fastfoodsoat.domain.payment.PaymentStatus;

import java.util.Objects;

public class PaymentJpaMapper {

    private PaymentJpaMapper() {
        // Private constructor to prevent instantiation
    }

    public static PaymentJpaEntity toJpa(final Payment payment, final OrderJpaEntity orderJpa) {
        if (payment == null) return new PaymentJpaEntity();

        return new PaymentJpaEntity(
                Objects.isNull(payment.getId()) ? null : payment.getId().getValue(),
                payment.getValue(),
                payment.getExternalReference(),
                payment.getQrCode(),
                payment.getStatus().toString(),
                orderJpa,
                payment.getCreatedAt(),
                payment.getUpdatedAt(),
                payment.getDeletedAt()
        );
    }

    public static PaymentJpaEntity toJpa(final Payment payment) {
        if (payment == null) return new PaymentJpaEntity();

        return new PaymentJpaEntity(
                Objects.isNull(payment.getId()) ? null : payment.getId().getValue(),
                payment.getValue(),
                payment.getExternalReference(),
                payment.getQrCode(),
                payment.getStatus().toString(),
                OrderJpaMapper.toJpa(payment.getOrder()),
                payment.getCreatedAt(),
                payment.getUpdatedAt(),
                payment.getDeletedAt()
        );
    }

    public static Payment fromJpa(final PaymentJpaEntity paymentJpa) {
        return Payment.with(
                PaymentId.of(paymentJpa.getId()),
                paymentJpa.getValue(),
                paymentJpa.getExternalReference(),
                paymentJpa.getQrCode(),
                PaymentStatus.valueOf(paymentJpa.getStatus()),
                paymentJpa.getOrder() != null ? OrderJpaMapper.fromJpaWithoutPayment(paymentJpa.getOrder()) : null,
                paymentJpa.getCreatedAt(),
                paymentJpa.getUpdatedAt(),
                paymentJpa.getDeletedAt()
        );
    }
}
