package com.smart.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.smart.dao.ContactRepository;
import com.smart.dao.UserRepository;
import com.smart.entities.Contact;
import com.smart.entities.User;
import com.smart.helper.Message;

import jakarta.validation.Valid;

import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

@Controller
@RequestMapping("/user")
public class UserController {

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private ContactRepository contactRepository;
	
//	to add common data
	@ModelAttribute
	public void addCommanData( Principal principal, Model model) {
		User user= userRepository.getUserByEmail(principal.getName());
		model.addAttribute("user", user);
	}

//	to get contacts of user
	@GetMapping("/Mycontacts")
	public String Mycontacts(Model model, Principal principal) {	
		
		User user = this.userRepository.getUserByEmail(principal.getName());
		List<Contact> contacts= user.getContacts();
		model.addAttribute("contacts", contacts);
		return "user/MyContacts";
	}
	
//	to delete contact
	@GetMapping("/delete/{cid}")
	public String deleteContact(@PathVariable("cid") Integer cId,Model model,Principal principal) {
		
	Optional<Contact> contactOptional=this.contactRepository.findById(cId);
	Contact contact=contactOptional.get();
	contactRepository.delete(contact);
	
	User user = this.userRepository.getUserByEmail(principal.getName());
	List<Contact> contacts= user.getContacts();
	model.addAttribute("contacts", contacts);
	model.addAttribute("message", new Message("alert-success", "Contact Deleted Successfully"));
	
		return"user/MyContacts";
	}
	
//	to add contact
	@GetMapping("/AddContact")
	public String AddContact(Model model) {
		model.addAttribute("contact", new Contact());
		return "user/AddContact";
	}
	@PostMapping("/add_contact")
	public String addContact(@Valid @ModelAttribute Contact contact,BindingResult result ,Model model, Principal principal,@RequestParam ("image") MultipartFile file) {
		try {
			
			if(result.hasErrors()){
				System.out.println("ERROR" + result.toString());
				model.addAttribute("contact", contact);
				return "user/AddContact";
			}
			
			User user=userRepository.getUserByEmail(principal.getName());
			
			if (file.isEmpty()) {
//				if file is empty
				contact.setImageUrl("logocontact.jpeg");
			}else {
				String fileName = (String)file.getOriginalFilename();
				contact.setImageUrl(fileName);
				File saveFile=new ClassPathResource("static/image").getFile();
			
				Path path=Paths.get(saveFile.getAbsolutePath()+File.separator+contact.getcId()+file.getOriginalFilename());
				
				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
			}
			
			
			contact.setUser(user);
		    this.contactRepository.save(contact);

			model.addAttribute("contact", new Contact());
			model.addAttribute("message", new Message("alert-success", "Contact Added Successfully!!!"));
			return "user/AddContact";
		} catch (Exception e) {
			// TODO: handle exception

			model.addAttribute("contact", contact);
			model.addAttribute("message", new Message("alert-danger", "Failed!!!"));
			return "user/AddContact";
		}
		
	}
	
//	to show details of contact
	@GetMapping("/contacts{cId}")
	public String  contactDetail(@PathVariable("cId") Integer cid,Model model) {
		
		Optional<Contact> optional=this.contactRepository.findById(cid);
		Contact contact=optional.get();
		model.addAttribute("contact", contact);
		return "user/contactDetails";
		
	}
	
//	to update contact
	@PostMapping("/update/{cId}")
	public String  update(@PathVariable("cId") Integer cid,Model model) {
		
		Optional<Contact> optional=this.contactRepository.findById(cid);
		Contact contact=optional.get();
		model.addAttribute("contact", contact);
		return "user/updateContact";
	}
	
	@PostMapping("update_process")
	public String updateProcess(@ModelAttribute Contact contact,Model model, Principal principal,@RequestParam ("image") MultipartFile file){
       try {
			
			
			User user=userRepository.getUserByEmail(principal.getName());
			Contact oldContact=this.contactRepository.findById(contact.getcId()).get();
			if (!file.isEmpty()) {
				
//				delete old photo
				File deleteFile=new ClassPathResource("static/image").getFile();
				File filr1=new File(deleteFile, oldContact.getImageUrl());
				filr1.delete();
				
//				upload new photo
		
				contact.setImageUrl(file.getOriginalFilename());
				File saveFile=new ClassPathResource("static/image").getFile();
			
				Path path=Paths.get(saveFile.getAbsolutePath()+File.separator+contact.getcId()+file.getOriginalFilename());
				
				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
			
				
			}else {
				contact.setImageUrl(oldContact.getImageUrl());
				}
			
			
			contact.setUser(user);
		    this.contactRepository.save(contact);

			return "redirect:/user/contacts"+contact.getcId();
		} catch (Exception e) {
			// TODO: handle exception

			model.addAttribute("contact", contact);
			model.addAttribute("message", new Message("alert-danger", "Failed!!!"));
			return "redirect:/user/contacts"+contact.getcId();
		}
		
	}
	

//	to show user profile
	@GetMapping("/MyProfile")
	public String MyProfile(Principal principal,Model model) {		
		
		User user=this.userRepository.getUserByEmail(principal.getName());
		model.addAttribute("user", user);
		System.out.println(user);
		return "user/MyProfile";
	}
	
}
