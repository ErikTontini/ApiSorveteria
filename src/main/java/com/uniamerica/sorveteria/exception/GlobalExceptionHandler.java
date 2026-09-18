package com.uniamerica.sorveteria.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.server.ResponseStatusException;

import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

  // Trata os ResponseStatusException lançados pelos Services
  @ResponseBody
  @ExceptionHandler(ResponseStatusException.class)
  public ResponseEntity<Map<String, Object>> tratarResponseStatusException(
    ResponseStatusException exception) {

    log.error("Erro na requisição: {}", exception.getReason());

    Map<String, Object> erro = new LinkedHashMap<>();

    erro.put("status", exception.getStatusCode().value());
    erro.put("mensagem", exception.getReason());

    return ResponseEntity.status(exception.getStatusCode()).body(erro);
  }


  // Trata erros do Bean Validation
  @ResponseBody
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<Map<String, Object>> tratarValidacao(
    MethodArgumentNotValidException exception) {

    log.warn("Erro de validação na requisição");

    Map<String, String> campos = new LinkedHashMap<>();

    exception.getBindingResult()
      .getFieldErrors()
      .forEach(erro ->
        campos.put(
          erro.getField(),
          erro.getDefaultMessage()
        )
      );

    Map<String, Object> resposta = new LinkedHashMap<>();

    resposta.put("status", 400);
    resposta.put("mensagem", "Erro de validação");
    resposta.put("campos", campos);

    return ResponseEntity.badRequest().body(resposta);
  }
}
