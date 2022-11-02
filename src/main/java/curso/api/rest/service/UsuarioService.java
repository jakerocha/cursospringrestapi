package curso.api.rest.service;
import org.springframework.stereotype.Service;

import curso.api.rest.repository.UsuarioRepository;

@Service
public class UsuarioService {
	
	final UsuarioRepository usuarioRepository;
	
	public UsuarioService(UsuarioRepository usuarioRepository) { 
		this.usuarioRepository = usuarioRepository;
	}

}
