package edu.steam.cms.config;

import javax.servlet.Filter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import edu.steam.cms.security.UserService;

@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	private ApplicationContext context;
	
	@Autowired
	private UserService userService;
	
	@Override
	public void configure(WebSecurity web) throws Exception {
		web.ignoring().antMatchers("/*", "/static/**");
	}

	@Override
	protected void configure(HttpSecurity httpSecurity) throws Exception {
		httpSecurity
	        .authorizeRequests()
	            .antMatchers("/*", "/login", "/login/*", "/accounts/*", "/contents/**", "/guide/**").permitAll()
	            //.antMatchers("/**").hasRole("USER")
	            .anyRequest().permitAll()
	        .and()
	        	.formLogin()
	            .loginProcessingUrl("/loginProcessing")
	        	.loginPage("/login")
	            .failureUrl("/login?error")
	            .defaultSuccessUrl("/")
	            .permitAll()
	        .and()
			// 여기 나오는 sso.filter 빈은 다음장에서 작성합니다.
			// 이 장에서 실행을 확인하시려면 당연히 NPE 오류가 나니 아래 소스에 주석을 걸어주시기 바랍니다.
			.addFilterBefore((Filter)context.getBean("sso.filter"), BasicAuthenticationFilter.class)
	        .csrf().disable()
	        .headers().frameOptions().disable();
		//httpSecurity.exceptionHandling().accessDeniedPage("/index");
		//httpSecurity.sessionManagement().invalidSessionUrl("/login");
	}
	
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}


	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth
        .userDetailsService(userService)
        .passwordEncoder(passwordEncoder());
	}
	
	

}
