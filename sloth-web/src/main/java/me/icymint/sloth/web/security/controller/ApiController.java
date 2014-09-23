package me.icymint.sloth.web.security.controller;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import me.icymint.sloth.web.security.UserRepository;
import me.icymint.sloth.web.security.domain.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@Secured("ROLE_ADMIN")
public class ApiController {

	@Autowired
	private UserRepository uq;

	@RequestMapping("")
	public String api() {
		return "Version 1.0";
	}

	@RequestMapping(value = "user", method = RequestMethod.GET)
	public List<User> user() {
		return StreamSupport.stream(uq.findAll().spliterator(), false).collect(
				Collectors.toList());
	}

	@RequestMapping(value = "user/{name}", method = RequestMethod.GET)
	public User user(@PathVariable("name") String name) {
		return uq.findOne(name);
	}
}
