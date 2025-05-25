package soat.project.fastfoodsoat.domain.payment;

import soat.project.fastfoodsoat.domain.validation.DomainError;
import soat.project.fastfoodsoat.domain.validation.ValidationHandler;
import soat.project.fastfoodsoat.domain.validation.Validator;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

public class PaymentValidator extends Validator {

    private final Payment payment;

    public PaymentValidator(final Payment payment, final ValidationHandler handler) {
        super(handler);
        this.payment = payment;
    }

    @Override
    public void validate() {
        checkValueConstraints();
        checkStatusConstraints();
    }

    private void checkValueConstraints() {
        final BigDecimal value = this.payment.getValue();
        if (Objects.isNull(value)) {
            this.validationHandler().append(new DomainError("'value' should not be null"));
            return;
        }

        if (value.compareTo(BigDecimal.ZERO) <= 0) {
            this.validationHandler().append(new DomainError("'value' should be greater than zero"));
        }
    }

    private void checkStatusConstraints() {
        final PaymentStatus status = this.payment.getStatus();
        if (Objects.isNull(status)) {
            this.validationHandler().append(new DomainError("'status' should not be null"));
        }
    }
}
