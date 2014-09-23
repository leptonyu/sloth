package me.icymint.sloth.web.security.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "groups")
public class Group implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6621059031515670230L;
	@Id
	@GeneratedValue
	public Long id;
	@Column(unique = true)
	public String group_name;
}
