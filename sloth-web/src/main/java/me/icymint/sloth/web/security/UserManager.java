package me.icymint.sloth.web.security;

import java.lang.reflect.Field;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.stereotype.Repository;

@Repository("userDetailsService")
public class UserManager extends JdbcUserDetailsManager {
	@Value("${spring.datasource.prefix}")
	private String prefix;
	@Autowired
	private DataSource dataSource;

	@PostConstruct
	@Override
	protected void initDao() {
		setEnableGroups(true);
		this.setDataSource(dataSource);
		this.setUsersByUsernameQuery("select username,password,enabled from "
				+ prefix + "users where username = ?");
		this.setAuthoritiesByUsernameQuery("select username,authority from "
				+ prefix + "authorities where username = ?");
		this.setGroupAuthoritiesByUsernameQuery("select g.id, g.group_name, ga.authority "
				+ "from "
				+ prefix
				+ "groups g, "
				+ prefix
				+ "group_members gm, "
				+ prefix
				+ "group_authorities ga "
				+ "where gm.username = ? "
				+ "and g.id = ga.group_id "
				+ "and g.id = gm.group_id");
		//
		this.setCreateUserSql("insert into " + prefix
				+ "users (username, password, enabled) values (?,?,?)");
		this.setDeleteUserSql("delete from " + prefix
				+ "users where username = ?");
		this.setUpdateUserSql("update " + prefix
				+ "users set password = ?, enabled = ? where username = ?");
		this.setCreateAuthoritySql("insert into " + prefix
				+ "authorities (username, authority) values (?,?)");
		this.setDeleteUserAuthoritiesSql("delete from " + prefix
				+ "authorities where username = ?");
		this.setUserExistsSql("select username from " + prefix
				+ "users where username = ?");
		this.setChangePasswordSql("update " + prefix
				+ "users set password = ? where username = ?");
		//
		this.setFindAllGroupsSql("select group_name from " + prefix + "groups");
		this.setFindUsersInGroupSql("select username from " + prefix
				+ "group_members gm, " + prefix + "groups g "
				+ "where gm.group_id = g.id" + " and g.group_name = ?");
		this.setInsertGroupSql("insert into " + prefix
				+ "groups (group_name) values (?)");
		this.setFindGroupIdSql("select id from " + prefix
				+ "groups where group_name = ?");
		this.setInsertGroupAuthoritySql("insert into " + prefix
				+ "group_authorities (group_id, authority) values (?,?)");
		this.setDeleteGroupSql("delete from " + prefix + "groups where id = ?");
		this.setDeleteGroupAuthoritiesSql("delete from " + prefix
				+ "group_authorities where group_id = ?");
		this.setDeleteGroupMembersSql("delete from " + prefix
				+ "group_members where group_id = ?");
		this.setRenameGroupSql("update " + prefix
				+ "groups set group_name = ? where group_name = ?");
		this.setInsertGroupMemberSql("insert into " + prefix
				+ "group_members (group_id, username) values (?,?)");
		this.setDeleteGroupMemberSql("delete from " + prefix
				+ "group_members where group_id = ? and username = ?");
		this.setGroupAuthoritiesByUsernameQuery("select g.id, g.group_name, ga.authority "
				+ "from "
				+ prefix
				+ "groups g, "
				+ prefix
				+ "group_authorities ga "
				+ "where g.group_name = ? "
				+ "and g.id = ga.group_id ");
		this.setDeleteGroupAuthoritySql("delete from " + prefix
				+ "group_authorities where group_id = ? and authority = ?");
	}

	public List<String> rolesByUsername(String username) {
		return getJdbcTemplate().queryForList(
				"select authority from " + prefix
						+ "authorities where username=?", String.class,
				username);
	}

	private void set(JdbcTokenRepositoryImpl jtr, String key, String value)
			throws Exception {
		Field privateVar = jtr.getClass().getDeclaredField(key);
		privateVar.setAccessible(true);
		privateVar.set(jtr, value);
	}

	@Bean
	public PersistentTokenRepository tokenRepository() {
		JdbcTokenRepositoryImpl jtr = new JdbcTokenRepositoryImpl();
		jtr.setDataSource(dataSource);
		jtr.setCreateTableOnStartup(false);
		try {
			set(jtr, "tokensBySeriesSql",
					"select username,series,token,last_used from " + prefix
							+ "tokens where series = ?");
			set(jtr,
					"insertTokenSql",
					"insert into "
							+ prefix
							+ "tokens (username, series, token, last_used) values(?,?,?,?)");
			set(jtr, "updateTokenSql", "update " + prefix
					+ "tokens set token = ?, last_used = ? where series = ?");
			set(jtr, "removeUserTokensSql", "delete from " + prefix
					+ "tokens where username = ?");
		} catch (Exception e) {
			throw new IllegalArgumentException(e);
		}
		return jtr;
	}
}