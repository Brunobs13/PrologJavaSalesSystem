package com.bruno.salessystem.core;

import com.bruno.salessystem.prolog.PrologDataLoader;
import com.bruno.salessystem.prolog.PrologKnowledgeBase;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public final class SalesServiceTest {
    private SalesServiceTest() {
    }

    public static void main(String[] args) throws Exception {
        Path source = Path.of("src/main/resources/prolog/store.pl");
        PrologKnowledgeBase baseData = PrologDataLoader.load(source);
        SalesService service = new SalesService(baseData);

        testQuoteCalculation(service);
        testSaleRegistrationUpdatesStock(service);
        testInvalidProductThrows(service);

        System.out.println("SalesServiceTest: all checks passed");
    }

    private static void testQuoteCalculation(SalesService service) {
        Map<String, Object> response = service.quote(1, List.of(new CartLine(101, 1)));
        @SuppressWarnings("unchecked")
        Map<String, Object> quote = (Map<String, Object>) response.get("quote");

        assertNumber(1499.90, quote.get("itemSubtotal"), 0.01, "itemSubtotal");
        assertNumber(179.99, quote.get("categoryDiscount"), 0.01, "categoryDiscount");
        assertNumber(52.80, quote.get("loyaltyDiscount"), 0.01, "loyaltyDiscount");
        assertNumber(1272.01, quote.get("total"), 0.01, "total");
    }

    private static void testSaleRegistrationUpdatesStock(SalesService service) {
        service.registerSale(1, List.of(new CartLine(101, 1)));

        List<Map<String, Object>> products = service.listProducts();
        Map<String, Object> laptop = products.stream()
            .filter(p -> ((Integer) p.get("id")) == 101)
            .findFirst()
            .orElseThrow(() -> new AssertionError("Product 101 not found"));

        Object stock = laptop.get("stock");
        if (!(stock instanceof Integer) || ((Integer) stock) != 11) {
            throw new AssertionError("Expected stock 11 for product 101, got " + stock);
        }
    }

    private static void testInvalidProductThrows(SalesService service) {
        try {
            service.quote(1, List.of(new CartLine(999, 1)));
            throw new AssertionError("Expected exception for invalid product");
        } catch (IllegalArgumentException expected) {
            if (!expected.getMessage().contains("Product not found")) {
                throw new AssertionError("Unexpected error message: " + expected.getMessage());
            }
        }
    }

    private static void assertNumber(double expected, Object actual, double tolerance, String label) {
        if (!(actual instanceof Number number)) {
            throw new AssertionError(label + " should be numeric, got: " + actual);
        }

        double delta = Math.abs(expected - number.doubleValue());
        if (delta > tolerance) {
            throw new AssertionError(label + " mismatch. expected=" + expected + " actual=" + number);
        }
    }
}
