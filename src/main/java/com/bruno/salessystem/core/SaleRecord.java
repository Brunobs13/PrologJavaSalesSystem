package com.bruno.salessystem.core;

import java.util.List;

public record SaleRecord(
    String saleId,
    String timestampUtc,
    Customer customer,
    List<CartLine> lines,
    QuoteBreakdown quote
) {
}
