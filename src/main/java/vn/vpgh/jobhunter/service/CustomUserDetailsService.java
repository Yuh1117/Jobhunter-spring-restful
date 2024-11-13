package vn.vpgh.jobhunter.service;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import vn.vpgh.jobhunter.domain.User;

import java.util.Collections;

@Component("userDetailsService")
public class CustomUserDetailsService implements UserDetailsService {
    private final UserService userService;

    public CustomUserDetailsService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = this.userService.getUserByEmail(username);
        if (user == null) {
            throw new UsernameNotFoundException("Invalid username/password");
        }

        return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(),
                Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")));

    }
}
