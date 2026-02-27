package com.bruno.salessystem.api;

import com.bruno.salessystem.core.CartLine;
import com.bruno.salessystem.core.SalesService;
import com.bruno.salessystem.prolog.PrologDataLoader;
import com.bruno.salessystem.prolog.PrologKnowledgeBase;
import com.bruno.salessystem.util.AppLogger;
import com.bruno.salessystem.util.JsonUtil;
import com.bruno.salessystem.util.RequestParsers;
import com.bruno.salessystem.util.StaticAssets;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.time.OffsetDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class ApiServer {
    private static final AppLogger LOGGER = AppLogger.getLogger("api-server");

    private ApiServer() {
    }

    public static void main(String[] args) throws Exception {
        String prologPath = env("PROLOG_DATA_FILE", "src/main/resources/prolog/store.pl");
        int port = parsePort(env("PORT", "8080"));

        LOGGER.info("loading knowledge base from " + prologPath);
        PrologKnowledgeBase baseData = PrologDataLoader.load(Path.of(prologPath));
        SalesService service = new SalesService(baseData);

        HttpServer server = HttpServer.create(new InetSocketAddress("0.0.0.0", port), 0);
        Path webRoot = Path.of("web");

        server.createContext("/health", exchange -> {
            if (!ensureMethod(exchange, "GET")) {
                return;
            }
            respondJson(exchange, 200, service.health());
        });

        server.createContext("/api/customers", exchange -> {
            if (!ensureMethod(exchange, "GET")) {
                return;
            }
            respondJson(exchange, 200, mapOf("customers", service.listCustomers()));
        });

        server.createContext("/api/items", exchange -> {
            if (!ensureMethod(exchange, "GET")) {
                return;
            }
            respondJson(exchange, 200, mapOf("items", service.listProducts()));
        });

        server.createContext("/api/products", exchange -> {
            if (!ensureMethod(exchange, "GET")) {
                return;
            }
            respondJson(exchange, 200, mapOf("items", service.listProducts()));
        });

        server.createContext("/api/sales", exchange -> {
            if (!ensureMethod(exchange, "GET")) {
                return;
            }
            respondJson(exchange, 200, mapOf("sales", service.listSales()));
        });

        server.createContext("/api/metrics", exchange -> {
            if (!ensureMethod(exchange, "GET")) {
                return;
            }
            respondJson(exchange, 200, service.metrics());
        });

        server.createContext("/api/dashboard", exchange -> {
            if (!ensureMethod(exchange, "GET")) {
                return;
            }
            respondJson(exchange, 200, service.dashboard());
        });

        server.createContext("/api/sales/quote", exchange -> {
            if (!ensureMethod(exchange, "POST")) {
                return;
            }

            try {
                String body = readBody(exchange.getRequestBody());
                int customerId = RequestParsers.parseCustomerId(body);
                List<CartLine> items = RequestParsers.parseItems(body);
                respondJson(exchange, 200, service.quote(customerId, items));
            } catch (IllegalArgumentException exception) {
                respondJson(exchange, 400, errorPayload(exception.getMessage()));
            }
        });

        server.createContext("/api/sales/register", exchange -> {
            if (!ensureMethod(exchange, "POST")) {
                return;
            }

            try {
                String body = readBody(exchange.getRequestBody());
                int customerId = RequestParsers.parseCustomerId(body);
                List<CartLine> items = RequestParsers.parseItems(body);
                respondJson(exchange, 200, service.registerSale(customerId, items));
            } catch (IllegalArgumentException exception) {
                respondJson(exchange, 400, errorPayload(exception.getMessage()));
            }
        });

        server.createContext("/api/reset", exchange -> {
            if (!ensureMethod(exchange, "POST")) {
                return;
            }
            respondJson(exchange, 200, service.resetSession());
        });

        server.createContext("/", new StaticHandler(webRoot));
        server.createContext("/assets", new StaticHandler(webRoot));

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            LOGGER.info("shutdown signal received");
            server.stop(1);
        }));

        server.start();
        LOGGER.info(String.format("started on http://localhost:%d at %s", port, OffsetDateTime.now()));
    }

    private static int parsePort(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException exception) {
            LOGGER.error("invalid PORT value: " + value + ". Falling back to 8080");
            return 8080;
        }
    }

    private static boolean ensureMethod(HttpExchange exchange, String expectedMethod) throws IOException {
        if (exchange.getRequestMethod().equalsIgnoreCase(expectedMethod)) {
            return true;
        }

        respondJson(exchange, 405, errorPayload("Method not allowed."));
        return false;
    }

    private static void respondJson(HttpExchange exchange, int statusCode, Object payload) throws IOException {
        String response = JsonUtil.toJson(payload);
        byte[] bytes = response.getBytes(StandardCharsets.UTF_8);

        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8");
        exchange.sendResponseHeaders(statusCode, bytes.length);

        try (OutputStream outputStream = exchange.getResponseBody()) {
            outputStream.write(bytes);
        }
    }

    private static String readBody(InputStream inputStream) throws IOException {
        return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
    }

    private static Map<String, Object> mapOf(String key, Object value) {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put(key, value);
        return payload;
    }

    private static Map<String, Object> errorPayload(String message) {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("error", message);
        return payload;
    }

    private static String env(String key, String fallback) {
        String value = System.getenv(key);
        if (value == null || value.isBlank()) {
            return fallback;
        }
        return value;
    }

    private static final class StaticHandler implements HttpHandler {
        private final Path webRoot;

        private StaticHandler(Path webRoot) {
            this.webRoot = webRoot;
        }

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String method = exchange.getRequestMethod();
            if (!"GET".equalsIgnoreCase(method) && !"HEAD".equalsIgnoreCase(method)) {
                respondJson(exchange, 405, errorPayload("Method not allowed."));
                return;
            }

            String path = exchange.getRequestURI().getPath();

            try {
                byte[] bytes = StaticAssets.readAsset(webRoot, path);
                exchange.getResponseHeaders().set("Content-Type", StaticAssets.contentType(path));
                if ("HEAD".equalsIgnoreCase(method)) {
                    exchange.sendResponseHeaders(200, -1);
                    exchange.close();
                    return;
                }

                exchange.sendResponseHeaders(200, bytes.length);
                try (OutputStream outputStream = exchange.getResponseBody()) {
                    outputStream.write(bytes);
                }
            } catch (IOException notFound) {
                byte[] bytes = "Not Found".getBytes(StandardCharsets.UTF_8);
                if ("HEAD".equalsIgnoreCase(method)) {
                    exchange.sendResponseHeaders(404, -1);
                    exchange.close();
                    return;
                }

                exchange.sendResponseHeaders(404, bytes.length);
                try (OutputStream outputStream = exchange.getResponseBody()) {
                    outputStream.write(bytes);
                }
            }
        }
    }
}
