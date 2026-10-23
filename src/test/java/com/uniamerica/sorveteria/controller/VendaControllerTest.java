
package com.uniamerica.sorveteria.controller;

import com.uniamerica.sorveteria.controller.venda.VendaController;
import com.uniamerica.sorveteria.controller.venda.dto.VendaRequest;
import com.uniamerica.sorveteria.entity.StatusVenda;
import com.uniamerica.sorveteria.entity.Venda;
import com.uniamerica.sorveteria.service.VendaService;
import tools.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(VendaController.class)
class VendaControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockitoBean
  private VendaService vendaService;

  private Venda vendaMock() {
    Venda venda = new Venda();
    venda.setId(1L);
    venda.setItens(new ArrayList<>());
    venda.setValorTotal(30.0);
    venda.setStatus(StatusVenda.CONCLUIDA);
    return venda;
  }

  private VendaRequest requestValido() {
    return new VendaRequest(
      List.of(new com.uniamerica.sorveteria.controller.venda.dto.ItemVendaRequest(1L, 2))
    );
  }

  @Test
  void deveRegistrarVendaComSucesso() throws Exception {
    Venda venda = vendaMock();
    when(vendaService.registrar(any(VendaRequest.class))).thenReturn(venda);

    mockMvc.perform(post("/api/vendas")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(requestValido())))
      .andExpect(status().isCreated())
      .andExpect(jsonPath("$.id").value(1))
      .andExpect(jsonPath("$.valorTotal").value(30.0))
      .andExpect(jsonPath("$.status").value("CONCLUIDA"));

    verify(vendaService).registrar(any(VendaRequest.class));
  }

  @Test
  void deveRetornarBadRequestQuandoVendaNaoPossuirItens() throws Exception {
    VendaRequest request = new VendaRequest(List.of());

    mockMvc.perform(post("/api/vendas")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)))
      .andExpect(status().isBadRequest());

    verifyNoInteractions(vendaService);
  }

  @Test
  void deveListarTodasAsVendas() throws Exception {
    when(vendaService.listar()).thenReturn(List.of(vendaMock()));

    mockMvc.perform(get("/api/vendas"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.length()").value(1))
      .andExpect(jsonPath("$[0].id").value(1))
      .andExpect(jsonPath("$[0].valorTotal").value(30.0));

    verify(vendaService).listar();
  }

  @Test
  void deveBuscarVendaPorId() throws Exception {
    when(vendaService.buscarPorId(1L)).thenReturn(vendaMock());

    mockMvc.perform(get("/api/vendas/1"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.id").value(1))
      .andExpect(jsonPath("$.status").value("CONCLUIDA"));

    verify(vendaService).buscarPorId(1L);
  }

  @Test
  void deveBuscarVendasPorProdutoEStatus() throws Exception {
    when(vendaService.buscar(1L, StatusVenda.CONCLUIDA))
      .thenReturn(List.of(vendaMock()));

    mockMvc.perform(get("/api/vendas/busca")
        .param("produtoId", "1")
        .param("status", "CONCLUIDA"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.length()").value(1))
      .andExpect(jsonPath("$[0].id").value(1));

    verify(vendaService).buscar(1L, StatusVenda.CONCLUIDA);
  }

  @Test
  void deveListarVendasSemFiltros() throws Exception {
    when(vendaService.buscar(null, null)).thenReturn(List.of());

    mockMvc.perform(get("/api/vendas/busca"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.length()").value(0));

    verify(vendaService).buscar(null, null);
  }

  @Test
  void deveCancelarVenda() throws Exception {
    Venda venda = vendaMock();
    venda.setStatus(StatusVenda.CANCELADA);
    when(vendaService.cancelar(1L)).thenReturn(venda);

    mockMvc.perform(patch("/api/vendas/1/cancelar"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.id").value(1))
      .andExpect(jsonPath("$.status").value("CANCELADA"));

    verify(vendaService).cancelar(1L);
  }

  @Test
  void deveDeletarVenda() throws Exception {
    doNothing().when(vendaService).deletarPorId(1L);

    mockMvc.perform(delete("/api/vendas/1"))
      .andExpect(status().isNoContent())
      .andExpect(content().string(""));

    verify(vendaService).deletarPorId(1L);
  }
}
