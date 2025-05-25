package soat.project.fastfoodsoat.application.usecase.order.create;

public record CreateOrderPaymentOutput(
        String status,
        String externalReference,
        String qrCode
) {
    public static CreateOrderPaymentOutput from(
            final String status,
            final String externalReference,
            final String qrCode
    ) {
        return new CreateOrderPaymentOutput(status, externalReference, qrCode);
    }

    public static CreateOrderPaymentOutput from(final String status) {
        return new CreateOrderPaymentOutput(status, null, null);
    }
}
