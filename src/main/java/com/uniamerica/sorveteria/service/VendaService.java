package com.uniamerica.sorveteria.service;

import com.uniamerica.sorveteria.controller.produto.venda.dto.VendaRequest;
import com.uniamerica.sorveteria.entity.Produto;
import com.uniamerica.sorveteria.entity.StatusVenda;
import com.uniamerica.sorveteria.entity.Venda;
import com.uniamerica.sorveteria.repository.ProdutoRepository;
import com.uniamerica.sorveteria.repository.VendaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VendaService {

    private final VendaRepository vendaRepository;
    private final ProdutoRepository produtoRepository;

    public Venda registrar(VendaRequest vendaRequest) {
        Produto produto = this.produtoRepository.findById(vendaRequest.produtoId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Produto não encontrado com id " + vendaRequest.produtoId()));

        if (Boolean.FALSE.equals(produto.getDisponivel())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Produto não está disponível para venda");
        }

        Integer quantidade = vendaRequest.quantidade();
        if (quantidade == null || quantidade <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Quantidade deve ser maior que zero");
        }

        if (produto.getEstoque() == null || produto.getEstoque() < quantidade) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Estoque insuficiente para o produto " + produto.getNome());
        }

        produto.setEstoque(produto.getEstoque() - quantidade);
        this.produtoRepository.save(produto);

        Venda venda = new Venda();
        venda.setProduto(produto);
        venda.setQuantidade(quantidade);
        venda.setValorUnitario(produto.getPreco());
        venda.setValorTotal(produto.getPreco() * quantidade);
        venda.setDataHora(LocalDateTime.now());
        venda.setStatus(StatusVenda.CONCLUIDA);

        return this.vendaRepository.save(venda);
    }

    public Venda buscarPorId(Long id) {
        return this.vendaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Venda não encontrada com id " + id));
    }

    public List<Venda> listar() {
        return this.vendaRepository.findAll();
    }

    public List<Venda> buscar(Long produtoId, StatusVenda status) {
        if (produtoId != null) {
            Produto produto = this.produtoRepository.findById(produtoId)
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.NOT_FOUND,
                            "Produto não encontrado com id " + produtoId));
            return this.vendaRepository.findByProduto(produto);
        }
        if (status != null) {
            return this.vendaRepository.findByStatus(status);
        }
        return this.listar();
    }

    public Venda cancelar(Long id) {
        Venda venda = this.buscarPorId(id);

        if (venda.getStatus() == StatusVenda.CANCELADA) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Venda já está cancelada");
        }

        Produto produto = venda.getProduto();
        produto.setEstoque(produto.getEstoque() + venda.getQuantidade());
        this.produtoRepository.save(produto);

        venda.setStatus(StatusVenda.CANCELADA);
        return this.vendaRepository.save(venda);
    }

    public void deletarPorId(Long id) {
        Venda venda = this.buscarPorId(id);
        this.vendaRepository.delete(venda);
    }
}
