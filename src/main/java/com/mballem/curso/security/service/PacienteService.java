package com.mballem.curso.security.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mballem.curso.security.domain.Paciente;
import com.mballem.curso.security.repository.PacienteRepository;

@Service
public class PacienteService {

	@Autowired
	private PacienteRepository repository;
	
	@Transactional(readOnly = true)
	public Paciente buscarporEmail(String email) {
		return this.repository.findByEmail(email).orElse(new Paciente());
	}

	@Transactional(readOnly = false)
	public void salvar(Paciente paciente) {
		this.repository.save(paciente);
		
	}

	@Transactional(readOnly = false)
	public void update(Paciente paciente) {
		Paciente p = this.repository.findById(paciente.getId()).get();
		p.setNome(paciente.getNome());
		p.setDtNascimento(paciente.getDtNascimento());
	}
	
}
