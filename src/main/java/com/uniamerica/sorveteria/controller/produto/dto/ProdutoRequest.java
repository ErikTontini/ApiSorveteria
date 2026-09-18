package com.uniamerica.sorveteria.controller.produto.dto;

import com.uniamerica.sorveteria.entity.Categoria;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record ProdutoRequest(

  @NotBlank(message = "O nome é obrigatório")
  String nome,

  @NotNull(message = "A categoria é obrigatória")
  Categoria categoria,

  @NotNull(message = "O preço é obrigatório")
  @Positive(message = "O preço deve ser maior que zero")
  Double preco,

  @NotNull(message = "O estoque e obrigatorio")
  @PositiveOrZero(message = "O estoque nao pode ser negativo")
  Integer estoque,

  @NotNull(message = "A disponibilidade e obrigatoria")
  Boolean disponivel
) {
}
