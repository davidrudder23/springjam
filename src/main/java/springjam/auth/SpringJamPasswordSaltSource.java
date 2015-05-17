package springjam.auth;

import org.springframework.security.authentication.dao.SaltSource;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import springjam.app.SpringJam;

/**
 * Created by drig on 5/16/15.
 */

@Service
public class SpringJamPasswordSaltSource implements SaltSource{

    @Override
    public Object getSalt(UserDetails user) {
        return ((SpringJamUserDetails)user).getSalt();
    }
}
