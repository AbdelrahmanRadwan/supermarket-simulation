package com.viooh.checkout.dataLoader;

import com.sun.jdi.InvalidTypeException;
import com.viooh.checkout.discountRules.*;
import com.viooh.checkout.model.Item;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Data
@Slf4j
@AllArgsConstructor
public class Loader {
    final static String RULE1 = "Rule1";
    final static String RULE2 = "Rule2";
    final static String RULE3 = "Rule3";
    final static String RULE4 = "Rule4";

    final String itemsFileName;
    final String rulesFileName;
    final List<AbstractRule> rules = new ArrayList<>();
    List<Item> items = new ArrayList<>();

    public Loader(final String itemsFileName, final String rulesFileName) {
        this.itemsFileName = itemsFileName;
        this.rulesFileName = rulesFileName;
    }

    public void load() throws IOException, InvalidTypeException {
        parseRules();
        parseItems();
    }

    void parseRules() throws IOException, InvalidTypeException {
        final File rulesFile = new File(rulesFileName);
        System.out.println("Loading rules from " + rulesFile.getAbsolutePath());

        if (!rulesFile.exists()) {
            log.error("File " + rulesFile + " does not exist");
            throw new FileNotFoundException("File " + rulesFile + " does not exist");
        }

        BufferedReader rulesFileBufferedReader = new BufferedReader(new FileReader(rulesFile));
        String rulesLine = rulesFileBufferedReader.readLine();
        while (rulesLine != null) {
            String[] rulesLineTokens = rulesLine.split(",");
            if (RULE1.equals(rulesLineTokens[0])) {
                rules.add(new Rule1());
            } else if (RULE2.equals(rulesLineTokens[0])) {
                rules.add(new Rule2(rulesLineTokens[1], Double.parseDouble(rulesLineTokens[2])));
            } else if (RULE3.equals(rulesLineTokens[0])) {
                rules.add(new Rule3());
            } else if (RULE4.equals(rulesLineTokens[0])) {
                rules.add(new Rule4(Integer.parseInt(rulesLineTokens[1]), rulesLineTokens[2], Integer.parseInt(rulesLineTokens[3]), rulesLineTokens[4]));
            } else {
                log.error("Rule " + rulesLineTokens[0] + " doesn't exist");
                throw new InvalidTypeException("Rule " + rulesLineTokens[0] + " doesn't exist");
            }
            rulesLine = rulesFileBufferedReader.readLine();
        }
    }

    void parseItems() throws IOException {
        final File itemsFile = new File(itemsFileName);
        System.out.println("Loading items from " + itemsFile.getAbsolutePath());
        if (!itemsFile.exists()) {
            log.error("File " + itemsFile + " does not exist");
            throw new FileNotFoundException("File " + itemsFile + " does not exist");
        }

        BufferedReader itemsFileBufferedReader = new BufferedReader(new FileReader(itemsFile));
        String itemsLine = itemsFileBufferedReader.readLine();
        while (itemsLine != null) {
            if (itemsLine.startsWith("item-id")) {
                System.out.println("Skipping the header");
                itemsLine = itemsFileBufferedReader.readLine();
                continue;
            }
            System.out.println("Loading item");
            String[] itemsLineTokens = itemsLine.split(",");
            Item item = Item.builder().itemId(itemsLineTokens[0]).itemGroup(itemsLineTokens[1]).itemQuantity(Integer.valueOf(itemsLineTokens[2])).itemPrice(Double.valueOf(itemsLineTokens[3])).build();
            items.add(item);
            itemsLine = itemsFileBufferedReader.readLine();
        }
    }
}
