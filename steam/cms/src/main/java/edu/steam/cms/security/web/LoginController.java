package edu.steam.cms.security.web;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import edu.steam.cms.account.model.User;
import edu.steam.cms.security.UserService;
import edu.steam.cms.security.exception.DuplicateUserException;

@Controller
public class LoginController {

	@Autowired
	private UserService userService;
	
	@RequestMapping("/login")
	public String login() {
		// 타임리프의 뷰.
		// 타임리프의 뷰는 기본값으로 resources/templates/<경로>.html 이다.
		// 즉 이렇게하면 resources/templates/login.html 이 불린다.
		return "login";
	}
	
	@RequestMapping("/accounts/assign")
	public String assign(User user) {
		if( user == null || StringUtils.isEmpty(user.getId())) {
			return "assign";
		} else {
			try {
				userService.createUser(user, "USER");
			} catch (DuplicateUserException e) {
				e.printStackTrace();
				return "redirect:/assign?error";
			}
		}
		
		return "redirect:/login";
	}
	
}
