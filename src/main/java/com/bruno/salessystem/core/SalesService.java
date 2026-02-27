package com.bruno.salessystem.core;

import com.bruno.salessystem.prolog.PrologKnowledgeBase;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;

public final class SalesService {
    private final Map<Integer, Customer> customers;
    private final Map<String, Double> categoryDiscounts;
    private final NavigableMap<Integer, Double> loyaltyDiscounts;
    private final Map<String, Double> shippingCosts;
    private final Map<Integer, Product> products;
    private final Map<Integer, Product> baselineProducts;
    private final List<SaleRecord> salesHistory;
    private final Map<Integer, Integer> unitsSoldByProduct;
    private final AtomicInteger quoteCounter;

    private String startedAt;
    private long requests;
    private long quotesGenerated;
    private long salesRegistered;
    private long errors;
    private double revenue;
    private double discountsGranted;

    public SalesService(PrologKnowledgeBase baseData) {
        this.customers = new LinkedHashMap<>(baseData.customers());
        this.categoryDiscounts = new LinkedHashMap<>(baseData.categoryDiscounts());
        this.loyaltyDiscounts = new TreeMap<>(baseData.loyaltyDiscounts());
        this.shippingCosts = new LinkedHashMap<>(baseData.shippingCosts());
        this.products = PrologKnowledgeBase.deepCopyProducts(baseData.products());
        this.baselineProducts = PrologKnowledgeBase.deepCopyProducts(baseData.products());
        this.salesHistory = new ArrayList<>();
        this.unitsSoldByProduct = new LinkedHashMap<>();
        this.quoteCounter = new AtomicInteger(1);

        this.startedAt = nowUtc();
        this.requests = 0;
        this.quotesGenerated = 0;
        this.salesRegistered = 0;
        this.errors = 0;
        this.revenue = 0.0;
        this.discountsGranted = 0.0;

        for (Integer productId : products.keySet()) {
            unitsSoldByProduct.put(productId, 0);
        }
    }

    public synchronized Map<String, Object> health() {
        requests++;

        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("status", "ok");
        payload.put("service", "prolog-java-sales-engine");
        payload.put("startedAt", startedAt);
        payload.put("timestamp", nowUtc());
        return payload;
    }

    public synchronized List<Map<String, Object>> listCustomers() {
        requests++;

        List<Map<String, Object>> payload = new ArrayList<>();
        for (Customer customer : customers.values()) {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("id", customer.id());
            row.put("name", customer.name());
            row.put("district", customer.district());
            row.put("loyaltyYears", customer.loyaltyYears());
            payload.add(row);
        }
        return payload;
    }

    public synchronized List<Map<String, Object>> listProducts() {
        requests++;
        return productsToJson();
    }

    public synchronized List<Map<String, Object>> listSales() {
        requests++;

        List<Map<String, Object>> payload = new ArrayList<>();
        for (SaleRecord record : salesHistory) {
            payload.add(recordToMap(record));
        }
        return payload;
    }

    public synchronized Map<String, Object> metrics() {
        requests++;
        return buildMetricsPayload();
    }

    public synchronized Map<String, Object> dashboard() {
        requests++;

        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("metrics", buildMetricsPayload());
        payload.put("inventory", productsToJson());

        List<Map<String, Object>> recentSales = new ArrayList<>();
        int max = Math.min(salesHistory.size(), 5);
        for (int i = 0; i < max; i++) {
            recentSales.add(recordToMap(salesHistory.get(i)));
        }

        payload.put("recentSales", recentSales);
        payload.put("topProducts", topProducts());
        return payload;
    }

    public synchronized Map<String, Object> quote(int customerId, List<CartLine> lines) {
        requests++;
        try {
            QuoteBreakdown quote = calculateQuote(customerId, lines, false);
            quotesGenerated++;

            Map<String, Object> payload = new LinkedHashMap<>();
            payload.put("message", "Quote generated successfully");
            payload.put("quote", quoteToMap(quote));
            payload.put("inventory", productsToJson());
            return payload;
        } catch (IllegalArgumentException exception) {
            errors++;
            throw exception;
        }
    }

