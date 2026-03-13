package com.bryan.ECommerceApi.service;

import com.bryan.ECommerceApi.exception.EmptyCartException;
import com.bryan.ECommerceApi.exception.InsufficientStockException;
import com.bryan.ECommerceApi.exception.ResourceNotFoundException;
import com.bryan.ECommerceApi.model.*;
import com.bryan.ECommerceApi.model.dto.CheckoutResponseDto;
import com.bryan.ECommerceApi.model.dto.OrderResponseDto;
import com.bryan.ECommerceApi.model.dto.OrderSummaryResponseDto;
import com.bryan.ECommerceApi.model.enums.Status;
import com.bryan.ECommerceApi.repository.OrderRepo;
import com.stripe.exception.StripeException;
import org.jspecify.annotations.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class OrderService {
    private final OrderRepo orderRepo;
    private final UserService userService;
    private final CartService cartService;
    private final OrderItemService orderItemService;
    private final StripeService stripeService;
    private final ProductService productService;

    public OrderService(OrderRepo orderRepo, UserService userService, CartService cartService, OrderItemService orderItemService, StripeService stripeService, ProductService productService) {
        this.orderRepo = orderRepo;
        this.userService = userService;
        this.cartService = cartService;
        this.orderItemService = orderItemService;
        this.stripeService = stripeService;
        this.productService = productService;
    }

    private void validateStock(List<CartItem> cartItems) {
        for (CartItem item : cartItems) {
            if (item.getQuantity() > item.getProduct().getStock()) {
                throw new InsufficientStockException(item.getProduct().getName());
            }
        }
    }

    @Transactional()
    public CheckoutResponseDto checkout(String email) throws StripeException {
        User user = userService.findByEmail(email);
        Cart cart = cartService.getEntityByUser(user);
        List<CartItem> cartItems = cart.getItems();

        if(cartItems.isEmpty()) throw new EmptyCartException();

        validateStock(cartItems);

        BigDecimal total = cartItems.stream()
                .map((item) -> item.getProduct().getPrice()
                        .multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        //if we find pending order
        Optional<Order> pendingOrder = orderRepo.findByUserAndStatus(user, Status.PENDING);

        if(pendingOrder.isPresent()){
            Order order = pendingOrder.get();
            order.setTotal(total);
            orderItemService.deleteByOrder(order); // delete old items
            orderItemService.addItems(cart, order); // add new items
            String clientSecret = stripeService.updatePaymentIntent(order.getStripePaymentId(), total);
            orderRepo.save(order);
            return CheckoutResponseDto.fromEntity(order, clientSecret);
        }
        // create new order
        Order order = new Order(total, Status.PENDING, user);
        Order savedOrder = orderRepo.save(order);

        orderItemService.addItems(cart, savedOrder);

        // generate stripe payment
        String clientSecret = stripeService.createPaymentIntent(total, "usd");
        String paymentIntentId = clientSecret.split("_secret_")[0]; // client secret: pi_xxxxx_secret_xxxxx

        savedOrder.setStripePaymentId(paymentIntentId);
        orderRepo.save(savedOrder);

        return new CheckoutResponseDto(
                savedOrder.getId(),
                total,
                savedOrder.getStatus(),
                clientSecret
        );
    }

    public Page<OrderSummaryResponseDto> getAll(Pageable pageable, String email){
        User user = userService.findByEmail(email);
        return orderRepo.findByUser(user, pageable)
                .map(OrderSummaryResponseDto::fromEntity);
    }

    public OrderResponseDto getById(String email, Long id){
        User user = userService.findByEmail(email);
        Order order = orderRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Order", "id", id));

        if(!order.getUser().getId().equals(user.getId())) {
            throw new ResourceNotFoundException("Order", "id", id);
        }

        return OrderResponseDto.fromEntity(order);
    }

    private void completeOrder(Order order){
        for(OrderItem item : order.getItems()){
            productService.reduceStock(item.getId(), item.getQuantity());
        }

        cartService.cleanByUser(order.getUser());
    }

    public void markAsPaid(String paymentIntentId) {
        Order order = orderRepo.findByStripePaymentId(paymentIntentId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "paymentIntentId", paymentIntentId));
        order.setStatus(Status.PAID);
        completeOrder(order);
        orderRepo.save(order);
    }

    public void markAsFailed(String paymentIntentId) {
        Order order = orderRepo.findByStripePaymentId(paymentIntentId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "paymentIntentId", paymentIntentId));
        order.setStatus(Status.FAILED);
        orderRepo.save(order);
    }

    public Page<OrderSummaryResponseDto> getAllOrders(Pageable pageable) {
        return orderRepo.findAll(pageable)
                .map(OrderSummaryResponseDto::fromEntity);
    }
}
