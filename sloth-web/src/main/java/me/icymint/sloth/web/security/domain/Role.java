package me.icymint.sloth.web.security.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "authorities", uniqueConstraints = { @UniqueConstraint(columnNames = {
		"username", "authority" }) })
public class Role {
	@Id
	@GeneratedValue
	public Long id;
	public String username;
	public String authority;
}
