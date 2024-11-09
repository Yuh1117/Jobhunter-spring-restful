package vn.vpgh.jobhunter.controller;

import org.springframework.web.bind.annotation.RestController;

import vn.vpgh.jobhunter.domain.User;
import vn.vpgh.jobhunter.service.UserService;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/user/create")
    public User createNewUser(@RequestBody User postManUser) {
        this.userService.handleSaveUser(postManUser);

        return postManUser;
    }

}
