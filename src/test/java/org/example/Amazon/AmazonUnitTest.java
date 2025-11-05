package org.example.Amazon;

import org.example.Amazon.Cost.PriceRule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class AmazonUnitTest {

    private ShoppingCart mockCart;
    private PriceRule mockRule;
    private Amazon amazon;

    @BeforeEach
    void setup() {
        mockCart = mock(ShoppingCart.class);
        mockRule = mock(PriceRule.class);
        amazon = new Amazon(mockCart, List.of(mockRule));
    }

    @Test
    @DisplayName("specification-based: verify calculate() adds up rule prices correctly")
    void testCalculatePriceFromRules() {
        when(mockCart.getItems()).thenReturn(List.of());
        when(mockRule.priceToAggregate(any())).thenReturn(50.0);

        double result = amazon.calculate();

        assertEquals(50.0, result);
        verify(mockRule, times(1)).priceToAggregate(any());
    }

    @Test
    @DisplayName("structural-based: verify addToCart() calls ShoppingCart.add()")
    void testAddToCartInvokesShoppingCart() {
        Item item = new Item(null, "Laptop", 1, 999.99);

        amazon.addToCart(item);

        verify(mockCart, times(1)).add(item);
    }
}
