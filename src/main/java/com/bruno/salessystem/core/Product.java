package com.bruno.salessystem.core;

public final class Product {
    private final int id;
    private final String name;
    private final String category;
    private final double price;
    private int stock;

    public Product(int id, String name, String category, double price, int stock) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.price = price;
        this.stock = stock;
    }

    public int id() {
        return id;
    }

    public String name() {
        return name;
    }

    public String category() {
        return category;
    }

    public double price() {
        return price;
    }

    public int stock() {
        return stock;
    }

    public void decreaseStock(int quantity) {
        this.stock -= quantity;
    }

    public Product copy() {
        return new Product(id, name, category, price, stock);
    }
}
