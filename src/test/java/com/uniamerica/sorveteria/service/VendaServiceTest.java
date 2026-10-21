
package com.uniamerica.sorveteria.service;

import com.uniamerica.sorveteria.controller.venda.dto.ItemVendaRequest;
import com.uniamerica.sorveteria.controller.venda.dto.VendaRequest;
import com.uniamerica.sorveteria.entity.ItemVenda;
import com.uniamerica.sorveteria.entity.Produto;
import com.uniamerica.sorveteria.entity.StatusVenda;
import com.uniamerica.sorveteria.entity.Venda;
import com.uniamerica.sorveteria.repository.ProdutoRepository;
import com.uniamerica.sorveteria.repository.VendaRepository;
import org.junit.jupiter.api.BeforeEach;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VendaServiceTest {

  @Mock
  private VendaRepository vendaRepository;

  @Mock
  private ProdutoRepository produtoRepository;

  @InjectMocks
  private VendaService vendaService;

  private Produto produto;

  @BeforeEach
  void configurar() {
    produto = new Produto();
    produto.setId(1L);
    produto.setNome("Sorvete de Chocolate");
    produto.setPreco(10.0);
    produto.setEstoque(20);
    produto.setDisponivel(true);
  }

  private VendaRequest requisicao(Long produtoId, Integer quantidade) {
    return new VendaRequest(
      List.of(new ItemVendaRequest(produtoId, quantidade))
    );
  }

  private Venda vendaExistente(Long id, StatusVenda status) {
    Venda venda = new Venda();
    venda.setId(id);
    venda.setStatus(status);
    venda.setValorTotal(20.0);
    return venda;
  }

  private ItemVenda criarItem(Produto produto, Integer quantidade) {
    ItemVenda item = new ItemVenda();
    item.setProduto(produto);
    item.setQuantidade(quantidade);
    item.setValorUnitario(produto.getPreco());
    item.setSubtotal(produto.getPreco() * quantidade);
    return item;
  }

  @Test
  void deveRegistrarVendaComSucesso() {
    when(produtoRepository.findById(1L))
      .thenReturn(Optional.of(produto));
    when(vendaRepository.save(any(Venda.class)))
      .thenAnswer(invocacao -> invocacao.getArgument(0));

    Venda resultado = vendaService.registrar(requisicao(1L, 2));

    assertNotNull(resultado);
    assertEquals(20.0, resultado.getValorTotal());
    assertEquals(StatusVenda.CONCLUIDA, resultado.getStatus());
    assertNotNull(resultado.getDataHora());
    assertEquals(1, resultado.getItens().size());
    assertEquals(18, produto.getEstoque());

    verify(produtoRepository).save(produto);
    verify(vendaRepository).save(any(Venda.class));
  }

  @Test
  void deveRegistrarVendaComMaisDeUmProduto() {
    Produto segundoProduto = new Produto();
    segundoProduto.setId(2L);
    segundoProduto.setNome("Sorvete de Morango");
    segundoProduto.setPreco(5.0);
    segundoProduto.setEstoque(10);
    segundoProduto.setDisponivel(true);

    VendaRequest request = new VendaRequest(List.of(
      new ItemVendaRequest(1L, 2),
      new ItemVendaRequest(2L, 3)
    ));

    when(produtoRepository.findById(1L))
      .thenReturn(Optional.of(produto));
    when(produtoRepository.findById(2L))
      .thenReturn(Optional.of(segundoProduto));
    when(vendaRepository.save(any(Venda.class)))
      .thenAnswer(invocacao -> invocacao.getArgument(0));

    Venda resultado = vendaService.registrar(request);

    assertEquals(35.0, resultado.getValorTotal());
    assertEquals(2, resultado.getItens().size());
    assertEquals(18, produto.getEstoque());
    assertEquals(7, segundoProduto.getEstoque());
    verify(produtoRepository, times(2)).save(any(Produto.class));
  }

  @Test
  void naoDeveRegistrarVendaQuandoProdutoNaoExiste() {
    when(produtoRepository.findById(99L))
      .thenReturn(Optional.empty());

    ResponseStatusException excecao = assertThrows(
      ResponseStatusException.class,
      () -> vendaService.registrar(requisicao(99L, 1))
    );

    assertEquals(HttpStatus.NOT_FOUND, excecao.getStatusCode());
    verify(vendaRepository, never()).save(any(Venda.class));
  }

  @Test
  void naoDeveRegistrarVendaQuandoProdutoEstaIndisponivel() {
    produto.setDisponivel(false);
    when(produtoRepository.findById(1L))
      .thenReturn(Optional.of(produto));

    ResponseStatusException excecao = assertThrows(
      ResponseStatusException.class,
      () -> vendaService.registrar(requisicao(1L, 1))
    );

    assertEquals(HttpStatus.BAD_REQUEST, excecao.getStatusCode());
    verify(vendaRepository, never()).save(any(Venda.class));
  }

  @Test
  void naoDeveRegistrarVendaQuandoEstoqueEstaNulo() {
    produto.setEstoque(null);
    when(produtoRepository.findById(1L))
      .thenReturn(Optional.of(produto));

    ResponseStatusException excecao = assertThrows(
      ResponseStatusException.class,
      () -> vendaService.registrar(requisicao(1L, 1))
    );

    assertEquals(HttpStatus.BAD_REQUEST, excecao.getStatusCode());
    verify(vendaRepository, never()).save(any(Venda.class));
  }

  @Test
  void naoDeveRegistrarVendaQuandoEstoqueEInsuficiente() {
    produto.setEstoque(1);
    when(produtoRepository.findById(1L))
      .thenReturn(Optional.of(produto));

    ResponseStatusException excecao = assertThrows(
      ResponseStatusException.class,
      () -> vendaService.registrar(requisicao(1L, 2))
    );

    assertEquals(HttpStatus.BAD_REQUEST, excecao.getStatusCode());
    verify(vendaRepository, never()).save(any(Venda.class));
  }

  @Test
  void deveBuscarVendaPorId() {
    Venda venda = vendaExistente(1L, StatusVenda.CONCLUIDA);
    when(vendaRepository.findById(1L)).thenReturn(Optional.of(venda));

    Venda resultado = vendaService.buscarPorId(1L);

    assertSame(venda, resultado);
  }

  @Test
  void deveLancarExcecaoQuandoVendaNaoExiste() {
    when(vendaRepository.findById(99L)).thenReturn(Optional.empty());

    ResponseStatusException excecao = assertThrows(
      ResponseStatusException.class,
      () -> vendaService.buscarPorId(99L)
    );

    assertEquals(HttpStatus.NOT_FOUND, excecao.getStatusCode());
  }

  @Test
  void deveListarTodasAsVendas() {
    List<Venda> vendas = List.of(
      vendaExistente(1L, StatusVenda.CONCLUIDA),
      vendaExistente(2L, StatusVenda.CANCELADA)
    );
    when(vendaRepository.findAll()).thenReturn(vendas);

    List<Venda> resultado = vendaService.listar();

    assertEquals(2, resultado.size());
    assertEquals(vendas, resultado);
  }

  @Test
  void deveBuscarVendasPorProduto() {
    when(produtoRepository.findById(1L))
      .thenReturn(Optional.of(produto));

    List<Venda> vendas = List.of(
      vendaExistente(1L, StatusVenda.CONCLUIDA)
    );
    when(vendaRepository.findDistinctByItens_Produto(produto))
      .thenReturn(vendas);

    List<Venda> resultado = vendaService.buscar(1L, null);

    assertEquals(vendas, resultado);
    verify(vendaRepository).findDistinctByItens_Produto(produto);
  }

  @Test
  void deveBuscarVendasPorProdutoEFiltrarPorStatus() {
    when(produtoRepository.findById(1L))
      .thenReturn(Optional.of(produto));

    Venda concluida = vendaExistente(1L, StatusVenda.CONCLUIDA);
    Venda cancelada = vendaExistente(2L, StatusVenda.CANCELADA);

    when(vendaRepository.findDistinctByItens_Produto(produto))
      .thenReturn(List.of(concluida, cancelada));

    List<Venda> resultado =
      vendaService.buscar(1L, StatusVenda.CONCLUIDA);

    assertEquals(1, resultado.size());
    assertSame(concluida, resultado.get(0));
  }

  @Test
  void naoDeveBuscarPorProdutoInexistente() {
    when(produtoRepository.findById(99L))
      .thenReturn(Optional.empty());

    ResponseStatusException excecao = assertThrows(
      ResponseStatusException.class,
      () -> vendaService.buscar(99L, null)
    );

    assertEquals(HttpStatus.NOT_FOUND, excecao.getStatusCode());
  }

  @Test
  void deveBuscarVendasPorStatus() {
    List<Venda> vendas = List.of(
      vendaExistente(1L, StatusVenda.CONCLUIDA)
    );
    when(vendaRepository.findByStatus(StatusVenda.CONCLUIDA))
      .thenReturn(vendas);

    List<Venda> resultado =
      vendaService.buscar(null, StatusVenda.CONCLUIDA);

    assertEquals(vendas, resultado);
    verify(vendaRepository).findByStatus(StatusVenda.CONCLUIDA);
  }

  @Test
  void deveListarVendasQuandoNaoHaFiltros() {
    List<Venda> vendas = List.of(
      vendaExistente(1L, StatusVenda.CONCLUIDA)
    );
    when(vendaRepository.findAll()).thenReturn(vendas);

    List<Venda> resultado = vendaService.buscar(null, null);

    assertEquals(vendas, resultado);
    verify(vendaRepository).findAll();
  }

  @Test
  void deveCancelarVendaEDevolverEstoque() {
    Venda venda = vendaExistente(1L, StatusVenda.CONCLUIDA);
    venda.getItens().add(criarItem(produto, 3));

    when(vendaRepository.findById(1L)).thenReturn(Optional.of(venda));
    when(vendaRepository.save(any(Venda.class)))
      .thenAnswer(invocacao -> invocacao.getArgument(0));

    Venda resultado = vendaService.cancelar(1L);

    assertEquals(StatusVenda.CANCELADA, resultado.getStatus());
    assertEquals(23, produto.getEstoque());
    verify(produtoRepository).save(produto);
    verify(vendaRepository).save(venda);
  }

  @Test
  void naoDeveCancelarVendaJaCancelada() {
    Venda venda = vendaExistente(1L, StatusVenda.CANCELADA);
    when(vendaRepository.findById(1L)).thenReturn(Optional.of(venda));

    ResponseStatusException excecao = assertThrows(
      ResponseStatusException.class,
      () -> vendaService.cancelar(1L)
    );

    assertEquals(HttpStatus.BAD_REQUEST, excecao.getStatusCode());
    verify(vendaRepository, never()).save(any(Venda.class));
  }

  @Test
  void deveDeletarVendaAtivaEDevolverEstoque() {
    Venda venda = vendaExistente(1L, StatusVenda.CONCLUIDA);
    venda.getItens().add(criarItem(produto, 2));

    when(vendaRepository.findById(1L)).thenReturn(Optional.of(venda));

    vendaService.deletarPorId(1L);

    assertEquals(22, produto.getEstoque());
    verify(produtoRepository).save(produto);
    verify(vendaRepository).delete(venda);
  }

  @Test
  void deveDeletarVendaCanceladaSemDevolverEstoqueNovamente() {
    Venda venda = vendaExistente(1L, StatusVenda.CANCELADA);
    venda.getItens().add(criarItem(produto, 2));

    when(vendaRepository.findById(1L)).thenReturn(Optional.of(venda));

    vendaService.deletarPorId(1L);

    assertEquals(20, produto.getEstoque());
    verify(produtoRepository, never()).save(any(Produto.class));
    verify(vendaRepository).delete(venda);
  }

  @Test
  void naoDeveDeletarVendaInexistente() {
    when(vendaRepository.findById(99L)).thenReturn(Optional.empty());

    assertThrows(
      ResponseStatusException.class,
      () -> vendaService.deletarPorId(99L)
    );

    verify(vendaRepository, never()).delete(any(Venda.class));
  }
}
