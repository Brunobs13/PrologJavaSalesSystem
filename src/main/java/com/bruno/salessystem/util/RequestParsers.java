package com.bruno.salessystem.util;

import com.bruno.salessystem.core.CartLine;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class RequestParsers {
    private static final Pattern CUSTOMER_PATTERN = Pattern.compile("\"customerId\"\\s*:\\s*(\\d+)");
    private static final Pattern ITEM_PATTERN = Pattern.compile("\\{\\s*\"itemId\"\\s*:\\s*(\\d+)\\s*,\\s*\"quantity\"\\s*:\\s*(\\d+)\\s*\\}");

    private RequestParsers() {
    }

    public static int parseCustomerId(String jsonBody) {
        Matcher matcher = CUSTOMER_PATTERN.matcher(jsonBody);
        if (!matcher.find()) {
            throw new IllegalArgumentException("Invalid payload: customerId is required.");
        }
        return Integer.parseInt(matcher.group(1));
    }

    public static List<CartLine> parseItems(String jsonBody) {
        List<CartLine> lines = new ArrayList<>();
        Matcher matcher = ITEM_PATTERN.matcher(jsonBody);
        while (matcher.find()) {
            int itemId = Integer.parseInt(matcher.group(1));
            int quantity = Integer.parseInt(matcher.group(2));
            lines.add(new CartLine(itemId, quantity));
        }

        if (lines.isEmpty()) {
            throw new IllegalArgumentException("Invalid payload: at least one item line is required.");
        }

        return lines;
    }
}
