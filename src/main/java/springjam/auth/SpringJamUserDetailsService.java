package springjam.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import springjam.user.User;
import springjam.user.UserRepository;

import java.util.Optional;

/**
 * Created by drig on 5/16/15.
 */
@Service
public class SpringJamUserDetailsService  implements UserDetailsService {

    private final UserRepository userRepository;

    @Autowired
    public SpringJamUserDetailsService(final UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(username);
        if (user == null) {
            throw new UsernameNotFoundException("User " + username + " not found");
        }
        return new SpringJamUserDetails(user);
    }
}
