package com.bryan.EcommerceAPi.service;

import com.bryan.ECommerceApi.service.StripeService;
import com.stripe.exception.CardException;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import com.stripe.param.PaymentIntentUpdateParams;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class StripeServiceTest {
    private String key;

    @InjectMocks
    private StripeService stripeService;

    @BeforeEach
    void init(){
        key = "My-api-key";

        ReflectionTestUtils.setField(stripeService,"secretKey", key);
    }

    @Test
    void givenValidData_whenCreatePaymentIntent_thenReturnsClientSecret() throws StripeException {
        //Arrange
        BigDecimal total = new BigDecimal(100);
        String currency = "usd";
        String expectedClientSecret= "pi_test_secret_123";
        PaymentIntent mockPaymentIntent = mock(PaymentIntent.class);
        //act - assert
        when(mockPaymentIntent.getClientSecret()).thenReturn(expectedClientSecret);

        try (MockedStatic<PaymentIntent> mockedStatic = mockStatic(PaymentIntent.class)) {
            mockedStatic
                    .when(() -> PaymentIntent.create(any(PaymentIntentCreateParams.class)))
                    .thenReturn(mockPaymentIntent);

            String result = stripeService.createPaymentIntent(total,currency);
            assertEquals(expectedClientSecret, result);
        }
    }

    @Test
    void givenValidData_whenCreatePaymentIntentFails_throwsException() {
        try (MockedStatic<PaymentIntent> mockedStatic = mockStatic(PaymentIntent.class)) {
            mockedStatic
                    .when(() -> PaymentIntent.create(any(PaymentIntentCreateParams.class)))
                    .thenThrow(new CardException("Card declined", null, null, null, null, null, null, null));

            assertThrows(StripeException.class, () ->
                    stripeService.createPaymentIntent(new BigDecimal("100.00"), "usd")
            );
        }
    }

    @Test
    void givenPaymentIntentId_whenUpdatePaymentIntent_thenReturnsClientSecret() throws StripeException {
        //arrange
        String paymentIntentId = "payment-intent-id";
        BigDecimal total = new BigDecimal(1000);

        String expectedClientSecret  = "updated-payment-intent-id";
        PaymentIntent mockPaymentIntent = mock(PaymentIntent.class);

        //act
        try (MockedStatic<PaymentIntent> mockedStatic = mockStatic(PaymentIntent.class)) {
            //act
            mockedStatic
                    .when(() -> PaymentIntent.retrieve(anyString()))
                    .thenReturn(mockPaymentIntent);
            when(mockPaymentIntent.update(any(PaymentIntentUpdateParams.class))).thenReturn(mockPaymentIntent);
            when(mockPaymentIntent.getClientSecret()).thenReturn(expectedClientSecret );
            String result = stripeService.updatePaymentIntent(paymentIntentId, total);
            //asert
            assertNotNull(result);
            assertEquals(expectedClientSecret , result);
            mockedStatic.verify(() -> PaymentIntent.retrieve(paymentIntentId));
            verify(mockPaymentIntent).update(any(PaymentIntentUpdateParams.class));
        }
    }


}
