package com.uniamerica.sorveteria.controller.produto.venda.dto;

public record VendaRequest(
        Long produtoId,
        Integer quantidade
) {}