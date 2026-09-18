package com.uniamerica.sorveteria.controller.venda.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record ItemVendaRequest(

        @NotNull(message = "O produto e obrigatorio")
        @Positive(message = "O ID do produto deve ser maior que zero")
        Long produtoId,

        @NotNull(message = "A quantidade e obrigatoria")
        @Positive(message = "A quantidade deve ser maior que zero")
        Integer quantidade

) {
}