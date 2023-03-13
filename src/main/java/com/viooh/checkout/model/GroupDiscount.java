package com.viooh.checkout.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class GroupDiscount {
    double bestDiscount;
    List<Item> bestGroup;
}