    public synchronized Map<String, Object> registerSale(int customerId, List<CartLine> lines) {
        requests++;
        try {
            QuoteBreakdown quote = calculateQuote(customerId, lines, true);
            String saleId = String.format("SALE-%05d", quoteCounter.getAndIncrement());

            Customer customer = customers.get(customerId);
            SaleRecord record = new SaleRecord(
                saleId,
                nowUtc(),
                customer,
                lines,
                quote
            );
            salesHistory.add(0, record);

            for (CartLine line : lines) {
                int previous = unitsSoldByProduct.getOrDefault(line.itemId(), 0);
                unitsSoldByProduct.put(line.itemId(), previous + line.quantity());
            }

            salesRegistered++;
            revenue += quote.total();
            discountsGranted += (quote.categoryDiscount() + quote.loyaltyDiscount());

            Map<String, Object> payload = new LinkedHashMap<>();
            payload.put("message", "Sale registered successfully");
            payload.put("sale", recordToMap(record));
            payload.put("inventory", productsToJson());
            return payload;
        } catch (IllegalArgumentException exception) {
            errors++;
            throw exception;
        }
    }

    public synchronized Map<String, Object> resetSession() {
        requests++;

        products.clear();
        for (Map.Entry<Integer, Product> entry : baselineProducts.entrySet()) {
            products.put(entry.getKey(), entry.getValue().copy());
        }
        salesHistory.clear();
        quoteCounter.set(1);

        for (Integer productId : unitsSoldByProduct.keySet()) {
            unitsSoldByProduct.put(productId, 0);
        }

        startedAt = nowUtc();
        quotesGenerated = 0;
        salesRegistered = 0;
        errors = 0;
        revenue = 0.0;
        discountsGranted = 0.0;

        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("message", "Session reset completed");
        payload.put("inventory", productsToJson());
        return payload;
    }

    private QuoteBreakdown calculateQuote(int customerId, List<CartLine> lines, boolean applyStockMutation) {
        if (lines == null || lines.isEmpty()) {
            throw new IllegalArgumentException("At least one cart line is required.");
        }

        Customer customer = customers.get(customerId);
        if (customer == null) {
            throw new IllegalArgumentException("Customer not found: " + customerId);
        }

        List<QuoteBreakdown.QuoteLine> quoteLines = new ArrayList<>();
        double subtotal = 0.0;
        double categoryDiscountValue = 0.0;

        for (CartLine line : lines) {
            Product product = products.get(line.itemId());
            if (product == null) {
                throw new IllegalArgumentException("Product not found: " + line.itemId());
            }
            if (line.quantity() <= 0) {
                throw new IllegalArgumentException("Quantity must be > 0 for product " + line.itemId());
            }
            if (line.quantity() > product.stock()) {
                throw new IllegalArgumentException(
                    "Insufficient stock for product " + product.id() + ". Available: " + product.stock()
                );
            }

            double lineSubtotal = product.price() * line.quantity();
            subtotal += lineSubtotal;

            double discountRate = categoryDiscounts.getOrDefault(product.category().toLowerCase(Locale.ROOT), 0.0);
            categoryDiscountValue += lineSubtotal * discountRate;

            quoteLines.add(
                new QuoteBreakdown.QuoteLine(
                    product.id(),
                    product.name(),
                    product.category(),
                    line.quantity(),
                    round(product.price()),
                    round(lineSubtotal)
                )
            );
        }

        double loyaltyRate = 0.0;
        Map.Entry<Integer, Double> loyaltyEntry = loyaltyDiscounts.floorEntry(customer.loyaltyYears());
        if (loyaltyEntry != null) {
            loyaltyRate = loyaltyEntry.getValue();
        }

        double loyaltyDiscountValue = (subtotal - categoryDiscountValue) * loyaltyRate;
        double shipping = shippingCosts.getOrDefault(customer.district().toLowerCase(Locale.ROOT), 9.90);
        double total = subtotal - categoryDiscountValue - loyaltyDiscountValue + shipping;

        if (applyStockMutation) {
            for (CartLine line : lines) {
                Product product = products.get(line.itemId());
                product.decreaseStock(line.quantity());
            }
        }

        return new QuoteBreakdown(
            customer.id(),
            customer.name(),
            quoteLines,
            round(subtotal),
            round(categoryDiscountValue),
            round(loyaltyDiscountValue),
            round(shipping),
            round(total)
        );
    }

