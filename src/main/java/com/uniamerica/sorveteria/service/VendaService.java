package com.uniamerica.sorveteria.service;

import com.uniamerica.sorveteria.controller.venda.dto.ItemVendaRequest;
import com.uniamerica.sorveteria.controller.venda.dto.VendaRequest;
import com.uniamerica.sorveteria.entity.ItemVenda;
import com.uniamerica.sorveteria.entity.Produto;
import com.uniamerica.sorveteria.entity.StatusVenda;
import com.uniamerica.sorveteria.entity.Venda;
import com.uniamerica.sorveteria.repository.ProdutoRepository;
import com.uniamerica.sorveteria.repository.VendaRepository;

import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class VendaService {

    private final VendaRepository vendaRepository;
    private final ProdutoRepository produtoRepository;

    // ==========================================
    // REGISTRAR VENDA
    // ==========================================

    @Transactional
    public Venda registrar(VendaRequest vendaRequest) {

      log.info("Iniciando registro de venda");

        Venda venda = new Venda();
        venda.setDataHora(LocalDateTime.now());
        venda.setStatus(StatusVenda.CONCLUIDA);

        double valorTotal = 0.0;

        for (ItemVendaRequest itemRequest : vendaRequest.itens()) {
            Produto produto = produtoRepository.findById(itemRequest.produtoId()).orElseThrow(() ->
                            new ResponseStatusException(HttpStatus.NOT_FOUND, "Produto nao encontrado com id " + itemRequest.produtoId())
                    );

            if (produto.getDisponivel() == Boolean.FALSE) {
              log.warn("Produto Indisponivel para venda");
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Produto " + produto.getNome() + " nao está disponível para venda");
            }

            Integer quantidade = itemRequest.quantidade();

            if (produto.getEstoque() == null || produto.getEstoque() < quantidade) {
              log.warn("Estoque insuficiente");
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Estoque insuficiente para o produto " + produto.getNome());
            }

            double subtotal = produto.getPreco() * quantidade;

            ItemVenda item = new ItemVenda();

            item.setVenda(venda);
            item.setProduto(produto);
            item.setQuantidade(quantidade);
            item.setValorUnitario(produto.getPreco());
            item.setSubtotal(subtotal);
            venda.getItens().add(item);

            produto.setEstoque(produto.getEstoque() - quantidade);
            produtoRepository.save(produto);
            valorTotal += subtotal;
        }
        venda.setValorTotal(valorTotal);
       log.info("Venda registrada com sucesso.");
        return vendaRepository.save(venda);

    }

    public Venda buscarPorId(Long id) {
        return vendaRepository.findById(id).orElseThrow(() ->
        new ResponseStatusException(HttpStatus.NOT_FOUND, "Venda nao encontrada com id " + id));
    }

    public List<Venda> listar() {
        return vendaRepository.findAll();
    }

    public List<Venda> buscar(Long produtoId, StatusVenda status) {

        if (produtoId != null) {
            Produto produto = produtoRepository.findById(produtoId).orElseThrow(() ->
              new ResponseStatusException(HttpStatus.NOT_FOUND, "Produto nao encontrado com id " + produtoId));

            List<Venda> vendas = vendaRepository.findDistinctByItens_Produto(produto);

            if (status != null) {
                return vendas.stream().filter(venda -> venda.getStatus() == status).toList();
            }
            return vendas;
        }

        if (status != null) {
            return vendaRepository.findByStatus(status);
        }

        return listar();
    }

    @Transactional
    public Venda cancelar(Long id) {
       log.info("Solicitado cancelamento da venda ");
        Venda venda = buscarPorId(id);

        if (venda.getStatus() == StatusVenda.CANCELADA) {
           log.warn("Tentativa de cancelar venda que já está cancelada");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Venda ja esta cancelada");
        }

        for (ItemVenda item : venda.getItens()) {
            Produto produto = item.getProduto();
            produto.setEstoque( produto.getEstoque() + item.getQuantidade());
            produtoRepository.save(produto);
        }

        venda.setStatus(StatusVenda.CANCELADA);
       log.info("Venda cancelada com sucesso");
        return vendaRepository.save(venda);
    }

    @Transactional
    public void deletarPorId(Long id) {

        Venda venda = buscarPorId(id);
        if (venda.getStatus() != StatusVenda.CANCELADA) {

            for (ItemVenda item : venda.getItens()) {
                Produto produto = item.getProduto();
                produto.setEstoque( produto.getEstoque() + item.getQuantidade());
                produtoRepository.save(produto);
            }
        }

        vendaRepository.delete(venda);
    }
}
