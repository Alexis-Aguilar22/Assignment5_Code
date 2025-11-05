package org.example.Barnes;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class BarnesAndNobleTest {

    private BookDatabase bookDatabase;
    private BuyBookProcess buyBookProcess;
    private BarnesAndNoble barnesAndNoble;

    @BeforeEach
    void setUp() {
        bookDatabase = Mockito.mock(BookDatabase.class);
        buyBookProcess = Mockito.mock(BuyBookProcess.class);
        barnesAndNoble = new BarnesAndNoble(bookDatabase, buyBookProcess);
    }

    @DisplayName("specification-based")
    @Test
    void testGetPriceForCart_NormalPurchase() {
        Book book = new Book("1234", 10, 5);
        when(bookDatabase.findByISBN("1234")).thenReturn(book);

        Map<String, Integer> order = new HashMap<>();
        order.put("1234", 3);

        PurchaseSummary summary = barnesAndNoble.getPriceForCart(order);

        assertEquals(30, summary.getTotalPrice());
        assertTrue(summary.getUnavailable().isEmpty());
        verify(buyBookProcess, times(1)).buyBook(book, 3);
    }

    @DisplayName("specification-based")
    @Test
    void testGetPriceForCart_PartialAvailability() {
        Book book = new Book("5678", 15, 2);
        when(bookDatabase.findByISBN("5678")).thenReturn(book);

        Map<String, Integer> order = new HashMap<>();
        order.put("5678", 5);

        PurchaseSummary summary = barnesAndNoble.getPriceForCart(order);

        assertEquals(30, summary.getTotalPrice());
        assertEquals(1, summary.getUnavailable().size());
        assertEquals(3, summary.getUnavailable().get(book));
        verify(buyBookProcess, times(1)).buyBook(book, 2);
    }

    @DisplayName("structural-based")
    @Test
    void testGetPriceForCart_NullOrder() {
        PurchaseSummary summary = barnesAndNoble.getPriceForCart(null);
        assertNull(summary);
    }

    @DisplayName("structural-based")
    @Test
    void testGetPriceForCart_EmptyOrder() {
        Map<String, Integer> order = new HashMap<>();
        PurchaseSummary summary = barnesAndNoble.getPriceForCart(order);

        assertEquals(0, summary.getTotalPrice());
        assertTrue(summary.getUnavailable().isEmpty());
    }
}
