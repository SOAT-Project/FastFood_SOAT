package soat.project.fastfoodsoat.application.usecase.order;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.util.ReflectionTestUtils;
import soat.project.fastfoodsoat.application.usecase.UseCaseTest;
import soat.project.fastfoodsoat.application.usecase.order.create.CreateOrderCommand;
import soat.project.fastfoodsoat.application.usecase.order.create.CreateOrderProductCommand;
import soat.project.fastfoodsoat.application.usecase.order.create.DefaultCreateOrderUseCase;
import soat.project.fastfoodsoat.domain.exception.NotFoundException;
import soat.project.fastfoodsoat.domain.exception.NotificationException;
import soat.project.fastfoodsoat.domain.order.OrderGateway;
import soat.project.fastfoodsoat.domain.order.OrderPublicId;
import soat.project.fastfoodsoat.domain.payment.PaymentGateway;
import soat.project.fastfoodsoat.domain.payment.PaymentService;
import soat.project.fastfoodsoat.domain.product.Product;
import soat.project.fastfoodsoat.domain.product.ProductGateway;
import soat.project.fastfoodsoat.domain.product.ProductId;
import soat.project.fastfoodsoat.domain.product.productCategory.ProductCategoryId;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CreateOrderUseCaseTest extends UseCaseTest {

    @InjectMocks
    private DefaultCreateOrderUseCase useCase;

    @Mock
    private OrderGateway orderGateway;

    @Mock
    private ProductGateway productGateway;

    @Mock
    private PaymentGateway paymentGateway;

    @Mock
    private PaymentService paymentService;

    @Override
    protected List<Object> getMocks() {
        return List.of(orderGateway, productGateway, paymentGateway, paymentService);
    }

    @Test
    void givenValidCommand_whenCreateOrderWithoutClient_thenShouldReturnOrderPlubicId() {

        final var publicId = UUID.randomUUID();
        final var now = Instant.now();
        final var categoryId = 10;
        final var drinkCategoryId = 20;
        final var products = List.of(
                new CreateOrderProductCommand(1, 2),
                new CreateOrderProductCommand(2, 3)
        );

        final var command = new CreateOrderCommand(null, products);

        when(orderGateway.findLastOrderNumber()).thenReturn(1);
        when(productGateway.findByIds(List.of(products.get(0).productId(), products.get(1).productId())))
                .thenReturn(List.of(
                        Product.with(ProductId.of(1), "X-Burger", "podrão de queijo", BigDecimal.valueOf(19.99), "burger.jpg", ProductCategoryId.of(categoryId), now, now, null),
                        Product.with(ProductId.of(2), "Coca", "bebida boa", BigDecimal.valueOf(5.99), "coca.jpg", ProductCategoryId.of(drinkCategoryId), now, now, null)
        ));
        when(orderGateway.create(any())).thenAnswer(invocation -> {
            final var order = invocation.getArgument(0);
            ReflectionTestUtils.setField(order, "publicId", OrderPublicId.of(publicId));
            return order;
        });

        when(paymentService.createDynamicQrCode(
                any(),
                any(),
                any(),
                any()
        )).thenReturn("https://example.com/qr-code");

        when(paymentGateway.create(
                any()
        )).thenAnswer(invocation -> {
            final var payment = invocation.getArgument(0);
            return payment;
        });

        final var output = useCase.execute(command);

        assertNotNull(output);
        assertEquals(publicId, output.publicId());
        verify(orderGateway, times(1)).findLastOrderNumber();
        verify(orderGateway, times(1)).create(any());
        verify(productGateway, times(1)).findByIds(any());
        verify(paymentService, times(1)).createDynamicQrCode(any(),any(),any(),any());
        verify(paymentGateway, times(1)).create(any());
    }

    @Test
    void givenEmptyQRCode_whenCreateOrderWithoutClient_thenShouldThrowNotificationException() {
        final var publicId = UUID.randomUUID();
        final var now = Instant.now();
        final var categoryId = 10;
        final var drinkCategoryId = 20;
        final var products = List.of(
                new CreateOrderProductCommand(1, 2),
                new CreateOrderProductCommand(2, 3)
        );

        final var command = new CreateOrderCommand(null, products);

        when(orderGateway.findLastOrderNumber()).thenReturn(1);
        when(productGateway.findByIds(List.of(products.get(0).productId(), products.get(1).productId())))
                .thenReturn(List.of(
                        Product.with(ProductId.of(1), "X-Burger", "podrão de queijo", BigDecimal.valueOf(19.99), "burger.jpg", ProductCategoryId.of(categoryId), now, now, null),
                        Product.with(ProductId.of(2), "Coca", "bebida boa", BigDecimal.valueOf(5.99), "coca.jpg", ProductCategoryId.of(drinkCategoryId), now, now, null)
                ));
        when(orderGateway.create(any())).thenAnswer(invocation -> {
            final var order = invocation.getArgument(0);
            ReflectionTestUtils.setField(order, "publicId", OrderPublicId.of(publicId));
            return order;
        });

        when(paymentService.createDynamicQrCode(
                any(),
                any(),
                any(),
                any()
        )).thenReturn(null);

        final var ex = assertThrows(NotificationException.class, () -> useCase.execute(command));

        verify(orderGateway, times(1)).findLastOrderNumber();
        verify(orderGateway, times(1)).create(any());
        verify(productGateway, times(1)).findByIds(any());
        verify(paymentService, times(1)).createDynamicQrCode(any(),any(),any(),any());
        verify(paymentGateway, never()).create(any());
        assertEquals("could not create qr code", ex.getMessage());
    }

    @Test
    void givenEmptyProductList_whenCreateOrderWithoutClient_thenShouldThrowNotificationException() {

        final var command = new CreateOrderCommand(null, List.of());

        final var ex = assertThrows(NotificationException.class, () -> useCase.execute(command));

        assertEquals("Order must have at least one product", ex.getMessage());
        assertTrue(ex.getErrors().isEmpty());
    }

    @Test
    void givenInvalidProductList_whenCreateOrderWithoutClient_thenShouldThrowNotFoundException() {

        final var products = List.of(
                new CreateOrderProductCommand(1, 2),
                new CreateOrderProductCommand(5, 3)
        );

        final var command = new CreateOrderCommand(null, products);

        final var ex = assertThrows(NotFoundException.class, () -> useCase.execute(command));
        assertEquals("product with id 1 was not found", ex.getMessage());
        verify(orderGateway, never()).create(any());
    }


    @Test
    void givenInvalidOrder_whenCreateOrderWithoutClient_thenShouldThrowNotificationException() {

        final var now = Instant.now();
        final var categoryId = 10;
        final var drinkCategoryId = 20;
        final var products = List.of(
                new CreateOrderProductCommand(1, 2),
                new CreateOrderProductCommand(2, 3)
        );

        final var command = new CreateOrderCommand(null, products);

        when(orderGateway.findLastOrderNumber()).thenReturn(-1);
        when(productGateway.findByIds(List.of(products.get(0).productId(), products.get(1).productId())))
                .thenReturn(List.of(
                        Product.with(ProductId.of(1), "X-Burger", "podrão de queijo", BigDecimal.valueOf(19.99), "burger.jpg", ProductCategoryId.of(categoryId), now, now, null),
                        Product.with(ProductId.of(2), "Coca", "bebida boa", BigDecimal.valueOf(5.99), "coca.jpg", ProductCategoryId.of(drinkCategoryId), now, now, null)
                ));

        final var ex = assertThrows(NotificationException.class, () -> useCase.execute(command));
        assertEquals("could not create order", ex.getMessage());
        verify(orderGateway, times(1)).findLastOrderNumber();
        verify(orderGateway, never()).create(any());
    }
}