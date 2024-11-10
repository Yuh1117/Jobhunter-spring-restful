package vn.vpgh.jobhunter.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    
    @PostMapping("/users")
    public ResponseEntity<User> createNewUser(@RequestBody User reqUser) {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.userService.handleSaveUser(reqUser));
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable("id") long id) {
        this.userService.deleteUserById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<User> getUser(@PathVariable("id") long id) {
        return ResponseEntity.status(HttpStatus.OK).body(this.userService.getUserById(id));
    }

    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUser() {
        return ResponseEntity.status(HttpStatus.OK).body(this.userService.getAllUsers());
    }

    @PutMapping("/users")
    public ResponseEntity<User> updateUser(@RequestBody User reqUser) {
        return ResponseEntity.status(HttpStatus.OK).body(this.userService.handleUpdateUser(reqUser));
    }

}
