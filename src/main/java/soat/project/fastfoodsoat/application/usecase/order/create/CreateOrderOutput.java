package soat.project.fastfoodsoat.application.usecase.order.create;

import soat.project.fastfoodsoat.domain.order.Order;
import soat.project.fastfoodsoat.domain.payment.Payment;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record CreateOrderOutput(
        UUID publicId,
        Integer orderNumber,
        String status,
        BigDecimal value,
        CreateOrderPaymentOutput payment,
        List<CreateOrderProductOutput> orderProducts
) {
    public static CreateOrderOutput from(
            final UUID publicId,
            final Integer orderNumber,
            final String status,
            final BigDecimal value,
            final CreateOrderPaymentOutput payment,
            final List<CreateOrderProductOutput> orderProducts
    ) {
        return new CreateOrderOutput(publicId, orderNumber, status, value, payment, orderProducts);
    }

    public static CreateOrderOutput from(final Order order, final Payment payment) {
        return new CreateOrderOutput(
                order.getPublicId().getValue(),
                order.getOrderNumber(),
                order.getStatus().toString(),
                order.getValue(),
                CreateOrderPaymentOutput.from(
                        payment.getStatus().toString(),
                        payment.getExternalReference(),
                        payment.getQrCode()
                ),
                order.getOrderProducts().stream()
                        .map(CreateOrderProductOutput::from)
                        .toList()
        );
    }
}
