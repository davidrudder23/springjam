package springjam.auth;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import springjam.user.RegistrationDTO;
import springjam.user.User;
import springjam.user.UserRepository;

/**
 * Created by drig on 5/30/15.
 */

@RestController
@RequestMapping(value = "/api/")
public class AuthResource {

    @Autowired
    UserRepository userRepository;

    protected final Log logger = LogFactory.getLog(this.getClass());

    @RequestMapping(value = "/noauth/registerUser", method = RequestMethod.POST)
    @ResponseBody
    String registerUser(@RequestBody RegistrationDTO user) {

        User existingUser = userRepository.findByEmail(user.getEmail());
        if (existingUser != null) {
            return "A user with that email already exists";
        }

        User newUser = new User();
        newUser.setEmail(user.getEmail());

        newUser.generateSalt(4);
        newUser.setPassword(user.getPassword(), newUser.getSalt());

        userRepository.save(newUser);
        return "User registered";
    }

    @RequestMapping(value = "/user", method = RequestMethod.GET)
    @ResponseBody
    User currentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if ((authentication == null) ||
                (authentication.getPrincipal() == null) ||
                (!(authentication.getPrincipal() instanceof SpringJamUserDetails))) {
            logger.info("Non-authenticated user");
            return null;
        }
        User user = ((SpringJamUserDetails)(authentication.getPrincipal())).getUser();

        return user;
    }


}
