package soat.project.fastfoodsoat.application.usecase.order;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import soat.project.fastfoodsoat.IntegrationTest;
import soat.project.fastfoodsoat.adapter.outbound.jpa.mapper.OrderJpaMapper;
import soat.project.fastfoodsoat.adapter.outbound.jpa.mapper.ProductMapper;
import soat.project.fastfoodsoat.adapter.outbound.jpa.repository.OrderProductRepository;
import soat.project.fastfoodsoat.adapter.outbound.jpa.repository.OrderRepository;
import soat.project.fastfoodsoat.adapter.outbound.jpa.repository.ProductRepository;
import soat.project.fastfoodsoat.application.usecase.order.retrieve.list.DefaultListOrderUseCase;
import soat.project.fastfoodsoat.domain.order.Order;
import soat.project.fastfoodsoat.domain.order.OrderPublicId;
import soat.project.fastfoodsoat.domain.order.OrderStatus;
import soat.project.fastfoodsoat.domain.order.orderproduct.OrderProduct;
import soat.project.fastfoodsoat.domain.product.Product;
import soat.project.fastfoodsoat.domain.product.productCategory.ProductCategoryId;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@IntegrationTest
public class ListOrderUseCaseIT {

    @Autowired
    private DefaultListOrderUseCase useCase;

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

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17-alpine")
            .withDatabaseName("testdb")
            .withUsername("testuser")
            .withPassword("testpass");

    @DynamicPropertySource
    public static void setDatasourceProperties(final DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Test
    void givenValidQuery_whenCallsListOrders_shouldReturnOrders() {
        // Given
        assertEquals(0, orderRepository.count());
        assertEquals(0, orderProductRepository.count());
        assertEquals(0, productRepository.count());

        final var product1 = Product.newProduct(
                "X-Tudo",
                "Completo",
                BigDecimal.valueOf(34.99),
                "url",
                ProductCategoryId.of(1)
        );

        final var product2 = Product.newProduct(
                "X-Salada",
                "Leve",
                BigDecimal.valueOf(29.99),
                "url",
                ProductCategoryId.of(1)
        );

        productRepository.saveAndFlush(ProductMapper.fromDomain(product1));
        productRepository.saveAndFlush(ProductMapper.fromDomain(product2));

        final var orderProduct1 = OrderProduct.newOrderProduct(
                product1.getValue(),
                1,
                product1
        );

        final var orderProduct2 = OrderProduct.newOrderProduct(
                product2.getValue().multiply(BigDecimal.valueOf(2)),
                2,
                product2
        );

        final var order = Order.newOrder(
                OrderPublicId.of(UUID.randomUUID()),
                1,
                OrderStatus.COMPLETED,
                orderProduct1.getValue().add(orderProduct2.getValue()),
                List.of(orderProduct1, orderProduct2),
                null
        );

        orderRepository.saveAndFlush(OrderJpaMapper.toJpa(order));
    }


}
