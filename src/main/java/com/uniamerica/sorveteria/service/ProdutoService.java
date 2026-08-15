package com.uniamerica.sorveteria.service;

import com.uniamerica.sorveteria.controller.produto.dto.ProdutoRequest;
import com.uniamerica.sorveteria.entity.Categoria;
import com.uniamerica.sorveteria.entity.Produto;
import com.uniamerica.sorveteria.repository.ProdutoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProdutoService {

    private final ProdutoRepository produtoRepository;

    public Produto salvar(ProdutoRequest produtoRequest) {
        Produto produto = new Produto();
        produto.setNome(produtoRequest.getNome());
        produto.setCategoria(produtoRequest.getCategoria());
        produto.setPreco(produtoRequest.getPreco());
        produto.setEstoque(produtoRequest.getEstoque());
        // produto novo entra disponivel no caixa, a menos que informado o contrario
        produto.setDisponivel(produtoRequest.getDisponivel() != null ? produtoRequest.getDisponivel() : true);

        return this.produtoRepository.save(produto);
    }

    public Produto buscarPorId(Long id) {
        return this.produtoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Produto não encontrado com id " + id));
    }

    public List<Produto> listar() {
        return this.produtoRepository.findAll();
    }

    // usado pelo endpoint de busca com requestParam (categoria e/ou nome)
    public List<Produto> buscar(Categoria categoria, String nome) {
        if (categoria != null) {
            return this.produtoRepository.findByCategoria(categoria);
        }
        if (nome != null && !nome.isBlank()) {
            return this.produtoRepository.findByNomeContainingIgnoreCase(nome);
        }
        return this.listar();
    }

    // usado no caixa: so os itens que podem ser vendidos agora
    public List<Produto> listarDisponiveisParaVenda() {
        return this.produtoRepository.findByDisponivelTrue();
    }

    public Produto atualizar(Long id, ProdutoRequest produtoRequest) {
        Produto produto = this.buscarPorId(id);

        produto.setNome(produtoRequest.getNome());
        produto.setCategoria(produtoRequest.getCategoria());
        produto.setPreco(produtoRequest.getPreco());
        produto.setEstoque(produtoRequest.getEstoque());
        produto.setDisponivel(produtoRequest.getDisponivel());

        return this.produtoRepository.save(produto);
    }

    public Produto atualizarParcial(Long id, ProdutoRequest produtoRequest) {
        Produto produto = this.buscarPorId(id);

        if (produtoRequest.getNome() != null) produto.setNome(produtoRequest.getNome());
        if (produtoRequest.getCategoria() != null) produto.setCategoria(produtoRequest.getCategoria());
        if (produtoRequest.getPreco() != null) produto.setPreco(produtoRequest.getPreco());
        if (produtoRequest.getEstoque() != null) produto.setEstoque(produtoRequest.getEstoque());
        if (produtoRequest.getDisponivel() != null) produto.setDisponivel(produtoRequest.getDisponivel());

        return this.produtoRepository.save(produto);
    }

    public void deletarPorId(Long id) {
        Produto produto = this.buscarPorId(id);
        this.produtoRepository.delete(produto);
    }
}
