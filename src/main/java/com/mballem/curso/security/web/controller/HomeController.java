package com.mballem.curso.security.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

	// abrir pagina home
	@GetMapping({ "/", "/home" })
	public String home() {
		return "home";
	}

	@GetMapping({"/login"})
	public String login() {
		return "login";
	}
	
	// abrir pagina home
		@GetMapping({"/login-error"})
		public String loginError(ModelMap model) {
			
			model.addAttribute("alerta","erro");
			model.addAttribute("titulo","Credenciais Invalidas!");
			model.addAttribute("texto","Login ou senha incorretos, tente novamente.");
			model.addAttribute("subtexto","Acesso permitido apenas para cadastro já ativados.");
			
			return "login";
		}
}
