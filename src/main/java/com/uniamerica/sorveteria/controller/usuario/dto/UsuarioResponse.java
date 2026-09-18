package com.uniamerica.sorveteria.controller.usuario.dto;

import com.uniamerica.sorveteria.entity.Cargo;
import com.uniamerica.sorveteria.entity.Usuario;

public record UsuarioResponse(
        Long id,
        String nome,
        String login,
        Cargo cargo,
        Boolean ativo,
        String cep
) {

    public static UsuarioResponse fromEntity(Usuario usuario) {

        return new UsuarioResponse(
                usuario.getId(),
                usuario.getNome(),
                usuario.getLogin(),
                usuario.getCargo(),
                usuario.getAtivo(),
                usuario.getCep()
        );
    }
}
