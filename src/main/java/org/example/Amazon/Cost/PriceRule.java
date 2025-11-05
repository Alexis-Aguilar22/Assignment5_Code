package org.example.Amazon.Cost;

import org.example.Amazon.Item;
import java.util.List;

@FunctionalInterface
public interface PriceRule {
    double priceToAggregate(List<Item> items);
}
