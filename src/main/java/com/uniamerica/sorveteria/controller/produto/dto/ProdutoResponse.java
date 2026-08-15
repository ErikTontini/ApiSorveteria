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
    public static ProdutoResponse de(Produto produto) {
        return new ProdutoResponse(
                produto.getId(),
                produto.getNome(),
                produto.getCategoria(),
                produto.getPreco(),
                produto.getEstoque(),
                produto.getDisponivel()
        );
    }
}
