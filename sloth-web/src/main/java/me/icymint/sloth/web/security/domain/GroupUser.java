package me.icymint.sloth.web.security.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "group_members", uniqueConstraints = { @UniqueConstraint(columnNames = {
		"group_id", "username" }) })
public class GroupUser {
	@Id
	@GeneratedValue
	public Long id;
	public Long group_id;
	public String username;
}
