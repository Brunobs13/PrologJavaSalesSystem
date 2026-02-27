package com.bruno.salessystem.prolog;

import com.bruno.salessystem.core.Customer;
import com.bruno.salessystem.core.Product;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class PrologDataLoader {
    private static final Pattern CUSTOMER_PATTERN = Pattern.compile("customer\\((\\d+),\\s*'([^']+)',\\s*'([^']+)',\\s*(\\d+)\\)\\.");
    private static final Pattern ITEM_PATTERN = Pattern.compile("item\\((\\d+),\\s*'([^']+)',\\s*'([^']+)',\\s*([0-9]+(?:\\.[0-9]+)?),\\s*(\\d+)\\)\\.");
    private static final Pattern CATEGORY_DISCOUNT_PATTERN = Pattern.compile("category_discount\\('([^']+)',\\s*([0-9]+(?:\\.[0-9]+)?)\\)\\.");
    private static final Pattern LOYALTY_DISCOUNT_PATTERN = Pattern.compile("loyalty_discount\\((\\d+),\\s*([0-9]+(?:\\.[0-9]+)?)\\)\\.");
    private static final Pattern SHIPPING_COST_PATTERN = Pattern.compile("shipping_cost\\('([^']+)',\\s*([0-9]+(?:\\.[0-9]+)?)\\)\\.");

    private PrologDataLoader() {
    }

    public static PrologKnowledgeBase load(Path filePath) throws IOException {
        Map<Integer, Customer> customers = new LinkedHashMap<>();
        Map<Integer, Product> products = new LinkedHashMap<>();
        Map<String, Double> categoryDiscounts = new LinkedHashMap<>();
        NavigableMap<Integer, Double> loyaltyDiscounts = new TreeMap<>();
        Map<String, Double> shippingCosts = new LinkedHashMap<>();

        try (BufferedReader reader = Files.newBufferedReader(filePath, StandardCharsets.UTF_8)) {
            String line;
            while ((line = reader.readLine()) != null) {
                String normalized = line.trim();
                if (normalized.isEmpty() || normalized.startsWith("%")) {
                    continue;
                }

                parseCustomer(normalized, customers);
                parseProduct(normalized, products);
                parseCategoryDiscount(normalized, categoryDiscounts);
                parseLoyaltyDiscount(normalized, loyaltyDiscounts);
                parseShippingCost(normalized, shippingCosts);
            }
        }

        if (customers.isEmpty() || products.isEmpty()) {
            throw new IOException("Failed to load baseline data from Prolog source file: " + filePath);
        }

        return new PrologKnowledgeBase(customers, products, categoryDiscounts, loyaltyDiscounts, shippingCosts);
    }

    private static void parseCustomer(String line, Map<Integer, Customer> customers) {
        Matcher matcher = CUSTOMER_PATTERN.matcher(line);
        if (!matcher.matches()) {
            return;
        }

        int id = Integer.parseInt(matcher.group(1));
        String name = matcher.group(2);
        String district = matcher.group(3);
        int loyaltyYears = Integer.parseInt(matcher.group(4));
        customers.put(id, new Customer(id, name, district, loyaltyYears));
    }

    private static void parseProduct(String line, Map<Integer, Product> products) {
        Matcher matcher = ITEM_PATTERN.matcher(line);
        if (!matcher.matches()) {
            return;
        }

        int id = Integer.parseInt(matcher.group(1));
        String name = matcher.group(2);
        String category = matcher.group(3).toLowerCase(Locale.ROOT);
        double price = Double.parseDouble(matcher.group(4));
        int stock = Integer.parseInt(matcher.group(5));

        products.put(id, new Product(id, name, category, price, stock));
    }

    private static void parseCategoryDiscount(String line, Map<String, Double> categoryDiscounts) {
        Matcher matcher = CATEGORY_DISCOUNT_PATTERN.matcher(line);
        if (!matcher.matches()) {
            return;
        }

        categoryDiscounts.put(
            matcher.group(1).toLowerCase(Locale.ROOT),
            Double.parseDouble(matcher.group(2))
        );
    }

    private static void parseLoyaltyDiscount(String line, NavigableMap<Integer, Double> loyaltyDiscounts) {
        Matcher matcher = LOYALTY_DISCOUNT_PATTERN.matcher(line);
        if (!matcher.matches()) {
            return;
        }

        loyaltyDiscounts.put(
            Integer.parseInt(matcher.group(1)),
            Double.parseDouble(matcher.group(2))
        );
    }

    private static void parseShippingCost(String line, Map<String, Double> shippingCosts) {
        Matcher matcher = SHIPPING_COST_PATTERN.matcher(line);
        if (!matcher.matches()) {
            return;
        }

        shippingCosts.put(
            matcher.group(1).toLowerCase(Locale.ROOT),
            Double.parseDouble(matcher.group(2))
        );
    }
}
