package com.uniamerica.sorveteria.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/**
 * Item do cardapio vendido no caixa da sorveteria (ex.: sorvete, casquinha, acai, milk-shake).
 * Nao existe cadastro/visao para o cliente: quem cria, edita e consulta e apenas
 * o funcionario ou o gerente operando o caixa.
 */
@Getter
@Setter
@Entity
@Table(name = "produtos")
public class Produto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;

    @Enumerated(EnumType.STRING)
    private Categoria categoria;

    private Double preco;

    private Integer estoque;

    // indica se o item pode ser vendido no caixa no momento (ex.: sabor em falta)
    private Boolean disponivel;
}
