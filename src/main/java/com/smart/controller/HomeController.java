package com.smart.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.smart.dao.UserRepository;
import com.smart.entities.User;
import com.smart.helper.Message;

import jakarta.validation.Valid;


@Controller
public class HomeController {

	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	
	@Autowired
	private UserRepository userRepository;
	
	@GetMapping("/home")
	public String home(Model model) {
		return "home";
	}
	
	@GetMapping("/about")
	public String about(Model model) {
		return "about";
	}
	
	@GetMapping("/signin")
	public String login(Model model) {
		return "signin";
	}

	
	@GetMapping("/signup")
	public String signup(Model model) {
		model.addAttribute("user", new User());
		return "signup";
	}
	
	
	@PostMapping("/do_register")
	public String register(@Valid @ModelAttribute User user,BindingResult result,Model model ) {

		try {
			
			if(result.hasErrors()){
//				System.out.println("ERROR" + result.toString());
				model.addAttribute("user", user);
				return "signup";
			}
			user.setRole("ROLE_USER");
			user.setEnabled(true);
			user.setPassword(passwordEncoder.encode(user.getPassword()));
			User u=this.userRepository.save(user);
			
			model.addAttribute("user", u);
			System.out.println(u);
			model.addAttribute("message", new Message("alert-success", "Registration Successfull !!!!"));
			return "redirect:/user/MyProfile";
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			model.addAttribute("user", user);
			model.addAttribute("message", new Message("alert-danger", "Registration Failed !!!! Maybe email already exists"));
			return "signup";
		}
		
		
	}
	
}
