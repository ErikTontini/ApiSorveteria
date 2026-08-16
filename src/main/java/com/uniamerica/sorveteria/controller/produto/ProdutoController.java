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

@RestController
@RequestMapping("/api/produtos")
@RequiredArgsConstructor
public class ProdutoController {

    private final ProdutoService produtoService;

    @PostMapping
    public ResponseEntity<ProdutoResponse> cadastrar(@RequestBody ProdutoRequest request) {
        Produto salvo = produtoService.salvar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ProdutoResponse.fromEntity(salvo));
    }

    @GetMapping
    public ResponseEntity<List<ProdutoResponse>> listarTodos() {
        List<ProdutoResponse> lista = produtoService.listar().stream()
                .map(ProdutoResponse::fromEntity)
                .toList();
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProdutoResponse> buscarPorId(@PathVariable Long id) {
        Produto produto = produtoService.buscarPorId(id);
        return ResponseEntity.ok(ProdutoResponse.fromEntity(produto));
    }

    @GetMapping("/busca")
    public ResponseEntity<List<ProdutoResponse>> buscar(
            @RequestParam(required = false) Categoria categoria,
            @RequestParam(required = false) String nome) {
        List<ProdutoResponse> lista = produtoService.buscar(categoria, nome).stream()
                .map(ProdutoResponse::fromEntity)
                .toList();
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/venda")
    public ResponseEntity<List<ProdutoResponse>> listarDisponiveis() {
        List<ProdutoResponse> lista = produtoService.listarDisponiveisParaVenda().stream()
                .map(ProdutoResponse::fromEntity)
                .toList();
        return ResponseEntity.ok(lista);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProdutoResponse> atualizar(@PathVariable Long id, @RequestBody ProdutoRequest request) {
        Produto atualizado = produtoService.atualizar(id, request);
        return ResponseEntity.ok(ProdutoResponse.fromEntity(atualizado));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ProdutoResponse> atualizarParcial(@PathVariable Long id, @RequestBody ProdutoRequest request) {
        Produto atualizado = produtoService.atualizarParcial(id, request);
        return ResponseEntity.ok(ProdutoResponse.fromEntity(atualizado));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        produtoService.deletarPorId(id);
        return ResponseEntity.noContent().build();
    }
}