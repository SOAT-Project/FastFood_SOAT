package soat.project.fastfoodsoat.application.usecase.payment.retrieve.get.qrcode;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Component;
import soat.project.fastfoodsoat.domain.exception.NotFoundException;
import soat.project.fastfoodsoat.domain.payment.Payment;
import soat.project.fastfoodsoat.domain.payment.PaymentGateway;
import soat.project.fastfoodsoat.domain.payment.PaymentStatus;
import soat.project.fastfoodsoat.domain.validation.DomainError;
import soat.project.fastfoodsoat.utils.QRCodeGeneratorUtils;

@Transactional
@Component
public class DefaultGetQRCodeUseCase extends GetQRCodeUseCase {

    private final PaymentGateway paymentGateway;

    public DefaultGetQRCodeUseCase(PaymentGateway paymentGateway) {
        this.paymentGateway = paymentGateway;
    }

    @Override
    public String execute(GetQRCodeCommand command) {
        final String externalReference = command.externalReference();

        final Payment payment = paymentGateway.findByExternalReference(externalReference)
                .orElseThrow(() -> NotFoundException.with(new DomainError("Payment not found for externalReference: " + externalReference)));

        if (payment.getStatus() != PaymentStatus.PENDING) {
            throw new IllegalStateException("Payment is not pending");
        }

        return QRCodeGeneratorUtils.generateQRCodeImage(
                payment.getQrCode(),
                300,
                300
        );
    }
}
