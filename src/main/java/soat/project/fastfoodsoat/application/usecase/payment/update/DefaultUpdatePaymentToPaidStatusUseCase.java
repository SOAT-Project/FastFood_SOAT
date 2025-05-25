package soat.project.fastfoodsoat.application.usecase.payment.update;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Component;
import soat.project.fastfoodsoat.domain.exception.NotFoundException;
import soat.project.fastfoodsoat.domain.payment.Payment;
import soat.project.fastfoodsoat.domain.payment.PaymentGateway;
import soat.project.fastfoodsoat.domain.payment.PaymentStatus;
import soat.project.fastfoodsoat.domain.validation.DomainError;

@Transactional
@Component
public class DefaultUpdatePaymentToPaidStatusUseCase extends UpdatePaymentToPaidStatusUseCase {

    private final PaymentGateway paymentGateway;

    public DefaultUpdatePaymentToPaidStatusUseCase(PaymentGateway paymentGateway) {
        this.paymentGateway = paymentGateway;
    }

    @Override
    public UpdatePaymentToPaidStatusOutput execute(final UpdatePaymentToPaidStatusCommand command){
        final String externalReference = command.externalReference();

        final Payment payment = paymentGateway.findByExternalReference(externalReference)
                .orElseThrow(() -> NotFoundException.with(new DomainError("Payment not found for externalReference: " + externalReference)));

        if (payment.getStatus() == PaymentStatus.APPROVED) {
            throw new IllegalStateException("Payment already approved");
        }

        payment.update(
                payment.getValue(),
                payment.getExternalReference(),
                payment.getQrCode(),
                PaymentStatus.APPROVED
        );

        paymentGateway.update(payment);

        return UpdatePaymentToPaidStatusOutput.from(
                payment.getValue(),
                payment.getStatus().toString(),
                payment.getExternalReference()
        );
    }
}
