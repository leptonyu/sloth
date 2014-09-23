package me.icymint.sloth.web.security.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "users")
public class User implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7946728088202119788L;

	@Id
	@Column(length = 32)
	public String username;

	public String password;

	@Column(columnDefinition = "integer")
	public boolean enabled;
}
