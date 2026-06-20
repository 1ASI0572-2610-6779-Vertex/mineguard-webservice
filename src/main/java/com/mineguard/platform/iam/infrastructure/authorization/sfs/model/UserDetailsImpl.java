package com.mineguard.platform.iam.infrastructure.authorization.sfs.model;

import com.mineguard.platform.iam.domain.model.aggregates.User;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/** Spring Security adapter over the IAM {@link User} aggregate. */
@Getter
@EqualsAndHashCode
public class UserDetailsImpl implements UserDetails {
    private final String username;
    private final String password;
    private final Long companyId;
    private final boolean accountNonExpired = true;
    private final boolean accountNonLocked = true;
    private final boolean credentialsNonExpired = true;
    private final boolean enabled = true;
    private final Collection<? extends GrantedAuthority> authorities;

    public UserDetailsImpl(String username, String password, Long companyId,
                           Collection<? extends GrantedAuthority> authorities) {
        this.username = username;
        this.password = password;
        this.companyId = companyId;
        this.authorities = authorities;
    }

    public static UserDetailsImpl build(User user) {
        var authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getStringName()))
                .collect(Collectors.toList());
        return new UserDetailsImpl(user.getUsername(), user.getPassword(), user.getCompanyId(), authorities);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities == null ? List.of() : authorities;
    }
}
