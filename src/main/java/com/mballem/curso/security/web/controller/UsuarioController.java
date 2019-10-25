package com.mballem.curso.security.web.controller;

import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.mballem.curso.security.domain.Medico;
import com.mballem.curso.security.domain.Perfil;
import com.mballem.curso.security.domain.PerfilTipo;
import com.mballem.curso.security.domain.Usuario;
import com.mballem.curso.security.service.MedicoService;
import com.mballem.curso.security.service.UsuarioService;

@Controller
@RequestMapping("u")
public class UsuarioController {
	
	@Autowired
	private UsuarioService service;
	
	@Autowired
	private MedicoService medicoService;
	
	@GetMapping("/novo/cadastro/usuario")
	public String cadastroPorAdminMedicoPaciente(Usuario usuario) {
		
		return "usuario/cadastro";
	}
	
	
	@GetMapping("/lista")
	public String listarUsuarios() {
		
		return "usuario/lista";
	}
	
	
	@GetMapping("/datatables/server/usuarios")
	public ResponseEntity<?> listarUsuariosDatatables(HttpServletRequest request) {
		
		return ResponseEntity.ok(this.service.buscarTodos(request));
	}
	
	@PostMapping("/cadastro/salvar")
	public String salvarUsuarios(Usuario usuario, RedirectAttributes attr) {
		List<Perfil> perfis = usuario.getPerfis();
		if(perfis.size() > 2 ||
				perfis.containsAll(Arrays.asList(new Perfil(1L),new Perfil(3L))) || 
				perfis.containsAll(Arrays.asList(new Perfil(2L),new Perfil(3L)))) {
			
			attr.addFlashAttribute("falha","Paciente não pode ser admin ou médico!");
			attr.addFlashAttribute("usuario",usuario);
		}else {
			try {
				this.service.save(usuario);
				attr.addFlashAttribute("sucesso","Operação realizado com sucesso!");
				
			} catch (DataIntegrityViolationException e) {
				attr.addFlashAttribute("falha","Cadstro não realizado, Email já existente!");
			}
		}
		return "redirect:/u/novo/cadastro/usuario";
	}
	
	@GetMapping("/editar/credenciais/usuario/{id}")
	public ModelAndView preEditarCredenciais(@PathVariable("id") Long id) {
		return new ModelAndView("usuario/cadastro", "usuario", this.service.findById(id));
	}
	
	@GetMapping("/editar/dados/usuario/{id}/perfis/{perfis}")
	public ModelAndView preEditarCadastroDadosPessoais(@PathVariable("id") Long usuarioId,@PathVariable("perfis") Long[] perfisId) {
		
		Usuario us = this.service.findByIdAndPerfis(usuarioId,perfisId);
		
		if(us.getPerfis().contains(new Perfil(PerfilTipo.ADMIN.getCod())) &&
				! us.getPerfis().contains(new Perfil(PerfilTipo.MEDICO.getCod()))) {
			
			return new ModelAndView("usuario/cadastro", "usuario", us);
			
		}else if (us.getPerfis().contains(new Perfil(PerfilTipo.MEDICO.getCod()))) {
			Medico  medico = this.medicoService.findByUsuarioId(usuarioId); 
			
			return medico.hasNotId() 
					? new ModelAndView("medico/cadastro", "medico",new Medico(new Usuario(usuarioId)))
					: new ModelAndView("medico/cadastro", "medico",medico);
			
		}else if (us.getPerfis().contains(new Perfil(PerfilTipo.PACIENTE.getCod()))) {
			ModelAndView model = new ModelAndView("error");
			model.addObject("status", 403);
			model.addObject("error", "Área Restrita!");
			model.addObject("message", "Os dados do paciente são restritos a ele.");
			return model;
		}
		
		return new ModelAndView("redirect:/u/lista");
	}
	

}
