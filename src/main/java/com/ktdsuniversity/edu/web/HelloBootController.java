package com.ktdsuniversity.edu.web;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller //스프링이 인스턴스(생성자)를 자동으로 만들어줌.(Bean Container역할)
public class HelloBootController {
	
	@GetMapping("/hello")
	public ResponseEntity<String> hello() {
		return new ResponseEntity<>("Hello Spring Boot Controller", HttpStatus.OK);
	}
	
	@GetMapping("/hello2")
	public ResponseEntity<String> hello2() {
		StringBuffer html = new StringBuffer();
		html.append("<!DOCTYPE html>");
		html.append("<html>");
		html.append("<head><title>Hello, Boot!</title></head>");
		html.append("<body>");
		html.append("<div>안녕하세요?</div>");
		html.append("<div>언니가 짝꿍이어서 넘 좋아요.♥</div>");
		html.append("</body>");
		html.append("</html>");
		return new ResponseEntity<>(html.toString(), HttpStatus.OK);
	}
	
	@GetMapping("/hello3")
	public String helloJsp() {
		return "helloboot"; //파일명과 동일하게 작성
	}
	
	@GetMapping("/hello4")
	public ModelAndView helloModelAndVoew() {
		ModelAndView view = new ModelAndView();
		view.setViewName("helloboot");
		view.addObject("myname", "Spring Boot~!");
		return view;
	}
	
	@GetMapping("/hello5")
	public String helloModel(Model model) {
		model.addAttribute("myname", "Cafe Demo");
		return "helloboot";
	}

}