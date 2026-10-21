
package com.uniamerica.sorveteria.service;

import com.uniamerica.sorveteria.controller.produto.dto.ProdutoRequest;
import com.uniamerica.sorveteria.entity.Categoria;
import com.uniamerica.sorveteria.entity.Produto;
import com.uniamerica.sorveteria.repository.ProdutoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProdutoServiceTest {

  @Mock
  private ProdutoRepository produtoRepository;

  @InjectMocks
  private ProdutoService produtoService;

  private Categoria categoria() {
    return Categoria.values()[0];
  }

  private ProdutoRequest requisicao(
    String nome, Categoria categoria, Double preco,
    Integer estoque, Boolean disponivel) {
    return new ProdutoRequest(nome, categoria, preco, estoque, disponivel);
  }

  private Produto produtoExistente(Long id) {
    Produto produto = new Produto();
    produto.setId(id);
    produto.setNome("Sorvete de chocolate");
    produto.setCategoria(categoria());
    produto.setPreco(10.0);
    produto.setEstoque(20);
    produto.setDisponivel(true);
    return produto;
  }

  @Test
  void deveSalvarProdutoDisponivelPorPadrao() {
    ProdutoRequest request = requisicao(
      "Sorvete", categoria(), 10.0, 20, null);

    when(produtoRepository.save(any(Produto.class)))
      .thenAnswer(invocation -> invocation.getArgument(0));

    Produto resultado = produtoService.salvar(request);

    assertEquals("Sorvete", resultado.getNome());
    assertEquals(10.0, resultado.getPreco());
    assertEquals(20, resultado.getEstoque());
    assertTrue(resultado.getDisponivel());

    verify(produtoRepository).save(any(Produto.class));
  }

  @Test
  void deveSalvarProdutoIndisponivelQuandoInformado() {
    ProdutoRequest request = requisicao(
      "Sorvete", categoria(), 10.0, 20, false);

    when(produtoRepository.save(any(Produto.class)))
      .thenAnswer(invocation -> invocation.getArgument(0));

    Produto resultado = produtoService.salvar(request);

    assertFalse(resultado.getDisponivel());
  }

  @Test
  void deveBuscarProdutoPorId() {
    Produto produto = produtoExistente(1L);
    when(produtoRepository.findById(1L))
      .thenReturn(Optional.of(produto));

    Produto resultado = produtoService.buscarPorId(1L);

    assertSame(produto, resultado);
    verify(produtoRepository).findById(1L);
  }

  @Test
  void deveLancarExcecaoQuandoProdutoNaoExiste() {
    when(produtoRepository.findById(99L))
      .thenReturn(Optional.empty());

    ResponseStatusException excecao = assertThrows(
      ResponseStatusException.class,
      () -> produtoService.buscarPorId(99L));

    assertEquals(HttpStatus.NOT_FOUND, excecao.getStatusCode());
    assertTrue(excecao.getReason().contains("99"));
  }

  @Test
  void deveListarTodosOsProdutos() {
    List<Produto> produtos = List.of(
      produtoExistente(1L), produtoExistente(2L));

    when(produtoRepository.findAll()).thenReturn(produtos);

    List<Produto> resultado = produtoService.listar();

    assertEquals(2, resultado.size());
    assertSame(produtos, resultado);
    verify(produtoRepository).findAll();
  }

  @Test
  void deveBuscarPorCategoriaQuandoCategoriaInformada() {
    Categoria categoria = categoria();
    List<Produto> produtos = List.of(produtoExistente(1L));

    when(produtoRepository.findByCategoria(categoria))
      .thenReturn(produtos);

    List<Produto> resultado =
      produtoService.buscar(categoria, "chocolate");

    assertEquals(1, resultado.size());
    verify(produtoRepository).findByCategoria(categoria);
    verify(produtoRepository, never())
      .findByNomeContainingIgnoreCase(anyString());
  }

  @Test
  void deveBuscarPorNomeQuandoNomeInformado() {
    List<Produto> produtos = List.of(produtoExistente(1L));

    when(produtoRepository.findByNomeContainingIgnoreCase("choco"))
      .thenReturn(produtos);

    List<Produto> resultado =
      produtoService.buscar(null, "choco");

    assertEquals(1, resultado.size());
    verify(produtoRepository)
      .findByNomeContainingIgnoreCase("choco");
  }

  @Test
  void deveListarTodosQuandoNaoExistemFiltros() {
    List<Produto> produtos = List.of(produtoExistente(1L));
    when(produtoRepository.findAll()).thenReturn(produtos);

    List<Produto> resultado = produtoService.buscar(null, "  ");

    assertEquals(1, resultado.size());
    verify(produtoRepository).findAll();
  }

  @Test
  void deveListarProdutosDisponiveis() {
    List<Produto> produtos = List.of(produtoExistente(1L));
    when(produtoRepository.findByDisponivelTrue()).thenReturn(produtos);

    List<Produto> resultado =
      produtoService.listarDisponiveisParaVenda();

    assertEquals(1, resultado.size());
    verify(produtoRepository).findByDisponivelTrue();
  }

  @Test
  void deveAtualizarProdutoExistente() {
    Produto produto = produtoExistente(1L);
    ProdutoRequest request = requisicao(
      "Sorvete de morango", categoria(), 12.0, 15, false);

    when(produtoRepository.findById(1L))
      .thenReturn(Optional.of(produto));
    when(produtoRepository.save(any(Produto.class)))
      .thenAnswer(invocation -> invocation.getArgument(0));

    Produto resultado = produtoService.atualizar(1L, request);

    assertEquals("Sorvete de morango", resultado.getNome());
    assertEquals(12.0, resultado.getPreco());
    assertEquals(15, resultado.getEstoque());
    assertFalse(resultado.getDisponivel());

    verify(produtoRepository).save(produto);
  }

  @Test
  void deveAtualizarParcialmenteSomenteCamposInformados() {
    Produto produto = produtoExistente(1L);
    ProdutoRequest request = requisicao(
      "Novo nome", null, null, null, null);

    when(produtoRepository.findById(1L))
      .thenReturn(Optional.of(produto));
    when(produtoRepository.save(any(Produto.class)))
      .thenAnswer(invocation -> invocation.getArgument(0));

    Produto resultado =
      produtoService.atualizarParcial(1L, request);

    assertEquals("Novo nome", resultado.getNome());
    assertEquals(10.0, resultado.getPreco());
    assertEquals(20, resultado.getEstoque());
    assertTrue(resultado.getDisponivel());
    assertEquals(categoria(), resultado.getCategoria());

    verify(produtoRepository).save(produto);
  }

  @Test
  void deveDeletarProdutoExistente() {
    Produto produto = produtoExistente(1L);
    when(produtoRepository.findById(1L))
      .thenReturn(Optional.of(produto));

    produtoService.deletarPorId(1L);

    verify(produtoRepository).delete(produto);
  }

  @Test
  void naoDeveDeletarProdutoInexistente() {
    when(produtoRepository.findById(99L))
      .thenReturn(Optional.empty());

    assertThrows(
      ResponseStatusException.class,
      () -> produtoService.deletarPorId(99L));

    verify(produtoRepository, never()).delete(any(Produto.class));
  }
}
