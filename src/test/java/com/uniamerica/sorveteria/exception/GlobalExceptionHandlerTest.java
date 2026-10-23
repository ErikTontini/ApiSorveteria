
package com.uniamerica.sorveteria.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {

  private GlobalExceptionHandler handler;

  @BeforeEach
  void setUp() {
    handler = new GlobalExceptionHandler();
  }

  @Test
  void deveTratarResponseStatusException() {
    ResponseStatusException exception = new ResponseStatusException(
      HttpStatus.NOT_FOUND, "Produto não encontrado");

    var resposta = handler.tratarResponseStatusException(exception);

    assertEquals(404, resposta.getStatusCode().value());
    assertNotNull(resposta.getBody());
    assertEquals(404, resposta.getBody().get("status"));
    assertEquals("Produto não encontrado", resposta.getBody().get("mensagem"));
  }

  @Test
  void deveTratarErroDeValidacao() {
    Object alvo = new Object();
    BeanPropertyBindingResult resultado =
      new BeanPropertyBindingResult(alvo, "request");

    resultado.addError(new FieldError(
      "request", "nome", "", false,
      new String[]{"NotBlank"}, null,
      "O nome é obrigatório"));

    MethodArgumentNotValidException exception =
      new MethodArgumentNotValidException(
        null, resultado);

    var resposta = handler.tratarValidacao(exception);

    assertEquals(400, resposta.getStatusCode().value());
    assertNotNull(resposta.getBody());
    assertEquals(400, resposta.getBody().get("status"));
    assertEquals("Erro de validação", resposta.getBody().get("mensagem"));

    @SuppressWarnings("unchecked")
    var campos = (java.util.Map<String, String>)
      resposta.getBody().get("campos");

    assertEquals("O nome é obrigatório", campos.get("nome"));
  }

  @Test
  void deveTratarErroDeIntegridadeDoBanco() {
    DataIntegrityViolationException exception =
      new DataIntegrityViolationException("Violação de integridade");

    var resposta = handler.tratarErroBanco(exception);

    assertEquals(409, resposta.getStatusCode().value());
    assertNotNull(resposta.getBody());
    assertEquals(409, resposta.getBody().get("status"));
    assertTrue(resposta.getBody().get("mensagem").toString()
      .contains("restrição do banco de dados"));
  }
}
