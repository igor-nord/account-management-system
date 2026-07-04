package com.homework.transaction.infrastructure.id;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class TsidTransactionIdGeneratorTest {

    private final TsidTransactionIdGenerator generator = new TsidTransactionIdGenerator();

    @Test
    void generatesNonNullThirteenCharIds() {
        String id = generator.newTransactionId();
        assertNotNull(id);
        assertEquals(13, id.length());
    }

    @Test
    void generatesDistinctIds() {
        assertNotEquals(generator.newTransactionId(), generator.newTransactionId());
    }
}
