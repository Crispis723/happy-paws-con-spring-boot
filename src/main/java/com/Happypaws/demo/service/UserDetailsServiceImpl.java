package com.Happypaws.demo.service;

import com.Happypaws.demo.model.User;
import com.Happypaws.demo.repository.UserRepository;
import com.Happypaws.demo.repository.PermissionRepository;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;
    private final PermissionRepository permissionRepository;

    public UserDetailsServiceImpl(UserRepository userRepository, PermissionRepository permissionRepository) {
        this.userRepository = userRepository;
        this.permissionRepository = permissionRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UsernameNotFoundException(
                                "Usuario no encontrado: " + email
                        )
                );

        Set<GrantedAuthority> authorities = new HashSet<>();

        // ============================================================
        // ROLES
        // ============================================================

        user.getRoles().stream().filter(role -> Boolean.TRUE.equals(role.getEnabled())).forEach(role -> {

            if (role.getName() != null && !role.getName().isBlank()) {

                authorities.add(
                    new SimpleGrantedAuthority(
                        "ROLE_" + role.getName().toUpperCase()
                    )
                );
            }

            // ========================================================
            // PERMISOS DEL ROL
            // ========================================================

            if (role.getPermissions() != null) {

                role.getPermissions().forEach(permission -> {

                    if (Boolean.TRUE.equals(permission.getEnabled())
                            && permission.getCode() != null
                            && !permission.getCode().isBlank()) {

                        authorities.add(
                            new SimpleGrantedAuthority(
                                permission.getCode().toUpperCase()
                            )
                        );
                    }
                });
            }
        });

        // ADMIN is a super-administrator inside the application.
        // This also repairs access for databases with stale role_permissions rows.
        boolean isAdmin = authorities.stream()
                .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));
        if (isAdmin) {
            permissionRepository.findAll().stream()
                    .filter(p -> Boolean.TRUE.equals(p.getEnabled()))
                    .map(p -> p.getCode())
                    .filter(code -> code != null && !code.isBlank())
                    .map(code -> new SimpleGrantedAuthority(code.toUpperCase()))
                    .forEach(authorities::add);
        }

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),

                Boolean.TRUE.equals(user.getEnabled()),

                true,
                true,
                true,

                authorities
        );
    }
}
