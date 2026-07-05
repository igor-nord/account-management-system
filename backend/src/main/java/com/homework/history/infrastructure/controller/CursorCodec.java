package com.homework.history.infrastructure.controller;

import com.homework.history.domain.HistoryCursor;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;

final class CursorCodec {

    private CursorCodec() {
    }

    static String encode(HistoryCursor cursor) {
        String raw = cursor.createdAt().toString() + "|" + cursor.transactionId();
        return Base64.getUrlEncoder().withoutPadding().encodeToString(raw.getBytes(StandardCharsets.UTF_8));
    }

    static HistoryCursor decode(String cursor) {
        String raw = new String(Base64.getUrlDecoder().decode(cursor), StandardCharsets.UTF_8);
        int sep = raw.lastIndexOf('|');
        return new HistoryCursor(Instant.parse(raw.substring(0, sep)), raw.substring(sep + 1));
    }
}
