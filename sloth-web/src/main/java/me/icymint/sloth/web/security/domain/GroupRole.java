package me.icymint.sloth.web.security.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "group_authorities", uniqueConstraints = { @UniqueConstraint(columnNames = {
		"group_id", "authority" }) })
public class GroupRole {
	@Id
	@GeneratedValue
	public Long id;

	public Long group_id;
	public String authority;
}
