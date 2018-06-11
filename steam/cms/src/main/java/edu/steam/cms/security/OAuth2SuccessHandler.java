package edu.steam.cms.security;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import edu.steam.cms.account.model.User;
import edu.steam.cms.security.exception.DuplicateUserException;

public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {
	final String provider;
	final UserService accountService;
	
	List<GrantedAuthority> authorities = new ArrayList<>(1);
	
	public OAuth2SuccessHandler(String provider, UserService accountService){
		this.provider = provider;
		this.accountService = accountService;
		this.authorities.add(new SimpleGrantedAuthority("OAUTH"));
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void onAuthenticationSuccess(HttpServletRequest req, HttpServletResponse res, Authentication auth)
			throws IOException, ServletException {
				
		Authentication token = ((OAuth2Authentication)auth).getUserAuthentication();
		Map<String, Object> details = (Map<String, Object>)token.getDetails();
		// 연결되어 있는 계정이 있는지 확인합니다.
		// 이전 강의 AccountService.getAccountByOAuthId 참고!!
		User account = accountService.getUserByOAuth(provider, (String)details.get("sub"));
		
		// 연결되어 있는 계정이 있는경우.
		if (account != null) {
			// 기존 인증을 바꿉니다.
			// 이전 강의의 일반 로그인과 동일한 방식으로 로그인한 것으로 간주하여 처리합니다.
			// 기존 인증이 날아가기 때문에 OAUTH ROLE은 증발하며, USER ROLE 이 적용됩니다.
			SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(
				account, null, authorities));
			
			res.sendRedirect("/");
		}
		// 연결된 계정이 없는경우
		else
		{
			// 회원가입 페이지로 보냅니다.
			// ROLE 은 OAUTH 상태입니다.
			
			
			// 특별히 추가정보를 받아서 가입해야할 일이없다면,
			// 즉석으로 계정을 생성한 후 성공처리 해준다면 사용자 입장에서 좋을 것 같습니다.
			account = new User();
			account.setId((String)details.get("sub"));
			account.setName((String)details.get("name"));
			account.setEmail((String)details.get("email"));
			
			try {
				accountService.createUser(account, "OAUTH");
			} catch (DuplicateUserException e) {
				
			}
			res.sendRedirect("/");
		}
	}
}