package com.mballem.curso.security.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.mballem.curso.security.domain.PerfilTipo;
import com.mballem.curso.security.service.UsuarioService;

@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
	
	private static final String ADMIN = PerfilTipo.ADMIN.getDesc();
	private static final String MEDICO = PerfilTipo.MEDICO.getDesc();
	private static final String PACIENTE = PerfilTipo.PACIENTE.getDesc();
	
	@Autowired
	private UsuarioService usuarioService;

	
	
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {

		http.authorizeRequests()
				//ACESSO PUBLICO LIBERADOS
				.antMatchers("/webjars/**", "/css/**", "/image/**", "/js/**").permitAll()
				.antMatchers("/", "/home").permitAll()
				//ACESSO PRIVADO A ADMIN
				.antMatchers("/u/editar/senha", "/u/confirmar/senha").hasAnyAuthority(PACIENTE,MEDICO)
				.antMatchers("/u/**").hasAuthority(ADMIN)
				//ACESSO PRIVADO A MEDICO
				.antMatchers("/medicos/dados", "/medicos/salvar", "/medicos/editar").hasAnyAuthority(MEDICO,ADMIN)
				.antMatchers("/medicos/**").hasAuthority(MEDICO)
				//ACESSO PRIVADO A PACIENTE
				.antMatchers("/pacientes/**").hasAuthority(PACIENTE)
				//ACESSO PRIVADO A ESPECIALIDADES
				.antMatchers("/especialidades/datatables/server/medico/*").hasAnyAuthority(ADMIN,MEDICO)
				.antMatchers("/especialidades/titulo").hasAnyAuthority(ADMIN,MEDICO)
				.antMatchers("/especialidades/**").hasAuthority(ADMIN)
				.anyRequest().authenticated()
				.and()
				.formLogin()
				.loginPage("/login")
				.defaultSuccessUrl("/", true)
				.failureForwardUrl("/login-error")
				.permitAll()
				.and()
				.logout()
				.logoutSuccessUrl("/")
				//TRATANDO MENSAGEM DA PAGINA 403
				.and()
				.exceptionHandling()
				.accessDeniedPage("/acesso-negado");
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(this.usuarioService).passwordEncoder(new  BCryptPasswordEncoder());
	}
	
	

}
