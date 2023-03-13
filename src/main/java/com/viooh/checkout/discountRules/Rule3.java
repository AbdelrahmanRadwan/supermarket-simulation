package com.viooh.checkout.discountRules;

import com.viooh.checkout.model.GroupDiscount;
import com.viooh.checkout.model.Item;
import lombok.NoArgsConstructor;

import java.util.*;

@NoArgsConstructor
public class Rule3 extends AbstractRule {
    // Rule3: buy 3 (in a group of items) and the cheapest is free
    @Override
    public Double getBestSingleDiscount(final List<Item> items) {
        Double result = null;
        GroupDiscount groupDiscount = getGroupWithBestDiscount(items);
        if (groupDiscount.getBestDiscount() > 0 && !groupDiscount.getBestGroup().isEmpty()) {
            result = groupDiscount.getBestDiscount();
        }
        return result;
    }

    @Override
    public List<Item> applyBestSingleDiscount(final List<Item> items) {
        List<Item> bestGroup = getGroupWithBestDiscount(items).getBestGroup();
        List<Item> result = List.of(bestGroup.get(0), bestGroup.get(1), bestGroup.get(2));

        result.get(0).setIsItemDiscounted(true);
        result.get(1).setIsItemDiscounted(true);
        result.get(2).setIsItemDiscounted(true);
        result.get(2).setItemDiscount(bestGroup.get(2).getItemPrice());
        return result;
    }

    private GroupDiscount getGroupWithBestDiscount(final List<Item> items) {
        double bestDiscount = 0;
        List<Item> bestGroup = new ArrayList<>();

        Map<String, List<Item>> groupedItemsDict = new HashMap<>();

        for (final var item : items) {
            if (item.getIsItemDiscounted() != null || Boolean.TRUE.equals(item.getIsItemDiscounted())) {
                continue;
            }
            String itemGroup = item.getItemGroup();
            if (!groupedItemsDict.containsKey(itemGroup)) {
                groupedItemsDict.put(itemGroup, new ArrayList<>());
            }
            groupedItemsDict.get(itemGroup).add(item);
        }

        for (final var groupedItems : groupedItemsDict.values()) {
            if (groupedItems.size() < 3) {
                continue;
            }
            groupedItems.sort(Comparator.comparing(Item::getItemPrice).reversed());

            Double cheapestItemOfMostExpensiveThree = groupedItems.get(2).getItemPrice();
            if (cheapestItemOfMostExpensiveThree > bestDiscount) {
                bestDiscount = cheapestItemOfMostExpensiveThree;
                bestGroup = groupedItems;
            }
        }
        return new GroupDiscount(bestDiscount, bestGroup);
    }
}
