package com.uniamerica.sorveteria.controller.produto.dto;

import com.uniamerica.sorveteria.entity.Categoria;

public record ProdutoRequest(
        String nome,
        Categoria categoria,
        Double preco,
        Integer estoque,
        Boolean disponivel
) {}