package org.example.Amazon;

import org.example.Amazon.Cost.ItemType;
import org.example.Amazon.Cost.PriceRule;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AmazonIntegrationTest {

    private static Database database;
    private static ShoppingCartAdaptor shoppingCart;

    @BeforeAll
    static void setupAll() {
        database = new Database();
        shoppingCart = new ShoppingCartAdaptor(database);
    }

    @BeforeEach
    void resetDB() {
        database.resetDatabase();
    }

    @Test
    @DisplayName("specification-based - adding and retrieving items from database")
    @Order(1)
    void testAddAndRetrieveItemsFromDatabase() {
        Item book = new Item(ItemType.BOOK, "Java Basics", 2, 25.0);
        shoppingCart.add(book);

        var items = shoppingCart.getItems();
        assertThat(items).hasSize(1);
        assertThat(items.get(0).getName()).isEqualTo("Java Basics");
    }

    @Test
    @DisplayName("structural-based - calculate total using real components")
    @Order(2)
    void testCalculateTotalWithRealComponents() {
        PriceRule rule = items -> items.stream()
                .mapToDouble(i -> i.getQuantity() * i.getPricePerUnit())
                .sum();

        Amazon amazon = new Amazon(shoppingCart, List.of(rule));
        amazon.addToCart(new Item(ItemType.BOOK, "Effective Java", 1, 45.0));
        amazon.addToCart(new Item(ItemType.CLOTHING, "T-shirt", 2, 15.0));

        double total = amazon.calculate();
        assertThat(total).isEqualTo(75.0);
    }

    @AfterAll
    static void tearDown() {
        database.close();
    }
}
