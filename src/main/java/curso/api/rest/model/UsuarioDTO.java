package curso.api.rest.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UsuarioDTO implements Serializable{

	private static final long serialVersionUID = 1L;
	@JsonProperty("Login")
	private String userLogin;
	@JsonProperty("Nome")
	private String userNome;
	@JsonProperty("CPF")
	private String userCpf;
	
	public UsuarioDTO(Usuario usuario) {
		userLogin = usuario.getLogin();
		userNome = usuario.getNome();
		userCpf = usuario.getCpf();
	}
	
	public String getUserLogin() {
		return userLogin;
	}
	public void setUserLogin(String userLogin) {
		this.userLogin = userLogin;
	}
	public String getUserNome() {
		return userNome;
	}
	public void setUserNome(String userNome) {
		this.userNome = userNome;
	}
	public String getUserCpf() {
		return userCpf;
	}
	public void setUserCpf(String userCpf) {
		this.userCpf = userCpf;
	}
	

}
