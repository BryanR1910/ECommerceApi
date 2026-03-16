package com.bryan.EcommerceAPi.service;

import com.bryan.ECommerceApi.exception.EmptyCartException;
import com.bryan.ECommerceApi.exception.InsufficientStockException;
import com.bryan.ECommerceApi.exception.ResourceNotFoundException;
import com.bryan.ECommerceApi.model.*;
import com.bryan.ECommerceApi.model.dto.CheckoutResponseDto;
import com.bryan.ECommerceApi.model.dto.OrderResponseDto;
import com.bryan.ECommerceApi.model.dto.OrderSummaryResponseDto;
import com.bryan.ECommerceApi.model.enums.Status;
import com.bryan.ECommerceApi.repository.OrderRepo;
import com.bryan.ECommerceApi.service.CartService;
import com.bryan.ECommerceApi.service.OrderItemService;
import com.bryan.ECommerceApi.service.OrderService;
import com.bryan.ECommerceApi.service.ProductService;
import com.bryan.ECommerceApi.service.StripeService;
import com.bryan.ECommerceApi.service.UserService;
import com.stripe.exception.StripeException;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepo orderRepo;

    @Mock
    private UserService userService;

    @Mock
    private CartService cartService;

    @Mock
    private OrderItemService orderItemService;

    @Mock
    private StripeService stripeService;

    @Mock
    private ProductService productService;

    @InjectMocks
    private OrderService orderService;

    private User testUser;
    private Cart testCart;
    private List<CartItem> cartItems;
    private Product testProduct;
    private Order testOrder;
    private Pageable pageable;

    @BeforeEach
    void init() {
        testUser = new User(1L, "testuser", "test@example.com", "password", false);
        testProduct = new Product(1L, "Test Product", "Description", BigDecimal.valueOf(100), 10L, "image.jpg", "Category");
        
        CartItem cartItem = new CartItem();
        cartItem.setProduct(testProduct);
        cartItem.setQuantity(2L);
        
        cartItems = List.of(cartItem);
        
        testCart = new Cart(testUser);
        testCart.setItems(cartItems);

        OrderItem orderItem = new OrderItem();
        orderItem.setProduct(testProduct);
        orderItem.setQuantity(2L);

        testOrder = new Order(BigDecimal.valueOf(200), Status.PENDING, testUser);
        testOrder.setId(1L);
        testOrder.setItems(List.of(orderItem));
        
        pageable = PageRequest.of(0, 10);
    }

    @Test
    void givenValidEmail_whenCheckoutWithPendingOrder_thenReturnsCheckoutResponseDto() throws StripeException {
        // Arrange
        when(userService.findByEmail(testUser.getEmail())).thenReturn(testUser);
        when(cartService.getEntityByUser(testUser)).thenReturn(testCart);
        when(orderRepo.findByUserAndStatus(testUser, Status.PENDING)).thenReturn(Optional.of(testOrder));
        when(stripeService.updatePaymentIntent(testOrder.getStripePaymentId(), testOrder.getTotal())).thenReturn("client_secret");
        
        // Act
        CheckoutResponseDto result = orderService.checkout(testUser.getEmail());
        
        // Assert
        assertNotNull(result);
        assertEquals(testOrder.getId(), result.id());
        assertEquals(testOrder.getTotal(), result.total());
        assertEquals(testOrder.getStatus(), result.status());
        assertEquals("client_secret", result.clientSecret());
        verify(orderItemService).deleteByOrder(testOrder);
        verify(orderItemService).addItems(testCart, testOrder);
        verify(orderRepo).save(testOrder);
    }

    @Test
    void givenValidEmail_whenCheckoutWithoutPendingOrder_thenCreatesNewOrder() throws StripeException {
        // Arrange
        when(userService.findByEmail(testUser.getEmail())).thenReturn(testUser);
        when(cartService.getEntityByUser(testUser)).thenReturn(testCart);
        when(orderRepo.findByUserAndStatus(testUser, Status.PENDING)).thenReturn(Optional.empty());
        when(stripeService.createPaymentIntent(testOrder.getTotal(), "usd")).thenReturn("pi_123_secret_456");
        when(orderRepo.save(any(Order.class))).thenAnswer(invocation -> {
            Order order = invocation.getArgument(0);
            return order;
        });

        // Act
        CheckoutResponseDto result = orderService.checkout(testUser.getEmail());
        
        // Assert
        assertNotNull(result);
        assertEquals(testOrder.getTotal(), result.total());
        assertEquals(Status.PENDING, result.status());
        assertEquals("pi_123", result.clientSecret().split("_secret_")[0]);
        verify(orderItemService).addItems(eq(testCart), any(Order.class));
        verify(orderRepo,times(2)).save(any(Order.class));
    }

    @Test
    void givenEmptyCart_whenCheckout_thenThrowsEmptyCartException() {
        // Arrange
        Cart emptyCart = new Cart(testUser);
        emptyCart.setItems(Collections.emptyList());
        when(userService.findByEmail(testUser.getEmail())).thenReturn(testUser);
        when(cartService.getEntityByUser(testUser)).thenReturn(emptyCart);
        
        // Act & Assert
        assertThrows(EmptyCartException.class, () -> orderService.checkout(testUser.getEmail()));
    }

    @Test
    void givenInsufficientStock_whenCheckout_thenThrowsInsufficientStockException() {
        // Arrange
        Product lowStockProduct = new Product(2L, "Low Stock Product", "Description", BigDecimal.valueOf(50), 1L, "image2.jpg", "Category");
        CartItem lowStockCartItem = new CartItem();
        lowStockCartItem.setProduct(lowStockProduct);
        lowStockCartItem.setQuantity(5L); // More than available stock
        
        Cart lowStockCart = new Cart(testUser);
        lowStockCart.setItems(List.of(lowStockCartItem));
        
        when(userService.findByEmail(testUser.getEmail())).thenReturn(testUser);
        when(cartService.getEntityByUser(testUser)).thenReturn(lowStockCart);
        
        // Act & Assert
        assertThrows(InsufficientStockException.class, () -> orderService.checkout(testUser.getEmail()));
    }

    @Test
    void givenValidEmailAndPageable_whenGetAll_thenReturnsPageOfOrderSummaryResponseDto() {
        // Arrange
        when(userService.findByEmail(testUser.getEmail())).thenReturn(testUser);
        Page<Order> orderPage = new PageImpl<>(List.of(testOrder));
        when(orderRepo.findByUser(testUser, pageable)).thenReturn(orderPage);
        
        // Act
        Page<OrderSummaryResponseDto> result = orderService.getAll(pageable, testUser.getEmail());
        
        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(testOrder.getId(), result.getContent().get(0).id());
        verify(orderRepo).findByUser(testUser, pageable);
    }

    @Test
    void givenValidEmailAndOrderId_whenGetById_thenReturnsOrderResponseDto() {
        // Arrange
        when(userService.findByEmail(testUser.getEmail())).thenReturn(testUser);
        when(orderRepo.findById(testOrder.getId())).thenReturn(Optional.of(testOrder));
        
        // Act
        OrderResponseDto result = orderService.getById(testUser.getEmail(), testOrder.getId());
        
        // Assert
        assertNotNull(result);
        assertEquals(testOrder.getId(), result.id());
        assertEquals(testOrder.getTotal(), result.total());
        assertEquals(testOrder.getStatus(), result.status());
    }

    @Test
    void givenNonExistingOrderId_whenGetById_thenThrowsResourceNotFoundException() {
        // Arrange
        when(userService.findByEmail(testUser.getEmail())).thenReturn(testUser);
        when(orderRepo.findById(999L)).thenReturn(Optional.empty());
        
        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> orderService.getById(testUser.getEmail(), 999L));
    }

    @Test
    void givenOrderBelongsToOtherUser_whenGetById_thenThrowsResourceNotFoundException() {
        // Arrange
        User otherUser = new User(2L, "otheruser", "other@example.com", "password", false);
        Order otherUserOrder = new Order(BigDecimal.valueOf(100), Status.PENDING, otherUser);
        otherUserOrder.setId(2L);
        
        when(userService.findByEmail(testUser.getEmail())).thenReturn(testUser);
        when(orderRepo.findById(otherUserOrder.getId())).thenReturn(Optional.of(otherUserOrder));
        
        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> orderService.getById(testUser.getEmail(), otherUserOrder.getId()));
    }

    @Test
    void givenValidPaymentIntentId_whenMarkAsPaid_thenCompletesOrder() {
        // Arrange
        when(orderRepo.findByStripePaymentId("pi_123_secret")).thenReturn(Optional.of(testOrder));
        
        // Act
        orderService.markAsPaid("pi_123_secret");
        
        // Assert
        assertEquals(Status.PAID, testOrder.getStatus());
        verify(productService).reduceStock(testOrder.getItems().getFirst().getId(), testOrder.getItems().getFirst().getQuantity()); // quantity from cart item
        verify(cartService).cleanByUser(testUser);
        verify(orderRepo).save(testOrder);
    }

    @Test
    void givenNonExistingPaymentIntentId_whenMarkAsPaid_thenThrowsResourceNotFoundException() {
        // Arrange
        when(orderRepo.findByStripePaymentId("unknown")).thenReturn(Optional.empty());
        
        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> orderService.markAsPaid("unknown"));
    }

    @Test
    void givenValidPaymentIntentId_whenMarkAsFailed_thenSetsOrderStatusToFailed() {
        // Arrange
        when(orderRepo.findByStripePaymentId("pi_456_secret")).thenReturn(Optional.of(testOrder));
        
        // Act
        orderService.markAsFailed("pi_456_secret");
        
        // Assert
        assertEquals(Status.FAILED, testOrder.getStatus());
        verify(orderRepo).save(testOrder);
    }

    @Test
    void givenNonExistingPaymentIntentId_whenMarkAsFailed_thenThrowsResourceNotFoundException() {
        // Arrange
        when(orderRepo.findByStripePaymentId("unknown")).thenReturn(Optional.empty());
        
        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> orderService.markAsFailed("unknown"));
    }

    @Test
    void givenPageable_whenGetAllOrders_thenReturnsPageOfOrderSummaryResponseDto() {
        // Arrange
        Page<Order> orderPage = new PageImpl<>(List.of(testOrder));
        when(orderRepo.findAll(pageable)).thenReturn(orderPage);
        
        // Act
        Page<OrderSummaryResponseDto> result = orderService.getAllOrders(pageable);
        
        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(testOrder.getId(), result.getContent().get(0).id());
        verify(orderRepo).findAll(pageable);
    }
}