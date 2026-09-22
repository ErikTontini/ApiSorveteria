package com.uniamerica.sorveteria.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
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

  @ResponseBody
  @ExceptionHandler(DataIntegrityViolationException.class)
  public ResponseEntity<Map<String, Object>> tratarErroBanco(
    DataIntegrityViolationException exception) {

    log.error("Erro de integridade no banco de dados", exception);

    Map<String, Object> erro = new LinkedHashMap<>();

    erro.put("status", HttpStatus.CONFLICT.value());
    erro.put("mensagem", "Nao foi possível realizar a operação devido a uma restrição do banco de dados");

    return ResponseEntity.status(HttpStatus.CONFLICT).body(erro);
  }
}
