package org.example.Amazon;

import org.example.Amazon.Cost.ItemType;
import org.example.Amazon.Cost.PriceRule;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.same;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AmazonUnitTest {

    @Mock
    private ShoppingCart shoppingCart;

    @Mock
    private PriceRule firstRule;

    @Mock
    private PriceRule secondRule;

    @Test
    @DisplayName("specification-based: calculate returns the sum of all pricing rules")
    void calculateShouldReturnSumOfAllRules() {
        List<Item> items = List.of(
                new Item(ItemType.OTHER, "Notebook", 2, 10.0)
        );

        when(shoppingCart.getItems()).thenReturn(items);
        when(firstRule.priceToAggregate(items)).thenReturn(20.0);
        when(secondRule.priceToAggregate(items)).thenReturn(5.0);

        Amazon amazon = new Amazon(shoppingCart, List.of(firstRule, secondRule));

        double total = amazon.calculate();

        assertThat(total).isEqualTo(25.0);
    }

    @Test
    @DisplayName("specification-based: addToCart delegates the item to the shopping cart")
    void addToCartShouldDelegateToShoppingCart() {
        Amazon amazon = new Amazon(shoppingCart, List.of());
        Item item = new Item(ItemType.ELECTRONIC, "Headphones", 1, 59.99);

        amazon.addToCart(item);

        verify(shoppingCart).add(item);
    }

    @Test
    @DisplayName("structural-based: calculate passes cart items to every rule")
    void calculateShouldPassSameItemListToEveryRule() {
        List<Item> items = List.of(
                new Item(ItemType.ELECTRONIC, "Tablet", 1, 120.0),
                new Item(ItemType.OTHER, "Case", 1, 20.0)
        );

        when(shoppingCart.getItems()).thenReturn(items);
        when(firstRule.priceToAggregate(items)).thenReturn(120.0);
        when(secondRule.priceToAggregate(items)).thenReturn(7.5);

        Amazon amazon = new Amazon(shoppingCart, List.of(firstRule, secondRule));

        amazon.calculate();

        verify(firstRule).priceToAggregate(same(items));
        verify(secondRule).priceToAggregate(same(items));
    }

    @Test
    @DisplayName("structural-based: calculate returns zero when there are no pricing rules")
    void calculateShouldReturnZeroWhenNoRulesExist() {
        Amazon amazon = new Amazon(shoppingCart, List.of());

        double total = amazon.calculate();

        assertThat(total).isEqualTo(0.0);
    }
}