    private Map<String, Object> buildMetricsPayload() {
        int totalStock = 0;
        int lowStockProducts = 0;
        double inventoryValue = 0.0;

        for (Product product : products.values()) {
            totalStock += product.stock();
            inventoryValue += product.price() * product.stock();
            if (product.stock() <= 5) {
                lowStockProducts++;
            }
        }

        double avgSaleValue = salesRegistered == 0 ? 0.0 : revenue / salesRegistered;
        double quoteConversionRate = quotesGenerated == 0 ? 0.0 : (double) salesRegistered / quotesGenerated;

        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("startedAt", startedAt);
        payload.put("timestamp", nowUtc());
        payload.put("requests", requests);
        payload.put("quotesGenerated", quotesGenerated);
        payload.put("salesRegistered", salesRegistered);
        payload.put("errors", errors);
        payload.put("revenue", round(revenue));
        payload.put("discountsGranted", round(discountsGranted));
        payload.put("historyCount", salesHistory.size());
        payload.put("totalStock", totalStock);
        payload.put("inventoryValue", round(inventoryValue));
        payload.put("lowStockProducts", lowStockProducts);
        payload.put("avgSaleValue", round(avgSaleValue));
        payload.put("quoteConversionRate", round(quoteConversionRate));
        return payload;
    }

    private List<Map<String, Object>> topProducts() {
        List<Map<String, Object>> payload = new ArrayList<>();
        for (Map.Entry<Integer, Integer> entry : unitsSoldByProduct.entrySet()) {
            Product product = baselineProducts.get(entry.getKey());
            if (product == null) {
                continue;
            }

            Map<String, Object> row = new LinkedHashMap<>();
            row.put("itemId", product.id());
            row.put("itemName", product.name());
            row.put("category", product.category());
            row.put("unitsSold", entry.getValue());
            payload.add(row);
        }

        payload.sort((a, b) -> Integer.compare((Integer) b.get("unitsSold"), (Integer) a.get("unitsSold")));

        if (payload.size() > 5) {
            return new ArrayList<>(payload.subList(0, 5));
        }

        return payload;
    }

    private List<Map<String, Object>> productsToJson() {
        List<Map<String, Object>> payload = new ArrayList<>();
        for (Product product : products.values()) {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("id", product.id());
            row.put("name", product.name());
            row.put("category", product.category());
            row.put("price", round(product.price()));
            row.put("stock", product.stock());
            row.put("categoryDiscount", categoryDiscounts.getOrDefault(product.category().toLowerCase(Locale.ROOT), 0.0));
            row.put("lowStock", product.stock() <= 5);
            payload.add(row);
        }
        return payload;
    }

    private static Map<String, Object> quoteToMap(QuoteBreakdown quote) {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("customerId", quote.customerId());
        payload.put("customerName", quote.customerName());

        List<Map<String, Object>> lines = new ArrayList<>();
        for (QuoteBreakdown.QuoteLine line : quote.lines()) {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("itemId", line.itemId());
            row.put("itemName", line.itemName());
            row.put("category", line.category());
            row.put("quantity", line.quantity());
            row.put("unitPrice", line.unitPrice());
            row.put("lineSubtotal", line.lineSubtotal());
            lines.add(row);
        }

        payload.put("lines", lines);
        payload.put("itemSubtotal", quote.itemSubtotal());
        payload.put("categoryDiscount", quote.categoryDiscount());
        payload.put("loyaltyDiscount", quote.loyaltyDiscount());
        payload.put("shippingCost", quote.shippingCost());
        payload.put("total", quote.total());

        return payload;
    }

    private static Map<String, Object> recordToMap(SaleRecord record) {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("saleId", record.saleId());
        payload.put("timestampUtc", record.timestampUtc());

        Map<String, Object> customer = new LinkedHashMap<>();
        customer.put("id", record.customer().id());
        customer.put("name", record.customer().name());
        customer.put("district", record.customer().district());
        customer.put("loyaltyYears", record.customer().loyaltyYears());
        payload.put("customer", customer);

        List<Map<String, Object>> lines = new ArrayList<>();
        for (CartLine line : record.lines()) {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("itemId", line.itemId());
            row.put("quantity", line.quantity());
            lines.add(row);
        }

        payload.put("lines", lines);
        payload.put("quote", quoteToMap(record.quote()));
        return payload;
    }

    private static String nowUtc() {
        return OffsetDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    }

    private static double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
