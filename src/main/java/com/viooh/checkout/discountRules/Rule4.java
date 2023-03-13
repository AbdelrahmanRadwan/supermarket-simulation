package com.viooh.checkout.discountRules;

import com.viooh.checkout.model.Item;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Rule4 extends AbstractRule {
    // Rule4: for each N items X, you get K items Y for free
    final int amountOfItems;
    final String itemId;
    final int amountOfFreeItems;
    final String freeItemId;

    @Override
    public Double getBestSingleDiscount(final List<Item> items) {
        double bestDiscount = 0;
        double priceOfFreeItem = 0;

        int countOfItems = 0;
        int countOfFreeItems = 0;

        for (Item item : items) {
            if (item.getIsItemDiscounted() != null || Boolean.TRUE.equals(item.getIsItemDiscounted())) {
                continue;
            }
            if (item.getItemId().equals(itemId)) {
                countOfItems++;
            } else if (item.getItemId().equals(freeItemId)) {
                countOfFreeItems++;
                priceOfFreeItem = item.getItemPrice();
            }
        }

        if (countOfItems >= amountOfItems && countOfFreeItems >= amountOfFreeItems) {
            bestDiscount = priceOfFreeItem * amountOfFreeItems;
        }
        return bestDiscount;
    }

    @Override
    public List<Item> applyBestSingleDiscount(final List<Item> items) {
        List<Item> result = new ArrayList<>();
        int countOfItems = 0;
        int countOfFreeItems = 0;

        for (Item item : items) {
            if (item.getIsItemDiscounted() != null || Boolean.TRUE.equals(item.getIsItemDiscounted())) {
                continue;
            }
            if (item.getItemId().equals(itemId) && countOfItems < amountOfItems) {
                countOfItems++;
                item.setIsItemDiscounted(true);
                result.add(item);
            }
            if (item.getItemId().equals(freeItemId) && countOfFreeItems < amountOfFreeItems) {
                countOfFreeItems++;
                item.setIsItemDiscounted(true);
                item.setItemDiscount(item.getItemPrice());
                result.add(item);
            }
        }
        return result;
    }

}
