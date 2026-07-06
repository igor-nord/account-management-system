package com.homework.history.controller;

import com.homework.history.exception.InvalidCursorException;
import com.homework.history.domain.HistoryCursor;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.Base64;

final class CursorCodec {

    private CursorCodec() {
    }

    static String encode(HistoryCursor cursor) {
        String raw = cursor.createdAt().toString() + "|" + cursor.transactionId();
        return Base64.getUrlEncoder().withoutPadding().encodeToString(raw.getBytes(StandardCharsets.UTF_8));
    }

    static HistoryCursor decode(String cursor) {
        try {
            String rawCursor = new String(Base64.getUrlDecoder().decode(cursor), StandardCharsets.UTF_8);
            int sep = rawCursor.lastIndexOf('|');
            if (sep < 0) {
                throw new IllegalArgumentException("missing separator");
            }
            return new HistoryCursor(LocalDateTime.parse(rawCursor.substring(0, sep)), rawCursor.substring(sep + 1));
        } catch (IllegalArgumentException | DateTimeParseException e) {
            throw new InvalidCursorException(cursor);
        }
    }
}
