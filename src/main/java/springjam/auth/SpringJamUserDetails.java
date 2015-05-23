package springjam.auth;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import springjam.user.User;

import java.util.Collection;
import java.util.Collections;
import static java.util.Collections.unmodifiableList;
import static java.util.Arrays.asList;

/**
 * Created by drig on 5/16/15.
 */
public class SpringJamUserDetails implements UserDetails {

    User user;
    private final Collection<SimpleGrantedAuthority> authorities;

    public SpringJamUserDetails(User user) {
        this.user = user;
        this.authorities = unmodifiableList(asList(new SimpleGrantedAuthority("ROLE_USER")));
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return user.getHashedPassword();
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }

    public String getSalt() { return user.getSalt();  }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
