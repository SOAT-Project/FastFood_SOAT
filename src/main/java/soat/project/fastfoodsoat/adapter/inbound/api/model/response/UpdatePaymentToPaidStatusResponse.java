package soat.project.fastfoodsoat.adapter.inbound.api.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record UpdatePaymentToPaidStatusResponse(
        @JsonProperty("value") String value,
        @JsonProperty("status") String paymentStatus,
        @JsonProperty("externalReference") String externalReference
) {
}
