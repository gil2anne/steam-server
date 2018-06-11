package edu.steam.cms.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import edu.steam.cms.account.mapper.UserMapper;
import edu.steam.cms.account.model.User;
import edu.steam.cms.account.model.UserRole;
import edu.steam.cms.security.exception.DuplicateUserException;

@Service
public class UserService implements UserDetailsService {
	
	@Autowired
	UserMapper userDao;
	
	public boolean createUser(User user, String role) throws DuplicateUserException {
		if( userDao.getUserByEmail(user.getEmail()) != null ) {
			throw new DuplicateUserException(user.getEmail());
		}
		user.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));
	
		userDao.insertUserInfo(user);
		userDao.insertUserRole(user.getId(), role);
		return true;
	}
	
	// 필자의 경우 기존 해시 시스템이 좀 다르기 때문에
	// 이부분을 적용한다면 암호가 구형시스템이라면 바꿔주는 부분이 필요.	
	@Override
	public UserDetails loadUserByUsername(String id) throws UsernameNotFoundException {
		// 기존해시와 신규해시가 다를경우 이런식으로 받아 처리할 수 있음.
		// 위 @Autowired HttpServletRequest request;
		// request 처리
		
		User user = userDao.getUserInfo(id);
		
		if (user == null) {
			// 계정이 존재하지 않음
			throw new UsernameNotFoundException("login fail");
		}
		List<GrantedAuthority> authorities = new ArrayList<>();
		
		//authorities.add(new SimpleGrantedAuthority("USER"));
		
		List<UserRole> userRoleList = userDao.getRoleList(id);
		
		if (userRoleList != null) {
			userRoleList.stream().forEach((UserRole role) -> {
				authorities.add(new SimpleGrantedAuthority(role.getUserRole()));
			});
		}
				
		user.setAuthorities(authorities);
		return user;
	}
	
	public User getUserByOAuth(String provider, String oAuthId) {
		return userDao.getUserByOAuth(provider, oAuthId);
	}
	
}
