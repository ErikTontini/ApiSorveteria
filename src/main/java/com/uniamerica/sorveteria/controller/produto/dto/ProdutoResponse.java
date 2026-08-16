package com.uniamerica.sorveteria.controller.produto.dto;

import com.uniamerica.sorveteria.entity.Categoria;
import com.uniamerica.sorveteria.entity.Produto;

public record ProdutoResponse(
        Long id,
        String nome,
        Categoria categoria,
        Double preco,
        Integer estoque,
        Boolean disponivel
) {
    public static ProdutoResponse fromEntity(Produto p) {
        return new ProdutoResponse(
                p.getId(),
                p.getNome(),
                p.getCategoria(),
                p.getPreco(),
                p.getEstoque(),
                p.getDisponivel()
        );
    }
}