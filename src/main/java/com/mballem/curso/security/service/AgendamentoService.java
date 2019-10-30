package com.mballem.curso.security.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mballem.curso.security.AcessoNegadoException;
import com.mballem.curso.security.datatables.Datatables;
import com.mballem.curso.security.datatables.DatatablesColunas;
import com.mballem.curso.security.domain.Agendamento;
import com.mballem.curso.security.domain.Horario;
import com.mballem.curso.security.repository.AgendamentoRepository;
import com.mballem.curso.security.repository.projection.HistoricoPaciente;

@Service
public class AgendamentoService {

	@Autowired
	private AgendamentoRepository repository;

	@Autowired
	private Datatables dataTables;

	@Transactional(readOnly = true)
	public List<Horario> buscarHorariosNaoAgendados(Long id, LocalDate data) {
		return this.repository.findByMedicoIdAndDataNotHorarioAgendado(id, data);
	}

	@Transactional(readOnly = false)
	public void salvar(Agendamento agendamento) {
		this.repository.save(agendamento);

	}

	@Transactional(readOnly = true)
	public Map<String, Object> buscarHistoricoPorPacienteEmail(String username, HttpServletRequest request) {
		this.dataTables.setRequest(request);
		this.dataTables.setColunas(DatatablesColunas.AGENDAMENTOS);
		Page<HistoricoPaciente> page = this.repository.findHistoricoByPacienteEmail(username,
				this.dataTables.getPageable());
		return this.dataTables.getResponse(page);
	}

	@Transactional(readOnly = true)
	public Map<String, Object> buscarHistoricoPorMedicoEmail(String username, HttpServletRequest request) {
		this.dataTables.setRequest(request);
		this.dataTables.setColunas(DatatablesColunas.AGENDAMENTOS);
		Page<HistoricoPaciente> page = this.repository.findHistoricoByMedicoEmail(username,
				this.dataTables.getPageable());
		return this.dataTables.getResponse(page);
	}

	@Transactional(readOnly = true)
	public Agendamento findById(Long id) {

		return this.repository.findById(id).get();
	}

	@Transactional(readOnly = false)
	public void editar(Agendamento agendamento, String username) {
		Agendamento a = this.findByIdAndUsuario(agendamento.getId(), username);
		a.setDataConsulta(agendamento.getDataConsulta());
		a.setEspecialidade(agendamento.getEspecialidade());
		a.setHorario(agendamento.getHorario());
		a.setMedico(agendamento.getMedico());

	}

	@Transactional(readOnly = true)
	public Agendamento findByIdAndUsuario(Long id, String username) {

		return this.repository.findByIdAndUsuario(id, username)
				.orElseThrow(() -> new AcessoNegadoException("Acesso negado ao usuário: " + username));
	}

	@Transactional(readOnly = false)
	public void remover(Long id) {
		this.repository.deleteById(id);
		
	}

}
