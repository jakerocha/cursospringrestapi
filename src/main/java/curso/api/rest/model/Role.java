package curso.api.rest.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.springframework.security.core.GrantedAuthority;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "role")
@SequenceGenerator(name = "seq_role", sequenceName = "seq_role", allocationSize = 1, initialValue = 1)
@Getter
@Setter
@ToString
@AllArgsConstructor
@EqualsAndHashCode

public class Role implements GrantedAuthority {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_role")
	private Long id;

	@NotNull
	@NotBlank
	@Column(name = "nome_role")
	private String nomeRole; // EX: ROLE_SECRETARIA, ROLE_GERENTE, ROLE_USUARIO, ROLE_ADMIN

	@JsonIgnore
	@Override
	public String getAuthority() {
		return this.nomeRole;
	}

	public Role() {
	}

}
