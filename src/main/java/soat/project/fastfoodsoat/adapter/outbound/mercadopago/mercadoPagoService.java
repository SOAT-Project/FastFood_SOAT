package soat.project.fastfoodsoat.adapter.outbound.mercadopago;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import soat.project.fastfoodsoat.adapter.outbound.mercadopago.model.CreateDynamicQrCodeResponse;
import soat.project.fastfoodsoat.domain.order.orderproduct.OrderProduct;
import soat.project.fastfoodsoat.domain.payment.PaymentService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.springframework.http.MediaType.APPLICATION_JSON;

@Component
public class MercadoPagoService implements PaymentService {

    private final static String QR_CODE_URL = "https://api.mercadopago.com/instore/orders/qr/seller/collectors/%s/pos/%s/qrs";

    private final String collectorId;

    private final String posId;

    private final String token;

    private final ObjectMapper objectMapper;

    public MercadoPagoService(@Value("${mercadopago.token}") String token,
                              @Value("${mercadopago.collectorId}") String collectorId,
                              @Value("${mercadopago.posId}") String posId) {
        this.token = token;
        this.collectorId = collectorId;
        this.posId = posId;
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public String createDynamicQrCode(Integer orderNumber, UUID publicId, BigDecimal value, List<OrderProduct> orderProducts) {
        try {
            final WebClient webClient = WebClient.builder()
                    .baseUrl(QR_CODE_URL.formatted(collectorId, posId))
                    .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                    .build();

            Map<String, Object> requestBody = Map.of(
                    "title", "Order " + orderNumber,
                    "description", "Order " + orderNumber,
                    "external_reference", publicId.toString(),
                    "total_amount", value,
                    "items", orderProducts.stream().map(
                            orderProduct -> Map.of(
                                    "title", orderProduct.getProduct().getName(),
                                    "total_amount", orderProduct.getValue(),
                                    "quantity", orderProduct.getQuantity(),
                                    "unit_price", orderProduct.getValue().divide(BigDecimal.valueOf(orderProduct.getQuantity())),
                                    "unit_measure", "unit"
                            )
                    ).toList()
            );

            String bodyJson = objectMapper.writeValueAsString(requestBody);

            final CreateDynamicQrCodeResponse response = webClient.post()
                    .contentType(APPLICATION_JSON)
                    .bodyValue(bodyJson)
                    .retrieve()
                    .bodyToMono(CreateDynamicQrCodeResponse.class)
                    .block();

            return response.getQrCode();
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error creating QRCode", e);
        }
    }
}
