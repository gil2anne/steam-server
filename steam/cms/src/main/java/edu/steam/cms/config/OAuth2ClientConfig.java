package edu.steam.cms.config;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.Filter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.resource.ResourceServerProperties;
import org.springframework.boot.autoconfigure.security.oauth2.resource.UserInfoTokenServices;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.filter.OAuth2ClientAuthenticationProcessingFilter;
import org.springframework.security.oauth2.client.filter.OAuth2ClientContextFilter;
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeResourceDetails;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableOAuth2Client;
import org.springframework.web.filter.CompositeFilter;

import edu.steam.cms.security.OAuth2SuccessHandler;
import edu.steam.cms.security.UserService;
import edu.steam.cms.security.UserTokenServices;

@Configuration
@EnableOAuth2Client
public class OAuth2ClientConfig{
	
	@Autowired
	OAuth2ClientContext oauth2ClientContext;
	
	// 앞서 전장에서 만들었던 계정 서비스입니다.
	@Autowired
	UserService accountService;
	
	@Bean
	@ConfigurationProperties("facebook.client")
	AuthorizationCodeResourceDetails facebook() {
		return new AuthorizationCodeResourceDetails();
	}
 
	@Bean
	@ConfigurationProperties("facebook.resource")
	ResourceServerProperties facebookResource() {
		return new ResourceServerProperties();
	}
	
	@Bean
	@ConfigurationProperties("naver.client")
	AuthorizationCodeResourceDetails naver() {
		return new AuthorizationCodeResourceDetails();
	}
	
	@Bean
	@ConfigurationProperties("naver.resource") 	
	ResourceServerProperties naverResource() {
		return new ResourceServerProperties();
	}
	
	@Bean
	@ConfigurationProperties("google.client")
	AuthorizationCodeResourceDetails google() {
		return new AuthorizationCodeResourceDetails();
	}
	@Bean
	@ConfigurationProperties("google.resource") 	
	ResourceServerProperties googleResource() {
		return new ResourceServerProperties();
	}
	
	@Bean
	FilterRegistrationBean oauth2ClientFilterRegistration(OAuth2ClientContextFilter filter) {
		FilterRegistrationBean registration = new FilterRegistrationBean();
		registration.setFilter(filter);
		// 스프링 사이트에 의하면 다른 필터보다 우선순위를 올리기위해 -100을 주었다고 나옵니다.
		registration.setOrder(-100);
		return registration;
	}
	
	@Bean("sso.filter")
	Filter ssoFilter() {
		List<Filter> filters = new ArrayList<>();
		
		// 페이스북
		OAuth2ClientAuthenticationProcessingFilter facebook
			= new OAuth2ClientAuthenticationProcessingFilter("/login/facebook");
		facebook.setRestTemplate(new OAuth2RestTemplate(facebook(), oauth2ClientContext));
		facebook.setTokenServices(new UserTokenServices(facebookResource().getUserInfoUri(), facebook().getClientId()));
		facebook.setAuthenticationSuccessHandler(new OAuth2SuccessHandler("facebook", accountService));
		filters.add(facebook);
		
		// 네이버
		OAuth2ClientAuthenticationProcessingFilter naver
			= new OAuth2ClientAuthenticationProcessingFilter("/login/naver");
		naver.setRestTemplate(new OAuth2RestTemplate(naver(), oauth2ClientContext));
		naver.setTokenServices(new UserTokenServices(naverResource().getUserInfoUri(), naver().getClientId()));
		naver.setAuthenticationSuccessHandler(new OAuth2SuccessHandler("naver", accountService));
		filters.add(naver);
		
		//google
		OAuth2ClientAuthenticationProcessingFilter google
			= new OAuth2ClientAuthenticationProcessingFilter("/login/google");
		google.setRestTemplate(new OAuth2RestTemplate(google(), oauth2ClientContext));
		google.setTokenServices(new UserTokenServices(googleResource().getUserInfoUri(), google().getClientId()));
		google.setAuthenticationSuccessHandler(new OAuth2SuccessHandler("google", accountService));
		filters.add(google);
 
		CompositeFilter filter = new CompositeFilter();
		filter.setFilters(filters);
		return filter;
	}
}