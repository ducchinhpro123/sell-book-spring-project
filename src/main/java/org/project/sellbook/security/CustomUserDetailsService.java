package org.project.sellbook.security;


import org.project.sellbook.model.User;
import org.project.sellbook.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    @Autowired
    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User findUserByUserName(String username) {
        return userRepository.findUserByUsername(username);
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findUserByUsername(username);
        if (user != null) {
            return org.springframework.security.core.userdetails.User.builder()
                    .username(user.getUsername())
                    .password(user.getPassword())
                    .roles(getRoles(user)).build();
        } else {
            throw new UsernameNotFoundException(username);
        }
    }

    private String[] getRoles(User user) {
        if (user.getRole() == null) {
            return new String[]{"USER"};
        }

        return user.getRole().split(",");
    }
}