package com.uniamerica.sorveteria.service;

import com.uniamerica.sorveteria.client.viacep.ViaCepClient;
import com.uniamerica.sorveteria.client.viacep.ViaCepResponse;
import com.uniamerica.sorveteria.controller.usuario.dto.UsuarioRequest;
import com.uniamerica.sorveteria.entity.Usuario;
import com.uniamerica.sorveteria.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
@Slf4j
@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
   private final ViaCepClient viaCepClient;

    public Usuario cadastrar(UsuarioRequest request) {
      log.info("Inciando Cadastro de usuario");

        if (usuarioRepository.existsByLogin(request.login())) {
          log.warn("Erro ao cadastrar usuario.");
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Já existe um usuario com esse login");
        }

        Usuario usuario = new Usuario();
        usuario.setNome(request.nome());
        usuario.setLogin(request.login());
        usuario.setSenha(request.senha());
        usuario.setCargo(request.cargo());
        usuario.setCep(request.cep());
        usuario.setAtivo(true);

      log.info("Usuario {} Cadastrado com sucesso", usuario.getNome());

        return usuarioRepository.save(usuario);
    }

  public ViaCepResponse buscarEndereco(Long usuarioId) {

    log.info("Buscando endereço do usuário {}", usuarioId);

    Usuario usuario = usuarioRepository.findById(usuarioId).orElseThrow(() ->
      new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado com id " + usuarioId));

    if (usuario.getCep() == null || usuario.getCep().isBlank()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Usuário não possui CEP cadastrado");
    }

    ViaCepResponse endereco =
      viaCepClient.buscarCep(usuario.getCep());

    log.info("Endereço encontrado para o usuário {}", usuarioId);

    return endereco;
  }
}
