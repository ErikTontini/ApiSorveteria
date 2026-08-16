package com.uniamerica.sorveteria.repository;

import com.uniamerica.sorveteria.entity.Categoria;
import com.uniamerica.sorveteria.entity.Produto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProdutoRepository extends JpaRepository<Produto, Long> {

    List<Produto> findByCategoria(Categoria categoria);

    List<Produto> findByNomeContainingIgnoreCase(String nome);

    List<Produto> findByDisponivelTrue();
}
