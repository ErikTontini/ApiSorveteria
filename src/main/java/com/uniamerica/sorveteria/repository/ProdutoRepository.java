package com.uniamerica.sorveteria.repository;

import com.uniamerica.sorveteria.entity.Categoria;
import com.uniamerica.sorveteria.entity.Produto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProdutoRepository extends JpaRepository<Produto, Long> {

    // GET localhost:8080/api/produtos/buscar?categoria=SORVETE
    List<Produto> findByCategoria(Categoria categoria);

    // GET localhost:8080/api/produtos/buscar?nome=morango
    List<Produto> findByNomeContainingIgnoreCase(String nome);

    // usado no caixa: lista so o que pode ser vendido agora
    List<Produto> findByDisponivelTrue();
}
