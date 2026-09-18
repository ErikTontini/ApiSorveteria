package com.uniamerica.sorveteria.controller.venda.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record VendaRequest(

        @NotEmpty(message = "A venda deve possuir pelo menos um item")
        @Valid
        List<ItemVendaRequest> itens

) {
}