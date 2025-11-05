package org.example.Amazon;

import org.example.Amazon.Cost.PriceRule;
import org.example.Amazon.Cost.ItemType;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AmazonIntegrationTest {

    private Database db;
    private ShoppingCartAdaptor cartAdaptor;

    @BeforeEach
    void setup() {
        db = new Database();
        db.resetDatabase();
        cartAdaptor = new ShoppingCartAdaptor(db);
    }

    @AfterEach
    void tearDown() {
        db.close();
    }

    @Test
    @DisplayName("specification-based: verify items are added and retrieved correctly from DB")
    void testAddAndRetrieveItemsFromDatabase() {
        Item item1 = new Item(ItemType.BOOK, "Novel", 2, 10.0);
        cartAdaptor.add(item1);

        List<Item> items = cartAdaptor.getItems();

        assertEquals(1, items.size());
        assertEquals("Novel", items.get(0).getName());
    }

    @Test
    @DisplayName("structural-based: verify calculate() sums up rule logic correctly")
    void testCalculateTotalWithRealComponents() {
        Item item = new Item(ItemType.ELECTRONIC, "Headphones", 2, 30.0);
        cartAdaptor.add(item);

        PriceRule rule = items -> items.stream()
                .mapToDouble(i -> i.getPricePerUnit() * i.getQuantity())
                .sum();

        Amazon amazon = new Amazon(cartAdaptor, List.of(rule));
        double result = amazon.calculate();

        assertEquals(60.0, result);
    }
}
