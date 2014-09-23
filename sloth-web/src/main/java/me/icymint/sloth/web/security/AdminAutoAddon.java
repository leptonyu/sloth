package me.icymint.sloth.web.security;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;

public class AdminAutoAddon implements
		ApplicationListener<ContextRefreshedEvent> {

	@Override
	public void onApplicationEvent(ContextRefreshedEvent e) {
		JdbcUserDetailsManager userDetailsService = (JdbcUserDetailsManager) e
				.getApplicationContext().getBean("userDetailsService");
		PasswordEncoder passwordEncoder = (PasswordEncoder) e
				.getApplicationContext().getBean("passwordEncoder");
		if (!userDetailsService.userExists("admin")) {
			Collection<GrantedAuthority> list = new ArrayList<>();
			list.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
			list.add(new SimpleGrantedAuthority("ROLE_USER"));
			User user = new User("admin", passwordEncoder.encode(e
					.getApplicationContext().getEnvironment()
					.getProperty("spring.password.default", "admin")), list);
			userDetailsService.createUser(user);
		}
	}
}
