package org.project.sellbook.controller;


import org.modelmapper.ModelMapper;
import org.project.sellbook.DTO.UserDto;
import org.project.sellbook.model.User;
import org.project.sellbook.repository.UserRepository;
import org.project.sellbook.security.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;

@Controller
public class UserController {
    @Autowired
    private PasswordEncoder passwordEncoder;

    private final CustomUserDetailsService customUserDetailsService;
    private final UserRepository userRepository;

    public UserController(CustomUserDetailsService customUserDetailsService, UserRepository userRepository) {
        this.customUserDetailsService = customUserDetailsService;
        this.userRepository = userRepository;
    }

    @GetMapping("/user")
    public String userProfile(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        // get logged in username
        String username = auth.getName();

        User user = customUserDetailsService.findUserByUserName(username);
        model.addAttribute("user", user);

        return "user/index";
    }

    @GetMapping("/user-change")
    public String showEditUserPage(Model model, @RequestParam("id") Integer id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isEmpty()) {
            return "redirect:/home";
        }

        User userPresent = user.get();

        ModelMapper modelMapper = new ModelMapper();
        UserDto userDto = modelMapper.map(userPresent, UserDto.class);

        model.addAttribute("userDto", userDto);
        model.addAttribute("user", userPresent);
        return "user/user-changing";
    }

    @PostMapping("/user-change-post")
    public String editUserInformation(@ModelAttribute UserDto userDto) {
        Optional<User> userOptional = userRepository.findById(userDto.getId());
        if (userOptional.isEmpty()) {
            return "redirect:/home";
        }
        User user = userOptional.get();

        String uploadDir = System.getProperty("user.dir") + "/src/main/resources/static/images/user_avatar/";
        if (!userDto.getImage().isEmpty()) {
            deleteOldImage(uploadDir, user);
            uploadNewImage(uploadDir, userDto, user);
        }


        user.setAddress(userDto.getAddress());
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());

        userRepository.save(user);

        return "redirect:/user";
    }

    @GetMapping("/user/change-password")
    public String showChangePasswordPage(Model model) {
        User user = getCurrentUser();
        model.addAttribute("user", user);
        return "user/change-password";
    }

    @PostMapping("/user/change-password")
    public String changePassword(@RequestParam("oldPassword") String oldPassword,
                                 @RequestParam("newPassword") String newPassword,
                                 @RequestParam("confirmPassword") String confirmPassword) {
        User user = getCurrentUser();


        if (!newPassword.equals(confirmPassword)) {
            return "redirect:/user/change-password?error";
        }

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            return "redirect:/user/change-password?error";
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        return "redirect:/user";
    }

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        return customUserDetailsService.findUserByUserName(username);
    }

    private void deleteOldImage(String uploadDir, User user) {
        Path oldFileImage = Paths.get(uploadDir + user.getImage());
        try {
            Files.delete(oldFileImage);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void uploadNewImage(String uploadDir, UserDto userDto, User user) {
        MultipartFile image = userDto.getImage();
        Date date = new Date();
        String imageFileName = date.getTime() + "_" + StringUtils.cleanPath(Objects.requireNonNull(image.getOriginalFilename()));

        try {
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            try (InputStream inputStream = image.getInputStream()) {
                Files.copy(inputStream, Paths.get(uploadDir, imageFileName), StandardCopyOption.REPLACE_EXISTING);
            }
            user.setImage(imageFileName);
        } catch (IOException e) {
            throw new RuntimeException("Error uploading image", e);
        }
    }
}


