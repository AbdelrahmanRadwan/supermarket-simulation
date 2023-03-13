package com.viooh.checkout.model;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class Item {
    String itemId;
    String itemGroup;
    Integer itemQuantity;
    Double itemPrice;
    Boolean isItemDiscounted;
    Double itemDiscount;
}
