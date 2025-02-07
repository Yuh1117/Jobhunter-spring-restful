package vn.vpgh.jobhunter.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;
import vn.vpgh.jobhunter.domain.Permission;
import vn.vpgh.jobhunter.domain.Role;
import vn.vpgh.jobhunter.domain.User;
import vn.vpgh.jobhunter.service.UserService;
import vn.vpgh.jobhunter.util.SecurityUtil;
import vn.vpgh.jobhunter.util.error.PermissionException;

import java.util.List;

public class PermissionInterceptor implements HandlerInterceptor {
    @Autowired
    UserService userService;

    @Override
    @Transactional
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String path = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
        String requestURI = request.getRequestURI();
        String httpMethod = request.getMethod();
        System.out.println(">>> RUN preHandle");
        System.out.println(">>> path= " + path);
        System.out.println(">>> httpMethod= " + httpMethod);
        System.out.println(">>> requestURI= " + requestURI);

        // check permission
        String email = SecurityUtil.getCurrentUserLogin().isPresent() ? SecurityUtil.getCurrentUserLogin().get() : "";
        if (email != null && !email.isEmpty()) {
            User currentUser = this.userService.getUserByEmail(email);
            if (currentUser != null) {
                Role role = currentUser.getRole();
                if (role != null) {
                    List<Permission> permissions = role.getPermissions();
                    boolean isAllow = permissions.stream().anyMatch(i ->
                            i.getApiPath().equals(path) && i.getMethod().equals(httpMethod)
                    );

                    if (!isAllow) {
                        throw new PermissionException("You do not have permission to access.");
                    }
                } else {
                    throw new PermissionException("You do not have permission to access.");
                }
            }

        }

        return true;
    }
}
