package curso.api.rest.model;

import java.util.Collection;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ConstraintMode;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.UniqueConstraint;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class Usuario implements UserDetails {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column(name = "login", unique = true)
	private String login;

	@Column(name = "nome")
	private String nome;
	
	@Column(name="cpf", length=11, nullable = true)
	private String cpf;
	
	@Column(name = "senha")
	private String senha;
	
	@Column(name="token")
	private String token;

	@OneToMany(mappedBy = "usuario", orphanRemoval = true, cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private List<Telefone> telefones;

	@OneToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "usuario_role", 
			   uniqueConstraints = @UniqueConstraint(columnNames = {"usuario_id","role_id"}, 
			   										 name = "unique_role_user"), 
			   joinColumns = @JoinColumn(name = "usuario_id", referencedColumnName = "id", table = "usuario", unique = false, 
			   						     foreignKey = @ForeignKey(name = "usuario_fk", value = ConstraintMode.CONSTRAINT)), 
			   inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id", table = "role", unique = false, 
			   							        foreignKey = @ForeignKey(name = "role_fk", value = ConstraintMode.CONSTRAINT)))
	private List<Role> roles; // Users roles

	public Usuario() {
	}

	public Usuario(Long id, String login, String nome, String senha, List<Telefone> telefones, List<Role> roles) {
		this.id = id;
		this.login = login;
		this.nome = nome;
		this.senha = senha;
		this.telefones = telefones;
		this.roles = roles;
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}
	
	public String getCpf() {
		return cpf;
	}

	public void setCpf(String cpf) {
		this.cpf = cpf;
	}

	public String getSenha() {
		return senha;
	}

	public void setSenha(String senha) {
		this.senha = senha;
	}
	
	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public List<Telefone> getTelefones() {
		return telefones;
	}

	public void setTelefones(List<Telefone> telefones) {
		this.telefones = telefones;
	}

	public List<Role> getRoles() {
		return roles;
	}

	public void setRoles(List<Role> roles) {
		this.roles = roles;
	}

	@JsonIgnore
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return roles;
	}

	@JsonIgnore
	@Override
	public String getPassword() {
		return this.senha;
	}

	@JsonIgnore
	@Override
	public String getUsername() {
		return this.login;
	}

	@JsonIgnore
	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@JsonIgnore
	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@JsonIgnore
	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@JsonIgnore
	@Override
	public boolean isEnabled() {
		return true;
	}

}