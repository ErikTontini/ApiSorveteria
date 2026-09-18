package com.uniamerica.sorveteria.repository;

import com.uniamerica.sorveteria.entity.Produto;
import com.uniamerica.sorveteria.entity.StatusVenda;
import com.uniamerica.sorveteria.entity.Venda;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VendaRepository extends JpaRepository<Venda, Long> {

    List<Venda> findDistinctByItens_Produto(Produto produto);

    List<Venda> findByStatus(StatusVenda status);
}
