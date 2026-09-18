package com.uniamerica.sorveteria.service;

import com.uniamerica.sorveteria.controller.produto.dto.ProdutoRequest;
import com.uniamerica.sorveteria.entity.Categoria;
import com.uniamerica.sorveteria.entity.Produto;
import com.uniamerica.sorveteria.repository.ProdutoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
@Slf4j
@Service
@RequiredArgsConstructor
public class ProdutoService {

    private final ProdutoRepository produtoRepository;

    public Produto salvar(ProdutoRequest produtoRequest) {
      log.info("Iniciando cadastro de produto.");
        Produto produto = new Produto();
        produto.setNome(produtoRequest.nome());
        produto.setCategoria(produtoRequest.categoria());
        produto.setPreco(produtoRequest.preco());
        produto.setEstoque(produtoRequest.estoque());
        produto.setDisponivel(produtoRequest.disponivel() != null ? produtoRequest.disponivel() : true);
      log.info("Sucesso ao cadastrar produto.");
        return this.produtoRepository.save(produto);

    }

    public Produto buscarPorId(Long id) {
        return this.produtoRepository.findById(id).orElseThrow(() ->
                  new ResponseStatusException(HttpStatus.NOT_FOUND, "Produto não encontrado com id " + id));
    }

    public List<Produto> listar() {
        return this.produtoRepository.findAll();
    }

    public List<Produto> buscar(Categoria categoria, String nome) {
        if (categoria != null) {
            return this.produtoRepository.findByCategoria(categoria);
        }
        if (nome != null && !nome.isBlank()) {
            return this.produtoRepository.findByNomeContainingIgnoreCase(nome);
        }
        return this.listar();
    }

    public List<Produto> listarDisponiveisParaVenda() {
        return this.produtoRepository.findByDisponivelTrue();
    }

    public Produto atualizar(Long id, ProdutoRequest produtoRequest) {
        Produto produto = this.buscarPorId(id);
        produto.setNome(produtoRequest.nome());
        produto.setCategoria(produtoRequest.categoria());
        produto.setPreco(produtoRequest.preco());
        produto.setEstoque(produtoRequest.estoque());
        produto.setDisponivel(produtoRequest.disponivel());

        return this.produtoRepository.save(produto);
    }

    public Produto atualizarParcial(Long id, ProdutoRequest produtoRequest) {
        Produto produto = this.buscarPorId(id);

        if (produtoRequest.nome() != null) produto.setNome(produtoRequest.nome());
        if (produtoRequest.categoria() != null) produto.setCategoria(produtoRequest.categoria());
        if (produtoRequest.preco() != null) produto.setPreco(produtoRequest.preco());
        if (produtoRequest.estoque() != null) produto.setEstoque(produtoRequest.estoque());
        if (produtoRequest.disponivel() != null) produto.setDisponivel(produtoRequest.disponivel());

        return this.produtoRepository.save(produto);
    }

    public void deletarPorId(Long id) {
        Produto produto = this.buscarPorId(id);
        this.produtoRepository.delete(produto);
    }
}
