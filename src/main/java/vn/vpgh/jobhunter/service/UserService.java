package vn.vpgh.jobhunter.service;

import org.springframework.stereotype.Service;

import vn.vpgh.jobhunter.domain.User;
import vn.vpgh.jobhunter.repository.UserRepository;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User handleSaveUser(User user) {
        return this.userRepository.save(user);
    }
}
