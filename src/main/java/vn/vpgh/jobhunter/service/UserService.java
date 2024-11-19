package vn.vpgh.jobhunter.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import vn.vpgh.jobhunter.domain.User;
import vn.vpgh.jobhunter.domain.dto.Meta;
import vn.vpgh.jobhunter.domain.dto.ResCreateUserDTO;
import vn.vpgh.jobhunter.domain.dto.ResUpdateUserDTO;
import vn.vpgh.jobhunter.domain.dto.ResUserDTO;
import vn.vpgh.jobhunter.domain.dto.ResultPaginationDTO;
import vn.vpgh.jobhunter.repository.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

    public ResultPaginationDTO getAllUsers(Specification<User> specification, Pageable pageable) {
        ResultPaginationDTO res = new ResultPaginationDTO();
        Meta meta = new Meta();
        Page<User> pageUser = this.userRepository.findAll(specification, pageable);

        meta.setPage(pageUser.getNumber() + 1);
        meta.setPageSize(pageUser.getSize());
        meta.setPages(pageUser.getTotalPages());
        meta.setTotal(pageUser.getTotalElements());

        res.setMeta(meta);

        List<ResUserDTO> listUser = pageUser.getContent().stream()
                .map(item -> new ResUserDTO(item.getId(), item.getName(), item.getEmail(), item.getGender(),
                        item.getAddress(), item.getAge(), item.getCreateAt(), item.getUpdateAt()))
                .collect(Collectors.toList());

        res.setResult(listUser);
        return res;
    }

    public User handleUpdateUser(User reqUser) {
        User currentUser = this.getUserById(reqUser.getId());
        if (currentUser != null) {
            if (reqUser.getName() != null)
                currentUser.setName(reqUser.getName());

            if (reqUser.getGender() != null)
                currentUser.setGender(reqUser.getGender());

            if (reqUser.getAddress() != null)
                currentUser.setAddress(reqUser.getAddress());

            if (reqUser.getAge() != 0)
                currentUser.setAge(reqUser.getAge());

            currentUser = this.userRepository.save(currentUser);
            return currentUser;
        }
        return null;
    }

    public User getUserByEmail(String email) {
        return this.userRepository.findByEmail(email);
    }

    public boolean isEmaliExist(String email) {
        return this.userRepository.existsByEmail(email);
    }

    public ResCreateUserDTO convertToResCreateUserDTO(User user) {
        ResCreateUserDTO res = new ResCreateUserDTO();
        res.setId(user.getId());
        res.setName(user.getName());
        res.setEmail(user.getEmail());
        res.setGender(user.getGender());
        res.setAddress(user.getAddress());
        res.setAge(user.getAge());
        res.setCreateAt(user.getCreateAt());

        return res;
    }

    public ResUserDTO convertToResUserDTO(User user) {
        ResUserDTO res = new ResUserDTO();
        res.setId(user.getId());
        res.setName(user.getName());
        res.setEmail(user.getEmail());
        res.setGender(user.getGender());
        res.setAddress(user.getAddress());
        res.setAge(user.getAge());
        res.setCreateAt(user.getCreateAt());
        res.setUpdateAt(user.getUpdateAt());

        return res;
    }

    public ResUpdateUserDTO convertToResUpdateUserDTO(User user) {
        ResUpdateUserDTO res = new ResUpdateUserDTO();
        res.setId(user.getId());
        res.setName(user.getName());
        res.setEmail(user.getEmail());
        res.setGender(user.getGender());
        res.setAddress(user.getAddress());
        res.setAge(user.getAge());
        res.setUpdateAt(user.getUpdateAt());

        return res;
    }

    public void updateUserToken(String email, String token) {
        User user = this.userRepository.findByEmail(email);
        if (user != null) {
            user.setRefreshToken(token);
            this.userRepository.save(user);
        }
    }

}
