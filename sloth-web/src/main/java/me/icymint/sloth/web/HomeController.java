package me.icymint.sloth.web;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class HomeController {

	@ResponseBody
	@RequestMapping("/api")
	@Secured("ROLE_ADMIN")
	public List<String> api() {
		ArrayList<String> list = new ArrayList<>();
		list.add("Version 1.0");
		return list;
	}

	@RequestMapping("/")
	public String home(Map<String, Object> model) {
		model.put("message", "Hello World");
		Authentication au = SecurityContextHolder.getContext()
				.getAuthentication();
		if (au != null)
			model.put("user", au.getName());
		model.put("date", new Date());
		return "home";
	}
}
