package com.uniamerica.sorveteria.controller.usuario.dto;

import com.uniamerica.sorveteria.entity.Cargo;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UsuarioRequest(

  @NotBlank(message = "O nome é obrigatório")
  String nome,

  @NotBlank(message = "O login é obrigatório")
  String login,

  @NotBlank(message = "A senha é obrigatória")
  String senha,

  @NotNull(message = "O cargo é obrigatório")
  Cargo cargo,

  @NotBlank(message = "O CEP é obrigatório")
  @Pattern(regexp = "\\d{8}", message = "O CEP deve conter 8 números")
  String cep

) {
}
