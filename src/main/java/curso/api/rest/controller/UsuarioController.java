package curso.api.rest.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;

import curso.api.rest.model.Telefone;
import curso.api.rest.model.Usuario;
import curso.api.rest.model.UsuarioDTO;
import curso.api.rest.repository.UsuarioRepository;

//@CrossOrigin(origins = {"https://sistemas.sefaz.am.gov.br/", "https://homologacao.sefaz.am.gov.br/"}) 
@CrossOrigin(origins = "*", maxAge = 3600) /*liberado pra todos*/
@RestController
@RequestMapping(value="v1/usuario")
public class UsuarioController {
	
	@Autowired
	private UsuarioRepository usuarioRepository; 
	
	@GetMapping(value="/{id}", produces = "application/json")
	//@Cacheable("cacheusuer")
	@CacheEvict(value="cacheuser", allEntries = true)
	@CachePut("cacheuser")
	public ResponseEntity<UsuarioDTO> initV1(@PathVariable(value="id") Long id) {
		Optional<Usuario> usuario = usuarioRepository.findById(id);
		return new ResponseEntity<UsuarioDTO>(new UsuarioDTO(usuario.get()), HttpStatus.OK) ; 
	}
	
	@GetMapping(value="/{id}", produces = "application/json", headers = "X-API-Version=v2")
	public ResponseEntity<Usuario> initV2(@PathVariable(value="id") Long id) {
		Optional<Usuario> usuario = usuarioRepository.findById(id);
		return new ResponseEntity<Usuario>(usuario.get(), HttpStatus.OK) ; 
	}
	
	
	@GetMapping(value="/", produces = "application/json")
	@Cacheable("cacheusuarios")
	//@CacheEvict(value="cacheusuarios", allEntries = true)
	//@CachePut("cacheusuarios")
	public ResponseEntity<List<Usuario>> listar() throws InterruptedException {
		List<Usuario> lista = (List<Usuario>) usuarioRepository.findAll();
		Thread.sleep(6000L);//6 segundos
		return new ResponseEntity<List<Usuario>>(lista, HttpStatus.OK); 
	}
	
	@PostMapping(value="/", produces = "application/json")
	public ResponseEntity<Usuario> cadastrar(@RequestBody Usuario usuario) throws Exception{
		if (usuario.getTelefones()!=null) {
			for (Telefone telefone: usuario.getTelefones()) { 
				telefone.setUsuario(usuario);
			}
		}
		/*consumindo uma API publica externa de CEP*/
		URL url = new URL("https://viacep.com.br/ws/"+usuario.getCep()+"/json/");
		URLConnection connection = url.openConnection();
		InputStream is = connection.getInputStream();
		BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
		String cep="";
		StringBuilder jsonCep = new StringBuilder();
		while((cep = br.readLine()) !=null) {
			jsonCep.append(cep);
		}
		Usuario userAux = new Gson().fromJson(jsonCep.toString(), Usuario.class);
		usuario.setCep(userAux.getCep());
		usuario.setLogradouro(userAux.getLogradouro());
		usuario.setBairro(userAux.getBairro());
		usuario.setComplemento(userAux.getComplemento());
		usuario.setLocalidade(userAux.getLocalidade());
		usuario.setUf(userAux.getUf());
		/*consumindo uma API publica externa de CEP*/
		
		usuario.setSenha(new BCryptPasswordEncoder().encode(usuario.getSenha()));
		Usuario usuarioSalvo = (Usuario) usuarioRepository.save(usuario);
		return new ResponseEntity<Usuario>(usuarioSalvo, HttpStatus.CREATED);
	}
	
	@PutMapping(value="/", produces = "application/json")
	public ResponseEntity<Usuario> atualizar(@RequestBody Usuario usuario){
		if (usuario.getTelefones()!=null) {
			for (Telefone telefone: usuario.getTelefones()) { 
				telefone.setUsuario(usuario);
			}
		}
		Usuario userTemporario = usuarioRepository.findUserByLogin(usuario.getLogin());
		if (!userTemporario.equals(usuario.getSenha())) {
			usuario.setSenha(new BCryptPasswordEncoder().encode(usuario.getSenha()));
		}
		
		Usuario usuarioSalvo = (Usuario) usuarioRepository.save(usuario);
		return new ResponseEntity<Usuario>(usuarioSalvo, HttpStatus.OK);
	}

	@DeleteMapping(value="/{id}")
	public ResponseEntity<Object> deletar(@PathVariable(value="id") Long id) {
		Optional<Usuario> usuarioOptional = usuarioRepository.findById(id);
		if (!usuarioOptional.isPresent()) { 
			 return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
		}
		usuarioRepository.delete(usuarioOptional.get());
        return ResponseEntity.status(HttpStatus.OK).body("User deleted successfully.");
	}

}
