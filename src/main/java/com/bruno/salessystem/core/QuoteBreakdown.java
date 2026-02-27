package com.bruno.salessystem.core;

import java.util.List;

public record QuoteBreakdown(
    int customerId,
    String customerName,
    List<QuoteLine> lines,
    double itemSubtotal,
    double categoryDiscount,
    double loyaltyDiscount,
    double shippingCost,
    double total
) {
    public record QuoteLine(int itemId, String itemName, String category, int quantity, double unitPrice, double lineSubtotal) {
    }
}
