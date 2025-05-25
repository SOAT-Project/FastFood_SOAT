package soat.project.fastfoodsoat.application.usecase.payment;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import soat.project.fastfoodsoat.application.usecase.UseCaseTest;
import soat.project.fastfoodsoat.application.usecase.payment.update.DefaultUpdatePaymentToPaidStatusUseCase;
import soat.project.fastfoodsoat.application.usecase.payment.update.UpdatePaymentToPaidStatusCommand;
import soat.project.fastfoodsoat.domain.exception.NotFoundException;
import soat.project.fastfoodsoat.domain.payment.Payment;
import soat.project.fastfoodsoat.domain.payment.PaymentGateway;
import soat.project.fastfoodsoat.domain.payment.PaymentStatus;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

class UpdatePaymentToPaidStatusUseCaseTest extends UseCaseTest {

    @InjectMocks
    private DefaultUpdatePaymentToPaidStatusUseCase useCase;

    @Mock
    private PaymentGateway paymentGateway;

    @Override
    protected List<Object> getMocks() {
        return List.of(paymentGateway);
    }

    @Test
    void givenValidCommand_whenUpdatePaymentToPaidStatusUseCase_thenShouldReturnUpdatedPayment() {
        final var command = new UpdatePaymentToPaidStatusCommand("123456789");

        final var payment = mock(Payment.class);
        when(payment.getStatus()).thenReturn(PaymentStatus.PENDING);
        when(payment.getValue()).thenReturn(BigDecimal.valueOf(100.0));
        when(payment.getExternalReference()).thenReturn("123456789");
        when(payment.getQrCode()).thenReturn("QRCode");
        when(paymentGateway.findByExternalReference(command.externalReference())).thenReturn(Optional.of(payment));

        final var output = useCase.execute(command);

        assertNotNull(output);
        verify(paymentGateway, times(1)).findByExternalReference(command.externalReference());
        verify(paymentGateway, times(1)).update(payment);
    }

    @Test
    void givenInvalidCommand_whenUpdatePaymentToPaidStatusUseCase_thenShouldThrowNotFoundException() {
        final var command = new UpdatePaymentToPaidStatusCommand("123456789");

        when(paymentGateway.findByExternalReference(command.externalReference())).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> useCase.execute(command));
        verify(paymentGateway, times(1)).findByExternalReference(command.externalReference());
    }

    @Test
    void givenAlreadyPaidPayment_whenUpdatePaymentToPaidStatusUseCase_thenShouldThrowIllegalStateException() {
        final var command = new UpdatePaymentToPaidStatusCommand("123456789");

        final var payment = mock(Payment.class);
        when(payment.getStatus()).thenReturn(PaymentStatus.APPROVED);
        when(paymentGateway.findByExternalReference(command.externalReference())).thenReturn(Optional.of(payment));

        assertThrows(IllegalStateException.class, () -> useCase.execute(command));
        verify(paymentGateway, times(1)).findByExternalReference(command.externalReference());
    }
}