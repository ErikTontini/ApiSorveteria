package com.uniamerica.sorveteria.controller.venda.dto;

import com.uniamerica.sorveteria.entity.StatusVenda;
import com.uniamerica.sorveteria.entity.Venda;

import java.time.LocalDateTime;
import java.util.List;

public record VendaResponse(

        Long id,
        List<ItemVendaResponse> itens,
        Double valorTotal,
        LocalDateTime dataHora,
        StatusVenda status

) {

    public static VendaResponse fromEntity(Venda venda) {

        List<ItemVendaResponse> itens =
                venda.getItens()
                        .stream()
                        .map(ItemVendaResponse::fromEntity)
                        .toList();

        return new VendaResponse(
                venda.getId(),
                itens,
                venda.getValorTotal(),
                venda.getDataHora(),
                venda.getStatus()
        );
    }
}