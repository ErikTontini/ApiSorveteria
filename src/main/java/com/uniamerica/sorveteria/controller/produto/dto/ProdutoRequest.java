package com.uniamerica.sorveteria.controller.produto.dto;

import com.uniamerica.sorveteria.entity.Categoria;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProdutoRequest {
    private String nome;
    private Categoria categoria;
    private Double preco;
    private Integer estoque;
    private Boolean disponivel;
}
