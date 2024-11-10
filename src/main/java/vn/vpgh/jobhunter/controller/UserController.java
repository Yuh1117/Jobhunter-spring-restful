package vn.vpgh.jobhunter.controller;

import org.springframework.web.bind.annotation.*;

import vn.vpgh.jobhunter.domain.User;
import vn.vpgh.jobhunter.service.UserService;

import java.util.List;

@RestController
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/user")
    public User createNewUser(@RequestBody User reqUser) {
        this.userService.handleSaveUser(reqUser);
        return reqUser;
    }

    @DeleteMapping("/user/{id}")
    public String deleteUser(@PathVariable("id") long id) {
        this.userService.deleteUserById(id);
        return "delete";
    }

    @GetMapping("/user/{id}")
    public User getUser(@PathVariable("id") long id) {
        return this.userService.getUserById(id);
    }

    @GetMapping("/user")
    public List<User> getAllUser() {
        return this.userService.getAllUsers();
    }

    @PutMapping("/user")
    public User updateUser(@RequestBody User reqUser) {
        return this.userService.handleUpdateUser(reqUser);
    }

}
