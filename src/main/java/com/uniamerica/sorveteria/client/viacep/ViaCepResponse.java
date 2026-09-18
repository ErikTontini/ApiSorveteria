package com.uniamerica.sorveteria.client.viacep;

public record ViaCepResponse(
  String cep,
  String logradouro,
  String bairro,
  String localidade,
  String uf,
  String ddd
) {
}
