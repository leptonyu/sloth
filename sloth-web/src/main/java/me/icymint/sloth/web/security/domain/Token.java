package me.icymint.sloth.web.security.domain;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "tokens")
public class Token {
	@Column(length = 64, nullable = false)
	public String username;
	@Id
	@Column(length = 64, nullable = false)
	public String series;
	@Column(length = 64, nullable = false)
	public String token;
	public Timestamp last_used;
}
