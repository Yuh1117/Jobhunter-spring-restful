package vn.vpgh.jobhunter.controller;

import com.turkraft.springfilter.boot.Filter;

import jakarta.validation.Valid;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import vn.vpgh.jobhunter.domain.User;
import vn.vpgh.jobhunter.domain.response.ResCreateUserDTO;
import vn.vpgh.jobhunter.domain.response.ResUpdateUserDTO;
import vn.vpgh.jobhunter.domain.response.ResUserDTO;
import vn.vpgh.jobhunter.domain.response.ResultPaginationDTO;
import vn.vpgh.jobhunter.service.UserService;
import vn.vpgh.jobhunter.util.annotation.ApiMessage;
import vn.vpgh.jobhunter.util.error.IdInvalidException;

@RestController
@RequestMapping("/api/v1")
public class UserController {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public UserController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/users")
    @ApiMessage("Create a user")
    public ResponseEntity<ResCreateUserDTO> createNewUser(@Valid @RequestBody User reqUser) throws IdInvalidException {
        boolean isEmailExist = this.userService.isEmailExist(reqUser.getEmail());
        if (isEmailExist) {
            throw new IdInvalidException("Email already exists");
        }
        reqUser.setPassword(this.passwordEncoder.encode(reqUser.getPassword()));
        User user = this.userService.handleSaveUser(reqUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(this.userService.convertToResCreateUserDTO(user));
    }

    @DeleteMapping("/users/{id}")
    @ApiMessage("Delete a user")
    public ResponseEntity<Void> deleteUser(@PathVariable("id") long id) throws IdInvalidException {
        User user = this.userService.getUserById(id);
        if (user == null) {
            throw new IdInvalidException("There is no user with id " + id);
        }

        this.userService.deleteUserById(id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping("/users/{id}")
    @ApiMessage("Get a user")
    public ResponseEntity<ResUserDTO> getUser(@PathVariable("id") long id) throws IdInvalidException {
        User user = this.userService.getUserById(id);
        if (user == null) {
            throw new IdInvalidException("There is no user with id " + id);
        }

        return ResponseEntity.status(HttpStatus.OK).body(this.userService.convertToResUserDTO(user));
    }

    @GetMapping("/users")
    @ApiMessage("Get all users")
    public ResponseEntity<ResultPaginationDTO> getAllUsers(@Filter Specification<User> specification,
            Pageable pageable) {
        return ResponseEntity.status(HttpStatus.OK).body(this.userService.getAllUsers(specification, pageable));
    }

    @PutMapping("/users")
    @ApiMessage("Update a user")
    public ResponseEntity<ResUpdateUserDTO> updateUser(@RequestBody User reqUser) throws IdInvalidException {
        User user = this.userService.handleUpdateUser(reqUser);
        if (user == null) {
            throw new IdInvalidException("There is no user with id " + reqUser.getId());
        }

        return ResponseEntity.status(HttpStatus.OK).body(this.userService.convertToResUpdateUserDTO(user));
    }

}
