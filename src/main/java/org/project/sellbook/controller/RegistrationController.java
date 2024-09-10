package org.project.sellbook.controller;

import org.project.sellbook.model.User;
import org.project.sellbook.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class RegistrationController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/register")
    public String registration(User user, Model model) {
        String userName = user.getUsername();
        User isUserNameExist = userRepository.findUserByUsername(userName);

        /* check if there is already username in the database have taken */
        if (isUserNameExist != null) {
            model.addAttribute("userNameAlreadyExist", "User already taken. Please choose another username.");
            return "registration_page";
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole("USER");
        userRepository.save(user);
        return "login";
    }

    @GetMapping("/register")
    public String registrationPage() {
        return "registration_page";
    }

}
