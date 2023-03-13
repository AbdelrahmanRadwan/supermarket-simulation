package com.viooh.checkout.discountRules;

import com.viooh.checkout.model.Item;

import java.util.List;

public abstract class AbstractRule {

    public abstract Double getBestSingleDiscount(final List<Item> items);

    public abstract List<Item> applyBestSingleDiscount(final List<Item> items);

}
