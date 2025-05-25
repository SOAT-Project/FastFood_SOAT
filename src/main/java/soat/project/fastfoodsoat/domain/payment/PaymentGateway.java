package soat.project.fastfoodsoat.domain.payment;

import java.util.Optional;
import java.util.UUID;

public interface PaymentGateway {
    Payment create(Payment payment);
    Optional<Payment> findByExternalReference(String externalReference);
    Payment update(Payment payment);
}
