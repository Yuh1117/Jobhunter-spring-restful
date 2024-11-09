package vn.vpgh.jobhunter.controller;

import org.springframework.web.bind.annotation.RestController;

import vn.vpgh.jobhunter.domain.User;
import vn.vpgh.jobhunter.service.UserService;

import org.springframework.web.bind.annotation.GetMapping;

@RestController
public class Usercontroller {
    private final UserService userService;

    public Usercontroller(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/user/create")
    public String createNewUser() {
        User user = new User();
        user.setEmail("abc@gmail.com");
        user.setPassword("123");
        user.setName("huy");
        this.userService.handleSaveUser(user);
        return "create";
    }

}
