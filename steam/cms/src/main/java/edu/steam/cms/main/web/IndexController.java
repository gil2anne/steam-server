package edu.steam.cms.main.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class IndexController {
	
	@RequestMapping(value="/", method=RequestMethod.GET)
	public String index(HttpServletRequest request, HttpServletResponse response) {
		
		response.setHeader("Pragma", "no-cache");
		response.setDateHeader("Expires", 0);
		
		if( "HTTP/1.1".equals(request.getProtocol())) {
			response.setHeader("Cache-Control", "no-cache");
		} else {
			response.setHeader("Cache-Control", "no-store");
		}
		return "forward:/index.html";
	}
}
