package com.uniamerica.sorveteria.controller.produto.venda.dto;


import com.uniamerica.sorveteria.entity.StatusVenda;
import com.uniamerica.sorveteria.entity.Venda;

import java.time.LocalDateTime;

public record VendaResponse(
        Long id,
        Long produtoId,
        String produtoNome,
        Integer quantidade,
        Double valorUnitario,
        Double valorTotal,
        LocalDateTime dataHora,
        StatusVenda status
) {
    public static VendaResponse fromEntity(Venda v) {
        return new VendaResponse(
                v.getId(),
                v.getProduto().getId(),
                v.getProduto().getNome(),
                v.getQuantidade(),
                v.getValorUnitario(),
                v.getValorTotal(),
                v.getDataHora(),
                v.getStatus()
        );
    }
}