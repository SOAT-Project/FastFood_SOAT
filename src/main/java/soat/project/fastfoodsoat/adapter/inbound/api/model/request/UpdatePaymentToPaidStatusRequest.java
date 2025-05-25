package soat.project.fastfoodsoat.adapter.inbound.api.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public record UpdatePaymentToPaidStatusRequest(
        @JsonProperty("external_reference") String externalReference
) {
}
