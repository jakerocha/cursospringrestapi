package curso.api.rest.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import curso.api.rest.service.ImplementacaoUserDetailService;

@Configuration
@EnableWebSecurity
public class WebConfigSecurity extends WebSecurityConfigurerAdapter {

	@Autowired
	private ImplementacaoUserDetailService implementacaoUserDetailService;
	
	/*configura as solicitações de acesso por http*/
	@Override
	protected void configure(HttpSecurity http) throws Exception {
	
		//ativando proteção contra usuarios que não estão validados por token
		http.csrf().csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
		//ativando a permissão para a pagina inicial do sistema
		.disable().authorizeRequests().antMatchers("/").permitAll().antMatchers("/index").permitAll()
		.antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
		//URL de logout - redireciona após o user deslogar do sistema 
		.anyRequest().authenticated().and().logout().logoutSuccessUrl("/index")
		//Mapeia URL de logout e invalida o usuario
		.logoutRequestMatcher(new AntPathRequestMatcher("/logout"))		
		//Filtra requisições de login para autenticação
		.and().addFilterBefore(new JWTLoginFilter("/login", authenticationManager()), UsernamePasswordAuthenticationFilter.class)
		//Filtra demais requisições para verificar a presença do token JWT do HEADER HTTP
		.addFilterBefore(new JWTApiAutenticacaofilter(), UsernamePasswordAuthenticationFilter.class);
	}
	
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {		
		//service que irá consultar o usuario no banco de dados
		auth.userDetailsService(implementacaoUserDetailService)
		//padrao de decodificacao de senha
		.passwordEncoder(new BCryptPasswordEncoder());
	}
}
