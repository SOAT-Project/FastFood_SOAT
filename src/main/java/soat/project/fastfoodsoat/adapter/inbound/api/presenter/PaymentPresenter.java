package soat.project.fastfoodsoat.adapter.inbound.api.presenter;

import soat.project.fastfoodsoat.adapter.inbound.api.model.response.UpdatePaymentToPaidStatusResponse;
import soat.project.fastfoodsoat.application.usecase.payment.update.UpdatePaymentToPaidStatusOutput;

public interface PaymentPresenter {

    static UpdatePaymentToPaidStatusResponse present(final UpdatePaymentToPaidStatusOutput output) {
        return new UpdatePaymentToPaidStatusResponse(
                output.value().toString(),
                output.paymentStatus(),
                output.externalReference()
        );
    }
}
