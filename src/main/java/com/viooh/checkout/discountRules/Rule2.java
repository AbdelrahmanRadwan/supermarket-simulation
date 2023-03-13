package com.viooh.checkout.discountRules;

import com.viooh.checkout.model.Item;
import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class Rule2 extends AbstractRule {
    // Rule2: buy 2 equal priced items for a special price
    final String itemId;
    final Double specialPrice;

    @Override
    public Double getBestSingleDiscount(final List<Item> items) {
        Double discount = null;
        int itemCount = 0;
        for (final var item : items) {
            if (item.getIsItemDiscounted() != null || Boolean.TRUE.equals(item.getIsItemDiscounted())) {
                continue;
            }

            if (item.getItemId().equals(itemId)) {
                itemCount++;
                if (itemCount >= 2) {
                    discount = item.getItemPrice() * 2 - specialPrice;
                    break;
                }
            }
        }
        return discount;
    }

    @Override
    public ArrayList<Item> applyBestSingleDiscount(final List<Item> items) {
        ArrayList<Item> bestDiscount = new ArrayList<>();

        int itemCount = 0;
        for (final var item : items) {
            if (item.getIsItemDiscounted() != null || Boolean.TRUE.equals(item.getIsItemDiscounted())) {
                continue;
            }

            if (item.getItemId().equals(itemId)) {
                itemCount++;
                item.setIsItemDiscounted(Boolean.TRUE);
                item.setItemDiscount(specialPrice / 2.0);
                bestDiscount.add(item);
                if (itemCount == 2) {
                    return bestDiscount;
                }
            }
        }
        return bestDiscount;
    }
}
