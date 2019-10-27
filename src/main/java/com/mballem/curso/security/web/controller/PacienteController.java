package com.mballem.curso.security.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.mballem.curso.security.domain.Paciente;
import com.mballem.curso.security.domain.Usuario;
import com.mballem.curso.security.service.PacienteService;
import com.mballem.curso.security.service.UsuarioService;

@Controller
@RequestMapping("/pacientes")
public class PacienteController {

	@Autowired
	private PacienteService service;
	
	@Autowired
	private UsuarioService usuarioService;

	@GetMapping("/dados")
	public String cadastroDePaciente(Paciente paciente, ModelMap model, @AuthenticationPrincipal User user) {
		paciente = this.service.buscarporEmail(user.getUsername());
		if (paciente.hasNotId()) {
			paciente.setUsuario(new Usuario(user.getUsername()));
		}
		model.addAttribute("paciente", paciente);
		return "paciente/cadastro";
	}
	
	@SuppressWarnings("static-access")
	@PostMapping("/salvar")
	public String salvar(Paciente paciente, ModelMap model, @AuthenticationPrincipal User user) {
		Usuario u = this.usuarioService.findByEmail(user.getUsername());
		if(this.usuarioService.isSenhaCorreta(paciente.getUsuario().getSenha(), u.getSenha())) {
			paciente.setUsuario(u);
			this.service.salvar(paciente);
			model.addAttribute("sucesso", "Seus dados foram inseridos com sucesso.");
		}else {
			model.addAttribute("falha", "Sua senha não confere, tente novamente.");
		}
		return "paciente/cadastro";
	}
	
	
	@SuppressWarnings("static-access")
	@PostMapping("/editar")
	public String editar(Paciente paciente, ModelMap model, @AuthenticationPrincipal User user) {
		Usuario u = this.usuarioService.findByEmail(user.getUsername());
		if(this.usuarioService.isSenhaCorreta(paciente.getUsuario().getSenha(), u.getSenha())) {
			this.service.update(paciente);
			model.addAttribute("sucesso", "Seus dados foram atualizados com sucesso.");
		}else {
			model.addAttribute("falha", "Sua senha não confere, tente novamente.");
		}
		return "paciente/cadastro";
	}

}
