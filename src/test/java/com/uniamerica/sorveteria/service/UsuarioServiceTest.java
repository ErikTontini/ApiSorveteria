package com.uniamerica.sorveteria.service;

import com.uniamerica.sorveteria.client.viacep.ViaCepClient;
import com.uniamerica.sorveteria.client.viacep.ViaCepResponse;
import com.uniamerica.sorveteria.controller.usuario.dto.UsuarioRequest;
import com.uniamerica.sorveteria.entity.Usuario;
import com.uniamerica.sorveteria.repository.UsuarioRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import com.uniamerica.sorveteria.entity.Cargo;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private ViaCepClient viaCepClient;

    @InjectMocks
    private UsuarioService usuarioService;


  private UsuarioRequest criarRequest() {
    return new UsuarioRequest(
      "Miguel",
      "miguel",
      "123456",
      Cargo.FUNCIONARIO,
      "85851000"
    );
  }
    private Usuario criarUsuarioComCep(String cep) {
        Usuario usuario = new Usuario();
        usuario.setNome("Miguel");
        usuario.setLogin("miguel");
        usuario.setSenha("123456");
        usuario.setCep(cep);
        return usuario;
    }

    // cadastra

    @Test
    @DisplayName("Deve cadastrar usuário quando o login ainda não existe")
    void deveCadastrarUsuarioComSucesso() {
        UsuarioRequest request = criarRequest();

        when(usuarioRepository.existsByLogin(request.login())).thenReturn(false);
        when(usuarioRepository.save(any(Usuario.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Usuario resultado = usuarioService.cadastrar(request);

        assertNotNull(resultado);
        assertEquals("miguel", resultado.getLogin());
        assertEquals(Cargo.FUNCIONARIO, resultado.getCargo());
        assertEquals(true, resultado.getAtivo());
        assertEquals("85851000", resultado.getCep());
        verify(usuarioRepository).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Deve lançar CONFLICT quando o login já existe")
    void deveLancarConflitoQuandoLoginJaExiste() {
        UsuarioRequest request = criarRequest();

        when(usuarioRepository.existsByLogin(request.login())).thenReturn(true);

        ResponseStatusException erro = assertThrows(
                ResponseStatusException.class,
                () -> usuarioService.cadastrar(request));

        assertEquals(HttpStatus.CONFLICT, erro.getStatusCode());
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    @DisplayName(" usuário tem CEP")
    void BuscarEnderecoComSucesso() {
        Usuario usuario = criarUsuarioComCep("85851000");
        ViaCepResponse enderecoEsperado = mock(ViaCepResponse.class);

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(viaCepClient.buscarCep("85851000")).thenReturn(enderecoEsperado);

        ViaCepResponse resultado = usuarioService.buscarEndereco(1L);

        assertSame(enderecoEsperado, resultado);
        verify(viaCepClient).buscarCep("85851000");
    }

    @Test
    @DisplayName(" usuário não existe")
    void UsuarioNaoExiste() {
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        ResponseStatusException erro = assertThrows(
                ResponseStatusException.class,
                () -> usuarioService.buscarEndereco(99L));

        assertEquals(HttpStatus.NOT_FOUND, erro.getStatusCode());
        verify(viaCepClient, never()).buscarCep(anyString());
    }

    @Test
    @DisplayName("CEP e nulo")
    void CepNulo() {
        Usuario usuario = criarUsuarioComCep(null);

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

        ResponseStatusException erro = assertThrows(
                ResponseStatusException.class,
                () -> usuarioService.buscarEndereco(1L));

        assertEquals(HttpStatus.BAD_REQUEST, erro.getStatusCode());
        verify(viaCepClient, never()).buscarCep(anyString());
    }

    @Test
    @DisplayName("CEP está em branco")
    void CepEmBranco() {
        Usuario usuario = criarUsuarioComCep("   ");

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

        ResponseStatusException erro = assertThrows(
                ResponseStatusException.class,
                () -> usuarioService.buscarEndereco(1L));

        assertEquals(HttpStatus.BAD_REQUEST, erro.getStatusCode());
        verify(viaCepClient, never()).buscarCep(anyString());
    }
}
