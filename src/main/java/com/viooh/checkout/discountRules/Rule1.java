package com.viooh.checkout.discountRules;

import com.viooh.checkout.model.Item;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@NoArgsConstructor
public class Rule1 extends AbstractRule {
    // Rule1: buy any 3 equal priced items and pay for 2
    @Override
    public Double getBestSingleDiscount(final List<Item> items) {
        Double result = null;

        Map<Double, Integer> priceFrequency = new HashMap<>();

        for (final var item : items) {
            if (item.getIsItemDiscounted() != null || Boolean.TRUE.equals(item.getIsItemDiscounted())) {
                continue;
            }

            Double itemPrice = item.getItemPrice();
            priceFrequency.put(itemPrice, priceFrequency.getOrDefault(itemPrice, 0) + 1);

            if (priceFrequency.get(itemPrice) >= 3) {
                if (result == null || itemPrice > result) {
                    result = itemPrice;
                }
            }
        }
        return result;
    }

    @Override
    public List<Item> applyBestSingleDiscount(final List<Item> items) {
        Double bestDiscount = getBestSingleDiscount(items);
        List<Item> discountedItems = new ArrayList<>();

        int frequency = 0;
        for (var item : items) {
            if (item.getIsItemDiscounted() != null || Boolean.TRUE.equals(item.getIsItemDiscounted())) {
                continue;
            }
            if (item.getItemPrice().equals(bestDiscount)) {
                frequency++;
                item.setIsItemDiscounted(Boolean.TRUE);
                discountedItems.add(item);
                if (frequency == 3) {
                    item.setItemDiscount(bestDiscount);
                    break;
                }
            }
        }
        return discountedItems;
    }

}
