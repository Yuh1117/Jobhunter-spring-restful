package vn.vpgh.jobhunter.controller;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import vn.vpgh.jobhunter.domain.User;
import vn.vpgh.jobhunter.domain.dto.LoginDTO;
import vn.vpgh.jobhunter.domain.dto.ResLoginDTO;
import vn.vpgh.jobhunter.service.UserService;
import vn.vpgh.jobhunter.util.SecurityUtil;
import vn.vpgh.jobhunter.util.annotation.ApiMessage;

import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import vn.vpgh.jobhunter.util.error.IdInvalidException;

@RestController
@RequestMapping("/api/v0.1")
public class AuthController {
        private final AuthenticationManagerBuilder authenticationManagerBuilder;
        private final SecurityUtil securityUtil;
        private final UserService userService;

        @Value("${vpgh.jwt.refresh-token-validity-in-seconds}")
        private long refreshTokenExpiration;

        public AuthController(AuthenticationManagerBuilder authenticationManagerBuilder, SecurityUtil securityUtil,
                        UserService userService) {
                this.authenticationManagerBuilder = authenticationManagerBuilder;
                this.securityUtil = securityUtil;
                this.userService = userService;
        }

        @PostMapping("/auth/login")
        @ApiMessage("Login")
        public ResponseEntity<ResLoginDTO> login(@Valid @RequestBody LoginDTO login) {
                // Load input username/password into security
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                                login.getUsername(), login.getPassword());
                // Authenticate
                Authentication authentication = authenticationManagerBuilder.getObject()
                                .authenticate(authenticationToken);
                // Set for security context
                SecurityContextHolder.getContext().setAuthentication(authentication);

                User currentUser = this.userService.getUserByEmail(login.getUsername());
                ResLoginDTO resLoginDTO = new ResLoginDTO();
                ResLoginDTO.UserLogin userLogin = new ResLoginDTO.UserLogin(currentUser.getId(), currentUser.getName(),
                                currentUser.getEmail());
                resLoginDTO.setUserLogin(userLogin);

                // Create and set access token
                String role = authentication.getAuthorities().stream().findFirst().get().toString();
                String accessToken = this.securityUtil.createAccessToken(login.getUsername(), role, resLoginDTO);
                resLoginDTO.setAccessToken(accessToken);

                // Create and set refresh token
                String refreshToken = this.securityUtil.createRefreshToken(login.getUsername(), resLoginDTO);
                this.userService.updateUserToken(login.getUsername(), refreshToken);

                // Set cookie
                ResponseCookie cookie = ResponseCookie.from("refresh_token", refreshToken)
                                .httpOnly(true)
                                .secure(true)
                                .path("/")
                                .maxAge(refreshTokenExpiration)
                                .build();

                return ResponseEntity.status(HttpStatus.OK).header(HttpHeaders.SET_COOKIE, cookie.toString())
                                .body(resLoginDTO);
        }

        @GetMapping("/auth/account")
        @ApiMessage("Get account") // F5 - Refresh
        public ResponseEntity<ResLoginDTO.UserLogin> getAccount() {
                ResLoginDTO.UserLogin userLogin = new ResLoginDTO.UserLogin();

                String email = SecurityUtil.getCurrentUserLogin().get();
                User currentUser = this.userService.getUserByEmail(email);
                if (currentUser != null) {
                        userLogin.setId(currentUser.getId());
                        userLogin.setName(currentUser.getName());
                        userLogin.setEmail(currentUser.getEmail());
                }
                return ResponseEntity.status(HttpStatus.OK).body(userLogin);
        }

        @GetMapping("/auth/refresh")
        @ApiMessage("Get user by refresh token")
        public ResponseEntity<ResLoginDTO> getResfreshToken(
                        @CookieValue(name = "refresh_token", defaultValue = "huy") String refreshToken)
                        throws IdInvalidException {
                if (refreshToken.equals("huy")) {
                        throw new IdInvalidException("There are no tokens in cookies");
                }
                // Check valid token
                Jwt decodedRefreshToken = this.securityUtil.checkValidRefreshToken(refreshToken);
                String email = decodedRefreshToken.getSubject();

                // Check user by email & token
                User user = this.userService.getUserByEmailAndRefreshToken(email, refreshToken);
                if (user == null) {
                        throw new IdInvalidException("User not found");
                }

                ResLoginDTO resLoginDTO = new ResLoginDTO();
                ResLoginDTO.UserLogin userLogin = new ResLoginDTO.UserLogin(user.getId(), user.getName(),
                                user.getEmail());
                resLoginDTO.setUserLogin(userLogin);

                // Create and set access token
                String role = "ROLE_USER";
                String newAccessToken = this.securityUtil.createAccessToken(email, role, resLoginDTO);
                resLoginDTO.setAccessToken(newAccessToken);

                // Create and set refresh token
                String newRefreshToken = this.securityUtil.createRefreshToken(user.getEmail(), resLoginDTO);
                this.userService.updateUserToken(user.getEmail(), newRefreshToken);

                // Set cookie
                ResponseCookie cookie = ResponseCookie.from("refresh_token", newRefreshToken)
                                .httpOnly(true)
                                .secure(true)
                                .path("/")
                                .maxAge(refreshTokenExpiration)
                                .build();

                return ResponseEntity.status(HttpStatus.OK).header(HttpHeaders.SET_COOKIE, cookie.toString())
                                .body(resLoginDTO);
        }

        @PostMapping("/auth/logout")
        @ApiMessage("Logout")
        public ResponseEntity<Void> logout() throws IdInvalidException {
                String email = SecurityUtil.getCurrentUserLogin().get();
                if (email.equals("")) {
                        throw new IdInvalidException("Invalid access token");
                }

                // Update refresh token
                this.userService.updateUserToken(email, null);

                // Remove refresh token from cookies
                ResponseCookie cookie = ResponseCookie.from("refresh_token", null)
                                .httpOnly(true)
                                .secure(true)
                                .path("/")
                                .maxAge(0)
                                .build();

                return ResponseEntity.status(HttpStatus.OK).header(HttpHeaders.SET_COOKIE, cookie.toString()).build();
        }

}
