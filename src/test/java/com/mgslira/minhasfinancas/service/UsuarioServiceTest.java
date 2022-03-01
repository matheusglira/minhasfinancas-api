package com.mgslira.minhasfinancas.service;

import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.mgslira.minhasfinancas.exception.ErroAutenticacao;
import com.mgslira.minhasfinancas.exception.RegraNegocioException;
import com.mgslira.minhasfinancas.model.entity.Usuario;
import com.mgslira.minhasfinancas.model.repository.UsuarioRepository;
import com.mgslira.minhasfinancas.service.impl.UsuarioServiceImpl;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class UsuarioServiceTest {

	
	@SpyBean
	UsuarioServiceImpl service;
	
	@MockBean
	UsuarioRepository repository;
	
	@Test
	public void deveSalvarUmUsuario() {
		Assertions.assertDoesNotThrow(() -> {
			
			Mockito.doNothing().when(service).validarEmail(Mockito.anyString());
			Usuario usuario = Usuario.builder().id(1l).nome("nome").email("usuario@email.com").senha("senha").build();		
			
			Mockito.when(repository.save(Mockito.any(Usuario.class))).thenReturn(usuario);
			
			Usuario usuarioSalvo = service.salvarUsuario(new Usuario());
			
			org.assertj.core.api.Assertions.assertThat(usuarioSalvo).isNotNull();
			org.assertj.core.api.Assertions.assertThat(usuarioSalvo.getId()).isNotNull().isEqualTo(1l);
			org.assertj.core.api.Assertions.assertThat(usuarioSalvo.getNome()).isNotNull().isEqualTo("nome");
			org.assertj.core.api.Assertions.assertThat(usuarioSalvo.getEmail()).isNotNull().isEqualTo("usuario@email.com");
			org.assertj.core.api.Assertions.assertThat(usuarioSalvo.getSenha()).isNotNull().isEqualTo("senha");
		});
		
	}
	
	@Test
	public void naoDeveSalvarUsuarioComEmailJaCadastrado() {
		Assertions.assertThrows(RegraNegocioException.class, () -> {
			String email = "email@gmail.com";
			
			Usuario usuario = Usuario.builder().email(email).build();
			
			Mockito.doThrow(RegraNegocioException.class).when(service).validarEmail(email);
			
			service.salvarUsuario(usuario);
			
			Mockito.verify(repository, Mockito.never()).save(usuario);
			
		});

	}
	
	@Test
	public void deveAutenticarUmUsuarioComSucesso() {
			String email = "email@gmail.com";
			String senha = "senha";
			
			Usuario usuario = Usuario.builder().email(email).senha(senha).id(1l).nome("nome").build();
			Mockito.when(repository.findByEmail(email)).thenReturn(Optional.of(usuario));
			
			Usuario result = service.autenticar(email, senha);
			
			Assertions.assertNotNull(result);
			//org.assertj.core.api.Assertions.assertThat(result).isNotNull();
		
	}
	
	@Test
	public void deveLancarErroQUandoNaoEncontrarUsuarioCadastradoComOEmailInformado() {
		Throwable exception = Assertions.assertThrows(ErroAutenticacao.class, () ->{
			
			Mockito.when(repository.findByEmail(Mockito.anyString())).thenReturn(Optional.empty());
			
			service.autenticar("email@email.com", "senha");
		});
		
		org.assertj.core.api.Assertions.assertThat(exception).isInstanceOf(ErroAutenticacao.class).hasMessage("Usuário não encontrado para o email informado!");
	}
	
	@Test
	public void deveLancarErroQuandoSenhaNaoBater() {
		Throwable exception =  Assertions.assertThrows(ErroAutenticacao.class, () -> {
			
			String senha = "senha";
			Usuario usuario = Usuario.builder().email("email@gmail.com").senha(senha).build();
			Mockito.when(repository.findByEmail(Mockito.anyString())).thenReturn(Optional.of(usuario));
			
			service.autenticar("email@gmail.com", "outrasenha");
			
		});
		
		org.assertj.core.api.Assertions.assertThat(exception).isInstanceOf(ErroAutenticacao.class).hasMessage("Senha inválida!");
	}
	
	@Test
	public void deveValidarEmail() {
		Assertions.assertDoesNotThrow(() -> {
			
			Mockito.when(repository.existsByEmail(Mockito.anyString())).thenReturn(false);
			service.validarEmail("email@gmail.com");
		});
	}
	
	@Test
	public void deveLancarErroAoValidarEmailQuandoExistirEmailCadastrado() {
		Assertions.assertThrows(RegraNegocioException.class, () -> {
			
			Mockito.when(repository.existsByEmail(Mockito.anyString())).thenReturn(true);
			
			service.validarEmail("email@gmail.com");
		});
	}
}
