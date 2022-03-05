package com.mgslira.minhasfinancas.service.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mgslira.minhasfinancas.exception.ErroAutenticacao;
import com.mgslira.minhasfinancas.exception.RegraNegocioException;
import com.mgslira.minhasfinancas.model.entity.Usuario;
import com.mgslira.minhasfinancas.model.repository.UsuarioRepository;
import com.mgslira.minhasfinancas.service.UsuarioService;

@Service
public class UsuarioServiceImpl implements UsuarioService{

	@Autowired
	private UsuarioRepository repository;
	
	public UsuarioServiceImpl(UsuarioRepository repository) {
		super();
		this.repository = repository;
	}

	@Override
	public Usuario autenticar(String email, String senha) {
		Optional<Usuario> usuario = repository.findByEmail(email);
		
		if(!usuario.isPresent()) {
			throw new ErroAutenticacao("Usuário não encontrado para o email informado!");
		}
		
		if(!usuario.get().getSenha().equals(senha)) {
			throw new ErroAutenticacao("Senha inválida!");
		}
		
		return usuario.get();
	}

	@Override
	@Transactional
	public Usuario salvarUsuario(Usuario usuario) {
		
		validarEmail(usuario.getEmail());
		return repository.save(usuario);
	}

	@Override
	public void validarEmail(String email) {
		boolean verificar = repository.existsByEmail(email);
		
		if(verificar) {
			throw new RegraNegocioException("Já existe usuário cadastrado com este email.");
		}
	}

	@Override
	public Optional<Usuario> obterPorId(Long id) {
		
		return repository.findById(id);
	}

	
}
