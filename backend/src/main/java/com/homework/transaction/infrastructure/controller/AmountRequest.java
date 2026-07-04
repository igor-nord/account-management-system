package com.homework.transaction.infrastructure.controller;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record AmountRequest(
        @NotNull @DecimalMin("0.01") @JsonFormat(shape = JsonFormat.Shape.STRING) BigDecimal amount,
        @Size(max = 255) String description) {
}
