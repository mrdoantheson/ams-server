package ams.security;

import ams.constant.AppConstant;
import ams.enums.UserRole;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Arrays;
import java.util.Optional;



public final class SecurityUtil {

    private SecurityUtil() {
    }

    public static Optional<String> getCurrentUserLogin() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();
        Object object = authentication.getPrincipal();
        if (object instanceof UserDetails) {
            return Optional.of(((UserDetails) object).getUsername());
        }
        return Optional.empty();
    }

    public static Optional<UserRole> getRoleCurrentUserLogin() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();

        return authentication.getAuthorities().stream()
                .map(authority -> UserRole.valueOf(authority.getAuthority().replaceAll(AppConstant.USER_ROLE_PREFIX, "")))
                .findFirst();



//        List<GrantedAuthority> authorityList = (List<GrantedAuthority>) authentication.getAuthorities();
//        if (authorityList != null && !authorityList.isEmpty()) {
//            GrantedAuthority grantedAuthority = authorityList.get(0);
//            UserRole userRole = UserRole.valueOf(grantedAuthority.getAuthority());
//            return Optional.of(userRole);
//        }
//        return Optional.empty();
    }

    public static boolean isSystemAdmin() {
        Optional<UserRole> userRoleOptional = getRoleCurrentUserLogin();
        return userRoleOptional.isPresent() && userRoleOptional.get() == UserRole.SYSTEM_ADMIN;
    }

    public static boolean isFaManager() {
        Optional<UserRole> userRoleOptional = getRoleCurrentUserLogin();
        return userRoleOptional.isPresent() && userRoleOptional.get() == UserRole.FA_MANAGER;
    }

    public static boolean isDeliveryManager() {
        Optional<UserRole> userRoleOptional = getRoleCurrentUserLogin();
        return userRoleOptional.isPresent() && userRoleOptional.get() == UserRole.DELIVERY_MANAGER;
    }

    public static boolean isClassAdmin() {
        Optional<UserRole> userRoleOptional = getRoleCurrentUserLogin();
        return userRoleOptional.isPresent() && userRoleOptional.get() == UserRole.CLASS_ADMIN;
    }

    public static boolean isRecruiter() {
        Optional<UserRole> userRoleOptional = getRoleCurrentUserLogin();
        return userRoleOptional.isPresent() && userRoleOptional.get() == UserRole.RECRUITER;
    }

    public static boolean isTrainer() {
        Optional<UserRole> userRoleOptional = getRoleCurrentUserLogin();
        return userRoleOptional.isPresent() && userRoleOptional.get() == UserRole.TRAINER;
    }

    public static boolean isTrainee() {
        Optional<UserRole> userRoleOptional = getRoleCurrentUserLogin();
        return userRoleOptional.isPresent() && userRoleOptional.get() == UserRole.TRAINEE;
    }

    public static boolean isAnyRole(UserRole... userRoles) {
        Optional<UserRole> userRoleOptional = getRoleCurrentUserLogin();
        return Arrays.stream(userRoles)
                .anyMatch(role -> userRoleOptional.isPresent() && userRoleOptional.get() == role);
    }

}
