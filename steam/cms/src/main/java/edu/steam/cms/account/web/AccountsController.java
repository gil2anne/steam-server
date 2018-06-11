package edu.steam.cms.account.web;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import edu.steam.cms.account.model.User;
import edu.steam.cms.security.UserService;
import edu.steam.cms.security.exception.DuplicateUserException;

@Controller
public class AccountsController {
	
	@Autowired
	private UserService userService;
	
	
	@RequestMapping("/accounts/sign-up")
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
