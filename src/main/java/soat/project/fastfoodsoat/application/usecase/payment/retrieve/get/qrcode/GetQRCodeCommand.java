package soat.project.fastfoodsoat.application.usecase.payment.retrieve.get.qrcode;

public record GetQRCodeCommand(String externalReference) {
    public static GetQRCodeCommand with(final String externalReference)
    {
        return new GetQRCodeCommand(externalReference);
    }
}

