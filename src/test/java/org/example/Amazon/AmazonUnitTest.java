package org.example.Amazon;

import org.example.Amazon.Cost.ItemType;
import org.example.Amazon.Cost.PriceRule;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class AmazonUnitTest {

    @Test
    @DisplayName("specification-based - calculate price from rules")
    void testCalculatePriceFromRules() {
        ShoppingCart cart = mock(ShoppingCart.class);
        PriceRule rule1 = items -> 20.0;
        PriceRule rule2 = items -> 30.0;

        Amazon amazon = new Amazon(cart, List.of(rule1, rule2));
        double total = amazon.calculate();

        assertThat(total).isEqualTo(50.0);
    }

    @Test
    @DisplayName("structural-based - addToCart calls ShoppingCart.add()")
    void testAddToCartInvokesShoppingCart() {
        ShoppingCart mockCart = Mockito.mock(ShoppingCart.class);
        Amazon amazon = new Amazon(mockCart, List.of());
        Item item = new Item(ItemType.BOOK, "Mocking Made Easy", 1, 10.0);

        amazon.addToCart(item);

        verify(mockCart, times(1)).add(item);
    }
}
