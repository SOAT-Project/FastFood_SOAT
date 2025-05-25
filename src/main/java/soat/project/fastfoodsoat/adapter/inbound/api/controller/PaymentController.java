package soat.project.fastfoodsoat.adapter.inbound.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import soat.project.fastfoodsoat.adapter.inbound.api.PaymentAPI;
import soat.project.fastfoodsoat.adapter.inbound.api.model.request.UpdatePaymentToPaidStatusRequest;
import soat.project.fastfoodsoat.adapter.inbound.api.model.response.UpdatePaymentToPaidStatusResponse;
import soat.project.fastfoodsoat.adapter.inbound.api.presenter.PaymentPresenter;
import soat.project.fastfoodsoat.application.usecase.payment.retrieve.get.qrcode.GetQRCodeCommand;
import soat.project.fastfoodsoat.application.usecase.payment.retrieve.get.qrcode.GetQRCodeUseCase;
import soat.project.fastfoodsoat.application.usecase.payment.update.UpdatePaymentToPaidStatusCommand;
import soat.project.fastfoodsoat.application.usecase.payment.update.UpdatePaymentToPaidStatusOutput;
import soat.project.fastfoodsoat.application.usecase.payment.update.UpdatePaymentToPaidStatusUseCase;

import java.util.Base64;

@RestController
public class PaymentController implements PaymentAPI {

    private final GetQRCodeUseCase getQRCodeUseCase;
    private final UpdatePaymentToPaidStatusUseCase updatePaymentStatusUseCase;

    public PaymentController(final GetQRCodeUseCase getQRCodeUseCase, UpdatePaymentToPaidStatusUseCase updatePaymentStatusUseCase) {
        this.getQRCodeUseCase = getQRCodeUseCase;
        this.updatePaymentStatusUseCase = updatePaymentStatusUseCase;
    }

    @Override
    public ResponseEntity<byte[]> getByExternalReference(String externalReference) {
        final GetQRCodeCommand command = new GetQRCodeCommand(
                externalReference
        );

        final String qrCodeBase64 = this.getQRCodeUseCase.execute(command);

        byte[] imageBytes = Base64.getDecoder().decode(qrCodeBase64);

        return ResponseEntity.ok()
                .header("Content-Type", "image/png")
                .header("Content-Length", String.valueOf(imageBytes.length))
                .body(imageBytes);
    }

    @Override
    public UpdatePaymentToPaidStatusResponse updateToPaidStatus(UpdatePaymentToPaidStatusRequest request) {
        final UpdatePaymentToPaidStatusCommand command = new UpdatePaymentToPaidStatusCommand(
                request.externalReference()
        );

        final UpdatePaymentToPaidStatusOutput output  = this.updatePaymentStatusUseCase.execute(command);

        return PaymentPresenter.present(output);
    }
}
