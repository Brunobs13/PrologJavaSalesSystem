package com.bruno.salessystem.prolog;

import com.bruno.salessystem.core.Customer;
import com.bruno.salessystem.core.Product;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

public final class PrologKnowledgeBase {
    private final Map<Integer, Customer> customers;
    private final Map<Integer, Product> products;
    private final Map<String, Double> categoryDiscounts;
    private final NavigableMap<Integer, Double> loyaltyDiscounts;
    private final Map<String, Double> shippingCosts;

    public PrologKnowledgeBase(
        Map<Integer, Customer> customers,
        Map<Integer, Product> products,
        Map<String, Double> categoryDiscounts,
        NavigableMap<Integer, Double> loyaltyDiscounts,
        Map<String, Double> shippingCosts
    ) {
        this.customers = customers;
        this.products = products;
        this.categoryDiscounts = categoryDiscounts;
        this.loyaltyDiscounts = loyaltyDiscounts;
        this.shippingCosts = shippingCosts;
    }

    public Map<Integer, Customer> customers() {
        return customers;
    }

    public Map<Integer, Product> products() {
        return products;
    }

    public Map<String, Double> categoryDiscounts() {
        return categoryDiscounts;
    }

    public NavigableMap<Integer, Double> loyaltyDiscounts() {
        return loyaltyDiscounts;
    }

    public Map<String, Double> shippingCosts() {
        return shippingCosts;
    }

    public static Map<Integer, Product> deepCopyProducts(Map<Integer, Product> source) {
        Map<Integer, Product> copy = new LinkedHashMap<>();
        for (Map.Entry<Integer, Product> entry : source.entrySet()) {
            copy.put(entry.getKey(), entry.getValue().copy());
        }
        return copy;
    }

    public static NavigableMap<Integer, Double> copyLoyaltyDiscounts(NavigableMap<Integer, Double> source) {
        return new TreeMap<>(source);
    }
}
