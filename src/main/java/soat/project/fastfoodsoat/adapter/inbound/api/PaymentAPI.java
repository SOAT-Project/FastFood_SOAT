package soat.project.fastfoodsoat.adapter.inbound.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import soat.project.fastfoodsoat.adapter.inbound.api.model.request.UpdatePaymentToPaidStatusRequest;
import soat.project.fastfoodsoat.adapter.inbound.api.model.response.UpdatePaymentToPaidStatusResponse;

@Tag(name="Payments")
@RequestMapping(value="payments")
public interface PaymentAPI {

    @GetMapping(value = "/{external_reference}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get payment QRCode by external reference")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Payment QRCode retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Payment not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    ResponseEntity<byte[]> getByExternalReference(@PathVariable("external_reference") String externalReference);

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Update payment to paid status")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Payment status updated successfully"),
            @ApiResponse(responseCode = "404", description = "Payment not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    UpdatePaymentToPaidStatusResponse updateToPaidStatus(@RequestBody UpdatePaymentToPaidStatusRequest request);
}
