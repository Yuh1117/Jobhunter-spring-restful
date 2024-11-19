package vn.vpgh.jobhunter.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import vn.vpgh.jobhunter.domain.User;
import vn.vpgh.jobhunter.domain.dto.LoginDTO;
import vn.vpgh.jobhunter.domain.dto.ResLoginDTO;
import vn.vpgh.jobhunter.service.UserService;
import vn.vpgh.jobhunter.util.SecurityUtil;

@RestController
@RequestMapping("/api/v0.1")
public class AuthController {
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final SecurityUtil securityUtil;
    private final UserService userService;

    public AuthController(AuthenticationManagerBuilder authenticationManagerBuilder, SecurityUtil securityUtil,
            UserService userService) {
        this.authenticationManagerBuilder = authenticationManagerBuilder;
        this.securityUtil = securityUtil;
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<ResLoginDTO> login(@Valid @RequestBody LoginDTO login) {
        // Load input username/password into security
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                login.getUsername(), login.getPassword());
        // Authenticate
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        // Set for security context
        SecurityContextHolder.getContext().setAuthentication(authentication);

        User currentUser = this.userService.getUserByEmail(login.getUsername());
        ResLoginDTO resLoginDTO = new ResLoginDTO();
        ResLoginDTO.UserLogin userLogin = new ResLoginDTO.UserLogin(currentUser.getId(), currentUser.getName(),
                currentUser.getEmail());
        resLoginDTO.setUserLogin(userLogin);
        // Create and set token
        resLoginDTO.setAccessToken(this.securityUtil.createToken(authentication));

        return ResponseEntity.status(HttpStatus.OK).body(resLoginDTO);
    }
}
