package com.viooh.checkout;

import com.viooh.checkout.dataLoader.Loader;
import com.viooh.checkout.discountRules.AbstractRule;
import com.viooh.checkout.model.Item;
import lombok.extern.slf4j.Slf4j;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
public class Main {

    public static void main(String[] args) throws Exception {
        final String ITEMS_FILE_NAME = (args.length > 0) ? args[0] : "items.csv";
        final String RULES_FILE_NAME = (args.length > 1) ? args[1] : "rules.csv";
        final Loader loader = new Loader(ITEMS_FILE_NAME, RULES_FILE_NAME);
        checkout(loader);
    }

    static void checkout(final Loader loader) throws Exception {
        loader.load();
        List<Item> checkoutItems = loader.getItems();
        List<AbstractRule> rules = loader.getRules();

        if (checkoutItems == null) {
            String errorMessage = "Error: could not load items from " + loader.getItemsFileName();
            log.error(errorMessage);
            throw new Exception(errorMessage);
        }
        if (rules == null) {
            String errorMessage = "Error: could not load rules from " + loader.getRulesFileName();
            log.error(errorMessage);
            throw new Exception(errorMessage);
        }
        System.out.println("Successfully Loaded checkout items and rules");

        System.out.println("Transforming items");
        List<Item> expandedCheckoutItems = checkoutItems.stream()
                .flatMap(checkoutItem -> IntStream.range(0, checkoutItem.getItemQuantity())
                        .mapToObj(i -> Item.builder()
                                .itemId(checkoutItem.getItemId())
                                .itemGroup(checkoutItem.getItemGroup())
                                .itemPrice(checkoutItem.getItemPrice())
                                .itemQuantity(1)
                                .build()))
                .collect(Collectors.toList());

        double totalDiscount = 0.0;
        double bestRuleDiscount = 0.0;

        System.out.println();
        System.out.println("======= APPLYING RULES ============");
        AbstractRule bestRule;
        do {
            bestRule = null;
            for (var rule : rules) {
                Double roundDiscount = rule.getBestSingleDiscount(expandedCheckoutItems);
                if (roundDiscount != null && roundDiscount > bestRuleDiscount) {
                    bestRuleDiscount = roundDiscount;
                    bestRule = rule;
                }
            }

            if (bestRule != null) {
                List<Item> discountedItems = bestRule.applyBestSingleDiscount(expandedCheckoutItems);
                totalDiscount = totalDiscount + bestRuleDiscount;
                printDiscount(discountedItems, bestRule);
            }
            bestRuleDiscount = 0.0;
        } while (bestRule != null);

        printReceipt(expandedCheckoutItems, totalDiscount);
    }

    public static void printDiscount(final List<Item> items, final AbstractRule rule) {
        System.out.println("Applied discount " + rule.getClass().getSimpleName());
        for (Item i : items) {
            System.out.println("    -    " + i);
        }
    }

    public static void printReceipt(final List<Item> expandedCheckoutItems, final double totalDiscount) {
        Double total = 0.0;
        expandedCheckoutItems.sort(Comparator.comparing(Item::getItemId));
        System.out.println("========== RECEIPT ===============");
        for (Item item : expandedCheckoutItems) {
            total += item.getItemPrice();
            System.out.print(item.getItemId());
            System.out.print(" - ");
            System.out.print(item.getItemGroup());
            System.out.print(" PRICE: ");
            System.out.print(item.getItemPrice());
            if (item.getItemDiscount() != null) {
                System.out.print(" DISCOUNT: ");
                System.out.print(item.getItemDiscount());
            }
            System.out.println("");
        }
        System.out.println("==================================");
        System.out.println("Total: " + total);
        System.out.println("Discount: " + totalDiscount);
        System.out.println("Grand Total: " + (total - totalDiscount));
        System.out.println("==================================");
    }
}
