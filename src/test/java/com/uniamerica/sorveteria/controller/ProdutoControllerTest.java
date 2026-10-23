
package com.uniamerica.sorveteria.controller;

import tools.jackson.databind.ObjectMapper;
import com.uniamerica.sorveteria.controller.produto.ProdutoController;
import com.uniamerica.sorveteria.controller.produto.dto.ProdutoRequest;
import com.uniamerica.sorveteria.entity.Categoria;
import com.uniamerica.sorveteria.entity.Produto;
import com.uniamerica.sorveteria.service.ProdutoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProdutoController.class)
class ProdutoControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockitoBean
  private ProdutoService produtoService;

  private Categoria categoria() {
    return Categoria.values()[0];
  }

  private ProdutoRequest requestValido() {
    return new ProdutoRequest(
      "Sorvete de morango",
      categoria(),
      15.0,
      20,
      true
    );
  }

  private Produto produtoMock() {
    Produto produto = new Produto();

    produto.setId(1L);
    produto.setNome("Sorvete de morango");
    produto.setCategoria(categoria());
    produto.setPreco(15.0);
    produto.setEstoque(20);
    produto.setDisponivel(true);

    return produto;
  }
  @Test
  void deveCadastrarProdutoERetornarCreated() throws Exception {
    when(produtoService.salvar(any(ProdutoRequest.class)))
      .thenReturn(produtoMock());

    mockMvc.perform(post("/api/produtos")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(requestValido())))
      .andExpect(status().isCreated())
      .andExpect(jsonPath("$.id").value(1))
      .andExpect(jsonPath("$.nome").value("Sorvete de morango"))
      .andExpect(jsonPath("$.preco").value(15.0))
      .andExpect(jsonPath("$.estoque").value(20))
      .andExpect(jsonPath("$.disponivel").value(true));

    verify(produtoService).salvar(any(ProdutoRequest.class));
  }

  @Test
  void deveListarTodosOsProdutos() throws Exception {
    when(produtoService.listar())
      .thenReturn(List.of(produtoMock()));

    mockMvc.perform(get("/api/produtos"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.length()").value(1))
      .andExpect(jsonPath("$[0].id").value(1))
      .andExpect(jsonPath("$[0].nome").value("Sorvete de morango"));

    verify(produtoService).listar();
  }

  @Test
  void deveBuscarProdutoPorId() throws Exception {
    when(produtoService.buscarPorId(1L))
      .thenReturn(produtoMock());

    mockMvc.perform(get("/api/produtos/1"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.id").value(1))
      .andExpect(jsonPath("$.nome").value("Sorvete de morango"));

    verify(produtoService).buscarPorId(1L);
  }

  @Test
  void deveBuscarProdutosPorCategoriaENome() throws Exception {
    when(produtoService.buscar(categoria(), "morango"))
      .thenReturn(List.of(produtoMock()));

    mockMvc.perform(get("/api/produtos/busca")
        .param("categoria", categoria().name())
        .param("nome", "morango"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.length()").value(1))
      .andExpect(jsonPath("$[0].nome").value("Sorvete de morango"));

    verify(produtoService).buscar(categoria(), "morango");
  }

  @Test
  void deveListarProdutosDisponiveisParaVenda() throws Exception {
    when(produtoService.listarDisponiveisParaVenda())
      .thenReturn(List.of(produtoMock()));

    mockMvc.perform(get("/api/produtos/venda"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.length()").value(1))
      .andExpect(jsonPath("$[0].disponivel").value(true));

    verify(produtoService).listarDisponiveisParaVenda();
  }

  @Test
  void deveAtualizarProdutoERetornarOk() throws Exception {
    when(produtoService.atualizar(eq(1L), any(ProdutoRequest.class)))
      .thenReturn(produtoMock());

    mockMvc.perform(put("/api/produtos/1")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(requestValido())))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.id").value(1))
      .andExpect(jsonPath("$.nome").value("Sorvete de morango"));

    verify(produtoService).atualizar(eq(1L), any(ProdutoRequest.class));
  }

  @Test
  void deveAtualizarProdutoParcialmente() throws Exception {
    when(produtoService.atualizarParcial(eq(1L), any(ProdutoRequest.class)))
      .thenReturn(produtoMock());

    mockMvc.perform(patch("/api/produtos/1")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(requestValido())))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.id").value(1))
      .andExpect(jsonPath("$.preco").value(15.0));

    verify(produtoService).atualizarParcial(
      eq(1L), any(ProdutoRequest.class));
  }

  @Test
  void deveDeletarProdutoERetornarNoContent() throws Exception {
    doNothing().when(produtoService).deletarPorId(1L);

    mockMvc.perform(delete("/api/produtos/1"))
      .andExpect(status().isNoContent())
      .andExpect(content().string(""));

    verify(produtoService).deletarPorId(1L);
  }

  @Test
  void naoDeveCadastrarProdutoComDadosInvalidos() throws Exception {
    String jsonInvalido = """
                {
                    "nome": "",
                    "categoria": null,
                    "preco": -1.0,
                    "estoque": -1,
                    "disponivel": null
                }
                """;

    mockMvc.perform(post("/api/produtos")
        .contentType(MediaType.APPLICATION_JSON)
        .content(jsonInvalido))
      .andExpect(status().isBadRequest());

    verifyNoInteractions(produtoService);
  }
}
