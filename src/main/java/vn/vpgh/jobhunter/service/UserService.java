package vn.vpgh.jobhunter.service;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import vn.vpgh.jobhunter.domain.User;
import vn.vpgh.jobhunter.domain.dto.Meta;
import vn.vpgh.jobhunter.domain.dto.ResultPaginationDTO;
import vn.vpgh.jobhunter.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User handleSaveUser(User user) {
        return this.userRepository.save(user);
    }

    public void deleteUserById(long id) {
        this.userRepository.deleteById(id);
    }

    public User getUserById(long id) {
        Optional<User> optionalUser = this.userRepository.findById(id);
        return optionalUser.isPresent() ? optionalUser.get() : null;
    }

    public ResultPaginationDTO getAllUsers(Specification<User> specification) {
        ResultPaginationDTO res = new ResultPaginationDTO();
        Meta meta = new Meta();
        List<User> companies = this.userRepository.findAll(specification);

        res.setMeta(meta);
        res.setResult(companies);

        return res;
    }

    public User handleUpdateUser(User reqUser) {
        User currentUser = this.getUserById(reqUser.getId());
        if (currentUser != null) {
            if (reqUser.getEmail() != null)
                currentUser.setEmail(reqUser.getEmail());
            if (reqUser.getPassword() != null)
                currentUser.setPassword(reqUser.getPassword());
            if (reqUser.getName() != null)
                currentUser.setName(reqUser.getName());

            currentUser = this.userRepository.save(currentUser);
            return currentUser;
        }
        return null;
    }

    public User getUserByEmail(String email) {
        return this.userRepository.findByEmail(email);
    }
}
