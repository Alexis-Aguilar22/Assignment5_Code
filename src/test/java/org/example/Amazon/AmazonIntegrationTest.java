package org.example.Amazon;

import org.example.Amazon.Cost.DeliveryPrice;
import org.example.Amazon.Cost.ExtraCostForElectronics;
import org.example.Amazon.Cost.ItemType;
import org.example.Amazon.Cost.RegularCost;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class AmazonIntegrationTest {

    private Database database;
    private ShoppingCartAdaptor shoppingCartAdaptor;
    private Amazon amazon;

    @BeforeEach
    void setUp() {
        database = new Database();
        database.resetDatabase();

        shoppingCartAdaptor = new ShoppingCartAdaptor(database);
        amazon = new Amazon(
                shoppingCartAdaptor,
                List.of(
                        new RegularCost(),
                        new DeliveryPrice(),
                        new ExtraCostForElectronics()
                )
        );
    }

    @AfterEach
    void tearDown() {
        database.close();
    }

    @Test
    @DisplayName("specification-based: database-backed cart stores items and calculate includes regular plus delivery price")
    void shouldStoreItemAndCalculateWithDatabaseBackedCart() {
        Item notebook = new Item(ItemType.OTHER, "Notebook", 2, 15.0);

        amazon.addToCart(notebook);

        assertThat(shoppingCartAdaptor.getItems()).hasSize(1);
        assertThat(shoppingCartAdaptor.numberOfItems()).isEqualTo(1);
        assertThat(amazon.calculate()).isEqualTo(40.0);
    }

    @Test
    @DisplayName("specification-based: electronic purchase includes electronics surcharge")
    void shouldAddElectronicsSurchargeWhenElectronicItemExists() {
        Item earbuds = new Item(ItemType.ELECTRONIC, "Earbuds", 1, 120.0);

        amazon.addToCart(earbuds);

        assertThat(shoppingCartAdaptor.numberOfItems()).isEqualTo(1);
        assertThat(amazon.calculate()).isEqualTo(127.5);
    }

    @Test
    @DisplayName("structural-based: delivery fee is free when subtotal reaches one hundred dollars")
    void shouldMakeDeliveryFreeAtThreshold() {
        Item bookBundle = new Item(ItemType.OTHER, "Book Bundle", 2, 50.0);

        amazon.addToCart(bookBundle);

        assertThat(amazon.calculate()).isEqualTo(100.0);
    }

    @Test
    @DisplayName("structural-based: resetDatabase clears previously saved cart items")
    void resetDatabaseShouldClearPersistedState() {
        Item charger = new Item(ItemType.ELECTRONIC, "Charger", 1, 25.0);
        amazon.addToCart(charger);

        assertThat(shoppingCartAdaptor.numberOfItems()).isEqualTo(1);

        database.resetDatabase();

        assertThat(shoppingCartAdaptor.getItems()).isEmpty();
        assertThat(shoppingCartAdaptor.numberOfItems()).isEqualTo(0);
    }
}