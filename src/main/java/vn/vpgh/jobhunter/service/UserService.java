package vn.vpgh.jobhunter.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import vn.vpgh.jobhunter.domain.Company;
import vn.vpgh.jobhunter.domain.User;
import vn.vpgh.jobhunter.domain.response.ResCreateUserDTO;
import vn.vpgh.jobhunter.domain.response.ResUpdateUserDTO;
import vn.vpgh.jobhunter.domain.response.ResUserDTO;
import vn.vpgh.jobhunter.domain.response.ResultPaginationDTO;
import vn.vpgh.jobhunter.repository.CompanyRepository;
import vn.vpgh.jobhunter.repository.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;

    public UserService(UserRepository userRepository, CompanyRepository companyRepository) {
        this.userRepository = userRepository;
        this.companyRepository = companyRepository;
    }

    public User handleSaveUser(User user) {
        // Check company
        if (user.getCompany() != null) {
            Optional<Company> optionalCompany = this.companyRepository.findById(user.getCompany().getId());
            user.setCompany(optionalCompany.isPresent() ? optionalCompany.get() : null);
        }
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
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();
        Page<User> pageUser = this.userRepository.findAll(specification, pageable);

        meta.setPage(pageUser.getNumber() + 1);
        meta.setPageSize(pageUser.getSize());
        meta.setPages(pageUser.getTotalPages());
        meta.setTotal(pageUser.getTotalElements());

        res.setMeta(meta);

        List<ResUserDTO> listUser = pageUser.getContent().stream()
                .map(item -> {
                    ResUserDTO resUserDTO = new ResUserDTO();
                    resUserDTO.setId(item.getId());
                    resUserDTO.setName(item.getName());
                    resUserDTO.setEmail(item.getEmail());
                    resUserDTO.setGender(item.getGender());
                    resUserDTO.setAddress(item.getAddress());
                    resUserDTO.setAge(item.getAge());
                    resUserDTO.setCreatedAt(item.getCreatedAt());
                    resUserDTO.setUpdatedAt(item.getUpdatedAt());
                    if (item.getCompany() != null) {
                        resUserDTO.setCompany(
                                new ResUserDTO.CompanyUser(item.getCompany().getId(), item.getCompany().getName()));
                    } else {
                        resUserDTO.setCompany(null);
                    }
                    return resUserDTO;
                })
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

            if (reqUser.getCompany() != null) {
                Optional<Company> optionalCompany = this.companyRepository.findById(reqUser.getCompany().getId());
                currentUser.setCompany(optionalCompany.isPresent() ? optionalCompany.get() : null);
            }

            currentUser = this.userRepository.save(currentUser);
            return currentUser;
        }
        return null;
    }

    public User getUserByEmail(String email) {
        return this.userRepository.findByEmail(email);
    }

    public boolean isEmailExist(String email) {
        return this.userRepository.existsByEmail(email);
    }

    public ResCreateUserDTO convertToResCreateUserDTO(User user) {
        ResCreateUserDTO res = new ResCreateUserDTO();
        ResCreateUserDTO.CompanyUser comUser = new ResCreateUserDTO.CompanyUser();

        res.setId(user.getId());
        res.setName(user.getName());
        res.setEmail(user.getEmail());
        res.setGender(user.getGender());
        res.setAddress(user.getAddress());
        res.setAge(user.getAge());
        res.setCreatedAt(user.getCreatedAt());

        if (user.getCompany() != null) {
            comUser.setId(user.getCompany().getId());
            comUser.setName(user.getCompany().getName());
            res.setCompany(comUser);
        }

        return res;
    }

    public ResUserDTO convertToResUserDTO(User user) {
        ResUserDTO res = new ResUserDTO();
        ResUserDTO.CompanyUser comUser = new ResUserDTO.CompanyUser();

        res.setId(user.getId());
        res.setName(user.getName());
        res.setEmail(user.getEmail());
        res.setGender(user.getGender());
        res.setAddress(user.getAddress());
        res.setAge(user.getAge());
        res.setCreatedAt(user.getCreatedAt());
        res.setUpdatedAt(user.getUpdatedAt());

        if (user.getCompany() != null) {
            comUser.setId(user.getCompany().getId());
            comUser.setName(user.getCompany().getName());
            res.setCompany(comUser);
        }

        return res;
    }

    public ResUpdateUserDTO convertToResUpdateUserDTO(User user) {
        ResUpdateUserDTO res = new ResUpdateUserDTO();
        ResUpdateUserDTO.CompanyUser comUser = new ResUpdateUserDTO.CompanyUser();

        res.setId(user.getId());
        res.setName(user.getName());
        res.setEmail(user.getEmail());
        res.setGender(user.getGender());
        res.setAddress(user.getAddress());
        res.setAge(user.getAge());
        res.setUpdatedAt(user.getUpdatedAt());

        if (user.getCompany() != null) {
            comUser.setId(user.getCompany().getId());
            comUser.setName(user.getCompany().getName());
            res.setCompany(comUser);
        }

        return res;
    }

    public void updateUserToken(String email, String token) {
        User user = this.userRepository.findByEmail(email);
        if (user != null) {
            user.setRefreshToken(token);
            this.userRepository.save(user);
        }
    }

    public User getUserByEmailAndRefreshToken(String email, String refreshToken) {
        return this.userRepository.findByEmailAndRefreshToken(email, refreshToken);
    }

}
