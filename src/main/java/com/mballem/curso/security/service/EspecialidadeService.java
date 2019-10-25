package com.mballem.curso.security.service;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mballem.curso.security.datatables.Datatables;
import com.mballem.curso.security.datatables.DatatablesColunas;
import com.mballem.curso.security.domain.Especialidade;
import com.mballem.curso.security.repository.EspecialidadeRepository;

@Service
public class EspecialidadeService {
	
	@Autowired
	private EspecialidadeRepository repository;
	
	@Autowired
	private Datatables dataTables;
	
	@Transactional(readOnly = false)
	public void salvar(Especialidade especialidade) {
		
		this.repository.save(especialidade);
	}

	@Transactional(readOnly = true)
	public Map<String, Object> buscarEspecialidades(HttpServletRequest request) {
		this.dataTables.setRequest(request);
		this.dataTables.setColunas(DatatablesColunas.ESPECIALIDADES);
		Page<?> page = this.dataTables.getSearch().isEmpty() 
				? this.repository.findAll(this.dataTables.getPageable())
				: this.repository.findAllByTitulo(this.dataTables.getSearch(), this.dataTables.getPageable());
		return this.dataTables.getResponse(page);
	}

	@Transactional(readOnly = true)
	public Especialidade findById(Long id) {
		
		return this.repository.findById(id).get();
	}

	@Transactional(readOnly = false)
	public void deleteById(Long id) {
		this.repository.deleteById(id);
	}

}
