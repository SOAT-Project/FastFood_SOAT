package soat.project.fastfoodsoat.domain.payment;

import soat.project.fastfoodsoat.domain.Identifier;

import java.util.Objects;

public class PaymentId extends Identifier {

    private final Integer id;

    private PaymentId(final Integer id) {
        this.id = Objects.requireNonNull(id);
    }

    public static PaymentId of(final Integer id) {
        return new PaymentId(id);
    }

    @Override
    public Integer getValue() {
        return id;
    }

    @Override
    public boolean equals(final Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        final PaymentId orderId = (PaymentId) o;
        return Objects.equals(getValue(), orderId.getValue());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getValue());
    }
}
