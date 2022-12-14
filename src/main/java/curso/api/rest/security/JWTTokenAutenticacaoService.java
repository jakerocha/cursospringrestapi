package curso.api.rest.security;

import java.io.IOException;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import curso.api.rest.ApplicationContextLoad;
import curso.api.rest.model.Usuario;
import curso.api.rest.repository.UsuarioRepository;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Service
@Component
public class JWTTokenAutenticacaoService {

	//Tempo de validade do token 2 dias 
	private static final long EXPIRATION_TIME = 172800000; //milisegundos
	
	//Uma senha unica para compor a autenticação e ajudar na segurança
	private static final String SECRET = "SenhaExtremamenteSecreta";
	
	//prefixo padrão de token
	private static final String TOKEN_PREFIX = "Bearer";
	
	//header de autorizacao do token 
	private static final String HEADER_STRING = "Authorization";
	
	/*Gerando token de autenticação e adicionando ao cabeçalho e resposta http*/
	public void addAuthentication(HttpServletResponse response, String username)  throws IOException{
		//montagem do token	
		String JWT = Jwts.builder() //chama o gerador de token
						 .setSubject(username) //adiciona o usuario
				         .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME)) //tempo de expiração
				         .signWith(SignatureAlgorithm.HS512, SECRET).compact(); //compactação e algoritimo de geração de senha
		
		//junta o token com o prefixo
		String token = TOKEN_PREFIX + " " + JWT; //Ex: "Bearer jaçsldkfjasdlçfkjajk440r2u190-23478poehf"
		
		//adiciona no cabeçalho http
		response.addHeader(HEADER_STRING, token); //Ex: "Authorization: Bearer jaçsldkfjasdlçfkjajk440r2u190-23478poehf"
		
		ApplicationContextLoad.getApplicationContext()
				.getBean(UsuarioRepository.class).atualizaTokenUser(JWT, username);
		
		//liberando resposta para portas diferentes que usam a API ou caso clientes WEB
		liberacaoCors(response);
		
		//escreve o token como resposta no corpo http
		response.getWriter().write("{\"Authorization\": \""+token+"\"}");
		
	}
	
	/*retorna o usuario validado com token ou caso nao seja valido retorna mull*/
	public Authentication getAuthentication(HttpServletRequest request, HttpServletResponse response) { 
		//pega o token enviado no cabeçalho http
		String token = request.getHeader(HEADER_STRING);
		try {
		if (token != null) {
			
			String tokenLimpo = token.replace(TOKEN_PREFIX, "").trim();
			
			//faz a validação do token do usuário na requisição
			String user = Jwts.parser().setSigningKey(SECRET) //"Bearer jaçsldkfjasdlçfkjajk440r2u190-23478poehf"
					.parseClaimsJws(tokenLimpo) //"jaçsldkfjasdlçfkjajk440r2u190-23478poehf"
					.getBody().getSubject();//nome do usuario, Ex: João silva
			if (user != null) { 
				Usuario usuario = ApplicationContextLoad.getApplicationContext()
						.getBean(UsuarioRepository.class).findUserByLogin(user);
				//retorna o usuario logado
				if (usuario != null) {					
					if (tokenLimpo.equalsIgnoreCase(usuario.getToken())) {
					return new UsernamePasswordAuthenticationToken(
							usuario.getLogin(), 
							usuario.getSenha(),
							usuario.getAuthorities());
					}
				} 
			} 
		} /*fim condicao token*/
		} catch(ExpiredJwtException ex) {
			try {
				response.getOutputStream().println("Seu TOKEN está expirado, faça o login ou informe um novo TOKEN para autenticação.");
			} catch (IOException e) {}
		}
		
		//liberando resposta para portas diferentes que usam a API ou caso clientes WEB
		liberacaoCors(response);
		
		return null; //não autorizado
		
	}

	//libera resposta para portas diferentes que usam a API ou caso clientes WEB
	private void liberacaoCors(HttpServletResponse response) {
		
		if (response.getHeader("Access-Control-Allow-Origin")==null) {
			response.addHeader("Access-Control-Allow-Origin", "*");
		}
		if (response.getHeader("Access-Control-Allow-Headers")==null) {
			response.addHeader("Access-Control-Allow-Headers", "*");
		}
		if (response.getHeader("Access-Control-Request-Headers")==null) {
			response.addHeader("Access-Control-Request-Headers", "*");
		}
		if (response.getHeader("Access-Control-Allow-Methods")==null) {
			response.addHeader("Access-Control-Allow-Methods", "*");
		}
	}
		
}
