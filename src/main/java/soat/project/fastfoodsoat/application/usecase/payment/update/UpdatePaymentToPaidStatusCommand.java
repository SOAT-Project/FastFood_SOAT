package soat.project.fastfoodsoat.application.usecase.payment.update;

public record UpdatePaymentToPaidStatusCommand(
        String externalReference
) {
    public static UpdatePaymentToPaidStatusCommand with(final String externalReference) {
        return new UpdatePaymentToPaidStatusCommand(externalReference);
    }
}
