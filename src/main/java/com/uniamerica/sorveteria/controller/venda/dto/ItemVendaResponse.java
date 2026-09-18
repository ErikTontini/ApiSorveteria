package com.uniamerica.sorveteria.controller.venda.dto;

import com.uniamerica.sorveteria.entity.ItemVenda;

public record ItemVendaResponse(

        Long produtoId,
        String produtoNome,
        Integer quantidade,
        Double valorUnitario,
        Double subtotal

) {

    public static ItemVendaResponse fromEntity(ItemVenda item) {

        return new ItemVendaResponse(
                item.getProduto().getId(),
                item.getProduto().getNome(),
                item.getQuantidade(),
                item.getValorUnitario(),
                item.getSubtotal()
        );
    }
}