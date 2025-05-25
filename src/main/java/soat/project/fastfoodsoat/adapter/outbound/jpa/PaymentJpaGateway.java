package soat.project.fastfoodsoat.adapter.outbound.jpa;

import org.springframework.stereotype.Component;
import soat.project.fastfoodsoat.adapter.outbound.jpa.entity.PaymentJpaEntity;
import soat.project.fastfoodsoat.adapter.outbound.jpa.mapper.PaymentJpaMapper;
import soat.project.fastfoodsoat.adapter.outbound.jpa.repository.PaymentRepository;
import soat.project.fastfoodsoat.domain.payment.Payment;
import soat.project.fastfoodsoat.domain.payment.PaymentGateway;

import java.util.Optional;
import java.util.UUID;

@Component
public class PaymentJpaGateway implements PaymentGateway {

    private final PaymentRepository paymentRepository;

    public PaymentJpaGateway(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @Override
    public Payment create(Payment payment) {
        return save(payment);
    }

    @Override
    public Payment update(Payment payment) {
        return save(payment);
    }

    @Override
    public Optional<Payment> findByExternalReference(String externalReference) {
        return paymentRepository
                .findByExternalReference(externalReference)
                .map(PaymentJpaMapper::fromJpa);
    }

    private Payment save(Payment payment) {
        final PaymentJpaEntity paymentJpa = PaymentJpaMapper.toJpa(payment);

        return PaymentJpaMapper.fromJpa(paymentRepository.save(paymentJpa));
    }
}
