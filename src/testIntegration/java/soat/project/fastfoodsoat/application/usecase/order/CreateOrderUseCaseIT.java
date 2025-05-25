package soat.project.fastfoodsoat.application.usecase.order;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import soat.project.fastfoodsoat.adapter.outbound.jpa.repository.OrderProductRepository;
import soat.project.fastfoodsoat.adapter.outbound.jpa.repository.OrderRepository;
import soat.project.fastfoodsoat.adapter.outbound.jpa.repository.ProductRepository;
import soat.project.fastfoodsoat.application.usecase.order.create.CreateOrderCommand;
import soat.project.fastfoodsoat.application.usecase.order.create.CreateOrderProductCommand;
import soat.project.fastfoodsoat.application.usecase.order.create.CreateOrderUseCase;
import soat.project.fastfoodsoat.domain.exception.NotificationException;
import soat.project.fastfoodsoat.domain.product.Product;
import soat.project.fastfoodsoat.domain.product.productCategory.ProductCategoryId;
import soat.project.fastfoodsoat.setup.BaseIntegrationTest;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("integration-test")
public class CreateOrderUseCaseIT extends BaseIntegrationTest {

    @Autowired
    private CreateOrderUseCase useCase;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderProductRepository orderProductRepository;

    @Autowired
    private ProductRepository productRepository;

    @BeforeEach
    void individualTestSetup() {
        orderRepository.deleteAll();
        orderProductRepository.deleteAll();
        productRepository.deleteAll();
    }

    @Test
    void givenValidCommand_whenCreateOrder_thenShouldReturnOrderPublicId() {
        final var categoryId = ProductCategoryId.of(1);
        final var product1 = Product.newProduct(
                "X-Burger", "Delicioso", BigDecimal.valueOf(19.99), "burger.jpg", categoryId);
        final var product2 = Product.newProduct(
                "Coca-Cola", "Refrigerante", BigDecimal.valueOf(5.99), "coca.jpg", categoryId);

        final var products = List.of(
                new CreateOrderProductCommand(product1.getId().getValue(), 2),
                new CreateOrderProductCommand(product2.getId().getValue(), 1)
        );

        final var command = new CreateOrderCommand(null, products);
        final var output = useCase.execute(command);

        assertNotNull(output);
        assertNotNull(output.publicId());
        assertTrue(orderRepository.findOneByPublicId(UUID.fromString(output.publicId().toString())).isPresent());
    }

    @Test
    void givenEmptyProductList_whenCreateOrder_thenShouldThrowNotificationException() {
        final var command = new CreateOrderCommand(null, List.of());
        final var exception = assertThrows(NotificationException.class, () -> useCase.execute(command));

        assertEquals("Order must have at least one product", exception.getMessage());
    }

    @Test
    void givenInvalidProductId_whenCreateOrder_thenShouldThrowNotificationException() {
        final var products = List.of(new CreateOrderProductCommand(999, 1));
        final var command = new CreateOrderCommand(null, products);

        final var exception = assertThrows(NotificationException.class, () -> useCase.execute(command));

        assertEquals("Product with id 999 was not found", exception.getMessage());
    }

    @Test
    void givenZeroOrderValue_whenCreateOrder_thenShouldThrowNotificationException() {
        final var now = Instant.now();
        final var categoryId = ProductCategoryId.of(1);
        final var product = Product.newProduct(
                "X-Burger", "Delicioso", BigDecimal.ZERO, "burger.jpg", categoryId);

        final var products = List.of(new CreateOrderProductCommand(product.getId().getValue(), 1));
        final var command = new CreateOrderCommand(null, products);

        final var exception = assertThrows(NotificationException.class, () -> useCase.execute(command));

        assertEquals("'value' should be greater than zero", exception.getErrors().get(0).message());
    }
}
