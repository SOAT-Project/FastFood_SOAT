package soat.project.fastfoodsoat.application.usecase.order.create;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Component;
import soat.project.fastfoodsoat.domain.client.Client;
import soat.project.fastfoodsoat.domain.client.ClientGateway;
import soat.project.fastfoodsoat.domain.client.ClientId;
import soat.project.fastfoodsoat.domain.exception.NotFoundException;
import soat.project.fastfoodsoat.domain.exception.NotificationException;
import soat.project.fastfoodsoat.domain.order.Order;
import soat.project.fastfoodsoat.domain.order.OrderGateway;
import soat.project.fastfoodsoat.domain.order.OrderPublicId;
import soat.project.fastfoodsoat.domain.order.OrderStatus;
import soat.project.fastfoodsoat.domain.order.orderproduct.OrderProduct;
import soat.project.fastfoodsoat.domain.product.Product;
import soat.project.fastfoodsoat.domain.product.ProductGateway;
import soat.project.fastfoodsoat.domain.product.ProductId;
import soat.project.fastfoodsoat.domain.validation.handler.Notification;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static java.util.Objects.requireNonNull;

@Transactional
@Component
public class DefaultCreateOrderUseCase extends CreateOrderUseCase {

    private final OrderGateway orderGateway;
    private final ProductGateway productGateway;
    private final ClientGateway clientGateway;

    public DefaultCreateOrderUseCase(final OrderGateway orderGateway,
                                     final ProductGateway productGateway,
                                     final ClientGateway clientGateway) {
        this.orderGateway = requireNonNull(orderGateway);
        this.productGateway = requireNonNull(productGateway);
        this.clientGateway = clientGateway;
    }

    @Override
    public CreateOrderOutput execute(final CreateOrderCommand command) {
        final Notification notification = Notification.create();
        System.out.println("CreateOrderCommand: " + command);

        final var clientId = ClientId.of(command.clientId());
        final var client = clientGateway.findById(clientId)
                .orElseThrow(() -> NotFoundException.with(Client.class, clientId));


        final List<CreateOrderProductCommand> orderProducts = command.orderProducts();

        if (orderProducts.isEmpty()) {
            throw new NotificationException("Order must have at least one product", notification);
        }

        final List<Product> products = productGateway.findByIds(
                orderProducts.stream()
                        .map(CreateOrderProductCommand::productId)
                        .toList()
        );

        final List<OrderProduct> orderProductDomains =
                orderProducts.stream()
                        .map(orderProduct -> {
                            final Product product = products.stream()
                                    .filter(p -> p.getId().getValue().equals(orderProduct.productId()))
                                    .findFirst()
                                    .orElseThrow(() ->  NotFoundException.with(Product.class, new ProductId(orderProduct.productId())));

                            final BigDecimal orderProductValue = product.getValue()
                                    .multiply(BigDecimal.valueOf(orderProduct.quantity()));

                            return OrderProduct.newOrderProduct(
                                    orderProductValue,
                                    orderProduct.quantity(),
                                    product
                            );
                        })
                        .toList();

        final BigDecimal value = Order.calculateValue(orderProductDomains);
        final Integer orderNumber = orderGateway.findLastOrderNumber() + 1;
        final UUID publicId = UUID.randomUUID();

        final Order order = notification.validate(() ->
                Order.newOrder(
                        OrderPublicId.of(publicId),
                        orderNumber,
                        OrderStatus.RECEIVED,
                        clientId,
                        value,
                        orderProductDomains
                )
        );

        if (notification.hasError()) {
            throw new NotificationException("could not create order", notification);
        }

        final Order createdOrder = orderGateway.create(order);

        return CreateOrderOutput.from(createdOrder);
    }
}
