package com.mballem.curso.security.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mballem.curso.security.domain.Medico;
import com.mballem.curso.security.repository.MedicoRepository;

@Service
public class MedicoService {
	
	@Autowired
	private MedicoRepository repository;
	
	
	@Transactional(readOnly = true)
	public Medico findByUsuarioId(Long id) {
		return this.repository.findByUsuarioId(id).orElse(new Medico());
	}


	@Transactional(readOnly = false)
	public void save(Medico medico) {
		this.repository.save(medico);
		
	}

	@Transactional(readOnly = false)
	public void update(Medico medico) {
		Medico m2 = this.repository.findById(medico.getId()).get();
		m2.setCrm(medico.getCrm());
		m2.setNome(medico.getNome());
		m2.setDtInscricao(medico.getDtInscricao());
		
		if(!medico.getEspecialidades().isEmpty()) {
			m2.getEspecialidades().addAll(medico.getEspecialidades());
		}
	}

	@Transactional(readOnly = true)
	public Medico findByEmail(String username) {
		
		return this.repository.findByUsuarioEmail(username).get();
	}

	@Transactional(readOnly = false)
	public void excluirEspecialidadePorMedico(Long idMed, Long idEsp) {
		Medico medico = this.repository.findById(idMed).get();
		medico.getEspecialidades().removeIf(e -> e.getId().equals(idEsp));
		
	}


	@Transactional(readOnly = true)
	public List<Medico> buscarMedicoPorEspecialidades(String titulo) {
		
		return this.repository.findByMedicosPorEspecialidades(titulo);
	}

}
