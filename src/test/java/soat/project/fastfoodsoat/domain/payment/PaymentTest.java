package soat.project.fastfoodsoat.domain.payment;

import org.junit.jupiter.api.Test;
import soat.project.fastfoodsoat.domain.exception.NotificationException;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class PaymentTest {

    @Test
    void givenValidParams_whenCreatePayment_thenInstantiatePayment() {
        final BigDecimal value = BigDecimal.valueOf(25.00);
        final String externalReference = "externalReference";
        final String QRCode = "QRCode";
        final PaymentStatus status = PaymentStatus.PENDING;

        final Payment payment = Payment.newPayment(value, externalReference, QRCode, status, null);

        assertNotNull(payment);
        assertNull(payment.getId());
        assertNotNull(payment.getValue());
        assertNotNull(payment.getExternalReference());
        assertNotNull(payment.getQrCode());
        assertNotNull(payment.getStatus());
    }

    @Test
    void givenNullValue_whenCreatePaymentWithOrder_thenInstantiatePayment() {
        final BigDecimal value = null;
        final String externalReference = "externalReference";
        final String QRCode = "QRCode";
        final PaymentStatus status = PaymentStatus.PENDING;


        final var exception = assertThrows(NotificationException.class,
                () -> Payment.newPayment(value, externalReference, QRCode, status, null));

        assertEquals("'value' should not be null", exception.getErrors().get(0).message());
    }

    @Test
    void givenNegativeValue_whenCreatePaymentWithOrder_thenInstantiatePayment() {
        final BigDecimal value = BigDecimal.valueOf(-25.00);
        final String externalReference = "externalReference";
        final String QRCode = "QRCode";
        final PaymentStatus status = PaymentStatus.PENDING;

        final var exception = assertThrows(NotificationException.class,
                () -> Payment.newPayment(value, externalReference, QRCode, status, null));

        assertEquals("'value' should be greater than zero", exception.getErrors().get(0).message());
    }

    @Test
    void givenNullStatus_whenCreatePaymentWithOrder_thenInstantiatePayment() {
        final BigDecimal value = BigDecimal.valueOf(25.00);
        final String externalReference = "externalReference";
        final String QRCode = "QRCode";
        final PaymentStatus status = null;

        final var exception = assertThrows(NotificationException.class,
                () -> Payment.newPayment(value, externalReference, QRCode, status, null));

        assertEquals("'status' should not be null", exception.getErrors().get(0).message());
    }

    @Test
    void givenValidParams_whenUpdatePayment_thenInstantiatePayment() {
        final BigDecimal value = BigDecimal.valueOf(25.00);
        final String externalReference = "externalReference";
        final String QRCode = "QRCode";
        final PaymentStatus status = PaymentStatus.PENDING;

        final Payment payment = Payment.newPayment(value, externalReference, QRCode, status, null);

        assertNotNull(payment);
        assertNull(payment.getId());
        assertNotNull(payment.getValue());
        assertNotNull(payment.getExternalReference());
        assertNotNull(payment.getQrCode());
        assertNotNull(payment.getStatus());

        payment.update(
                BigDecimal.valueOf(50.00),
                "newExternalReference",
                "newQRCode",
                PaymentStatus.APPROVED
        );

        assertEquals(PaymentStatus.APPROVED, payment.getStatus());
    }
}