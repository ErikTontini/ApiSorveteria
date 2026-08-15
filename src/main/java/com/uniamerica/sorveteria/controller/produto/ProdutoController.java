package com.uniamerica.sorveteria.controller.produto;

import com.uniamerica.sorveteria.controller.produto.dto.ProdutoRequest;
import com.uniamerica.sorveteria.controller.produto.dto.ProdutoResponse;
import com.uniamerica.sorveteria.entity.Categoria;
import com.uniamerica.sorveteria.entity.Produto;
import com.uniamerica.sorveteria.service.ProdutoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Endpoints do cardapio usados no caixa da sorveteria.
 * Sistema de uso interno: apenas funcionario e gerente acessam (sem tela para cliente).
 */
@RestController
@RequestMapping("/api/produtos")
@RequiredArgsConstructor
public class ProdutoController {

    private final ProdutoService produtoService;

    // POST localhost:8080/api/produtos
    @PostMapping
    public ResponseEntity<ProdutoResponse> salvar(@RequestBody ProdutoRequest produtoRequest) {
        Produto produto = this.produtoService.salvar(produtoRequest);
        return new ResponseEntity<>(ProdutoResponse.de(produto), HttpStatus.CREATED);
    }

    // GET localhost:8080/api/produtos
    @GetMapping
    public ResponseEntity<List<ProdutoResponse>> listar() {
        List<ProdutoResponse> produtos = this.produtoService.listar()
                .stream()
                .map(ProdutoResponse::de)
                .toList();
        return new ResponseEntity<>(produtos, HttpStatus.OK);
    }

    // GET localhost:8080/api/produtos/3
    @GetMapping("/{id}")
    public ResponseEntity<ProdutoResponse> buscarPorId(@PathVariable Long id) {
        Produto produto = this.produtoService.buscarPorId(id);
        return new ResponseEntity<>(ProdutoResponse.de(produto), HttpStatus.OK);
    }

    // GET localhost:8080/api/produtos/buscar?categoria=SORVETE
    // GET localhost:8080/api/produtos/buscar?nome=morango
    @GetMapping("/buscar")
    public ResponseEntity<List<ProdutoResponse>> buscar(
            @RequestParam(required = false) Categoria categoria,
            @RequestParam(required = false) String nome
    ) {
        List<ProdutoResponse> produtos = this.produtoService.buscar(categoria, nome)
                .stream()
                .map(ProdutoResponse::de)
                .toList();
        return new ResponseEntity<>(produtos, HttpStatus.OK);
    }

    // GET localhost:8080/api/produtos/disponiveis
    // usado na tela do caixa: mostra so o que pode ser vendido agora
    @GetMapping("/disponiveis")
    public ResponseEntity<List<ProdutoResponse>> listarDisponiveis() {
        List<ProdutoResponse> produtos = this.produtoService.listarDisponiveisParaVenda()
                .stream()
                .map(ProdutoResponse::de)
                .toList();
        return new ResponseEntity<>(produtos, HttpStatus.OK);
    }

    // PUT localhost:8080/api/produtos/3
    @PutMapping("/{id}")
    public ResponseEntity<ProdutoResponse> atualizar(
            @PathVariable Long id,
            @RequestBody ProdutoRequest produtoRequest
    ) {
        Produto produto = this.produtoService.atualizar(id, produtoRequest);
        return new ResponseEntity<>(ProdutoResponse.de(produto), HttpStatus.OK);
    }

    // PATCH localhost:8080/api/produtos/3
    // usado, por exemplo, para o gerente marcar um sabor como indisponivel
    @PatchMapping("/{id}")
    public ResponseEntity<ProdutoResponse> atualizarParcial(
            @PathVariable Long id,
            @RequestBody ProdutoRequest produtoRequest
    ) {
        Produto produto = this.produtoService.atualizarParcial(id, produtoRequest);
        return new ResponseEntity<>(ProdutoResponse.de(produto), HttpStatus.OK);
    }

    // DELETE localhost:8080/api/produtos/3
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarPorId(@PathVariable Long id) {
        this.produtoService.deletarPorId(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
