package com.mballem.curso.security.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Base64Utils;

import com.mballem.curso.security.AcessoNegadoException;
import com.mballem.curso.security.datatables.Datatables;
import com.mballem.curso.security.datatables.DatatablesColunas;
import com.mballem.curso.security.domain.Perfil;
import com.mballem.curso.security.domain.PerfilTipo;
import com.mballem.curso.security.domain.Usuario;
import com.mballem.curso.security.repository.UsuarioRepository;

@Service
public class UsuarioService implements UserDetailsService {

	@Autowired
	private UsuarioRepository repository;

	@Autowired
	private Datatables dataTables;
	
	@Autowired
	private EmailService emailService;

	@Transactional(readOnly = true)
	public Usuario findByEmail(String email) {

		return this.repository.findByEmail(email);
	}

	@Transactional(readOnly = true)
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Usuario usuario = this.buscarPorEmailAtivo(username).orElseThrow(() -> new UsernameNotFoundException("Usuario "+username+" Não Encontrado!"));
		return new User(usuario.getEmail(), usuario.getSenha(),
				AuthorityUtils.createAuthorityList(this.getAuthorities(usuario.getPerfis())));
	}

	private String[] getAuthorities(List<Perfil> perfis) {
		String[] authorities = new String[perfis.size()];

		for (int i = 0; i < perfis.size(); i++) {
			authorities[i] = perfis.get(i).getDesc();
		}
		return authorities;
	}

	@Transactional(readOnly = true)
	public Map<String, Object> buscarTodos(HttpServletRequest request) {
		this.dataTables.setRequest(request);
		this.dataTables.setColunas(DatatablesColunas.USUARIOS);
		Page<Usuario> page = this.dataTables.getSearch().isEmpty()
				? this.repository.findAll(this.dataTables.getPageable())
				: this.repository.findByEmailOrPerfil(this.dataTables.getSearch(), this.dataTables.getPageable());
		return this.dataTables.getResponse(page);
	}

	@Transactional(readOnly = false)
	public void save(Usuario usuario) {
		String decrypter = new BCryptPasswordEncoder().encode(usuario.getSenha());
		usuario.setSenha(decrypter);
		this.repository.save(usuario);

	}

	@Transactional(readOnly = true)
	public Usuario findById(Long id) {
		return this.repository.findById(id).get();
	}

	@Transactional(readOnly = true)
	public Usuario findByIdAndPerfis(Long usuarioId, Long[] perfisId) {

		return this.repository.findByIdAndPerfis(usuarioId, perfisId)
				.orElseThrow(() -> new UsernameNotFoundException("Usuario Inexistente!"));
	}

	public static boolean isSenhaCorreta(String senhaDigitada, String senhaArmazenada) {

		return new BCryptPasswordEncoder().matches(senhaDigitada, senhaArmazenada);
	}

	@Transactional(readOnly = false)
	public void alterarSenha(Usuario usuario, String senha) {
		usuario.setSenha(new BCryptPasswordEncoder().encode(senha));
		this.repository.save(usuario);
	}

	@Transactional(readOnly = false)
	public void salvarCadastroPaciente(Usuario usuario) throws Exception {

		String crypt = new BCryptPasswordEncoder().encode(usuario.getSenha());
		usuario.setSenha(crypt);
		usuario.addPerfil(PerfilTipo.PACIENTE);
		this.repository.save(usuario);
		this.emailDeConfirmacaoDoCadastro(usuario.getEmail());
		

	}
	
	@Transactional(readOnly = true)
	public Optional<Usuario> buscarPorEmailAtivo(String email){
		
		return this.repository.findByEmailAtivo(email);
	}
	
	public void emailDeConfirmacaoDoCadastro(String email) throws Exception {
		
		String codigo  = Base64Utils.encodeToString(email.getBytes());
		this.emailService.enviarPedidoDeComfirmacaoDeCadastro(email, codigo);
		
	}
	
	@Transactional(readOnly = false)
	public void ativarCadastroPacinte(String codigo) {
		String email = new String(Base64Utils.decodeFromString(codigo));
		Usuario usuario = this.findByEmail(email);
		if(usuario.hasNotId()) {
			throw new AcessoNegadoException("Não foi possivel ativar seu cadastro, Entre em contato com suporte.");
		}
		usuario.setAtivo(true);
	}
	
	@Transactional(readOnly = false)
	public void pedidoRedefinicaoDeSenha(String email) throws Exception {
		Usuario usuario = this.buscarPorEmailAtivo(email)
				.orElseThrow(() -> new UsernameNotFoundException("Usuario " + email + " não encontrado."));;
		
		String verificador = RandomStringUtils.randomAlphanumeric(6);
		
		usuario.setCodigoVerificador(verificador);
		
		this.emailService.enviarPedidoRedefinicaoSenha(email, verificador);
	}

}
