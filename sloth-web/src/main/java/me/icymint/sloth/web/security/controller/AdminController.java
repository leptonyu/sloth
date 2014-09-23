package me.icymint.sloth.web.security.controller;

import java.util.Map;

import me.icymint.sloth.web.security.UserManager;
import me.icymint.sloth.web.security.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
@Secured("ROLE_ADMIN")
public class AdminController {

	@Autowired
	@Qualifier("userDetailsService")
	private UserManager userDetailsService;
	@Autowired
	UserRepository uq;

	@RequestMapping("")
	public String admin(Map<String, Object> model) {
		model.put("uds", userDetailsService);
		model.put("ur", uq);
		return "admin";
	}
}
