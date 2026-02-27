package com.bruno.salessystem.util;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public final class StaticAssets {
    private StaticAssets() {
    }

    public static byte[] readAsset(Path webRoot, String requestPath) throws IOException {
        String normalized = requestPath;
        if ("/".equals(normalized)) {
            normalized = "/index.html";
        }
        if (normalized.startsWith("/assets/")) {
            normalized = normalized.substring("/assets".length());
        }

        if (normalized.contains("..")) {
            throw new IOException("Invalid path traversal attempt.");
        }

        Path resolved = webRoot.resolve(normalized.substring(1));
        if (!Files.exists(resolved) || Files.isDirectory(resolved)) {
            throw new IOException("Asset not found: " + resolved);
        }

        return Files.readAllBytes(resolved);
    }

    public static String contentType(String requestPath) {
        String path = requestPath.toLowerCase();
        if (path.endsWith(".html") || "/".equals(path)) {
            return "text/html; charset=utf-8";
        }
        if (path.endsWith(".css")) {
            return "text/css; charset=utf-8";
        }
        if (path.endsWith(".js")) {
            return "application/javascript; charset=utf-8";
        }
        if (path.endsWith(".json")) {
            return "application/json; charset=utf-8";
        }
        if (path.endsWith(".svg")) {
            return "image/svg+xml";
        }
        if (path.endsWith(".png")) {
            return "image/png";
        }
        if (path.endsWith(".jpg") || path.endsWith(".jpeg")) {
            return "image/jpeg";
        }
        return "text/plain; charset=" + StandardCharsets.UTF_8;
    }
}
