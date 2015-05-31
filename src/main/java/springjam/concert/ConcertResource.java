package springjam.concert;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import springjam.auth.SpringJamUserDetails;
import springjam.band.Band;
import springjam.user.User;
import springjam.user.UserRepository;

import java.util.List;

/**
 * Created by drig on 5/30/15.
 */

@RestController
@RequestMapping(value = "/api/concert")
public class ConcertResource {

    @Autowired
    ConcertRepository concertRepository;

    @Autowired
    UserRepository userRepository;

    protected final Log logger = LogFactory.getLog(this.getClass());

    @RequestMapping(value = "", method = RequestMethod.GET)
    @ResponseBody
    Iterable<Concert> concerts() {
        Iterable<Concert> concerts = concertRepository.findAll();
        return concerts;
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ResponseBody
    Concert concert(@PathVariable Long id) {
        Concert concert = concertRepository.findOne(id);
        return concert;
    }


    @RequestMapping(value = "/attended/{concertId}/{attended}", method = RequestMethod.GET)
    @ResponseBody
    String toggleAttended(@PathVariable("concertId") long concertId, @PathVariable("attended") Boolean attended) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if ((authentication == null) ||
                (authentication.getPrincipal() == null) ||
                (!(authentication.getPrincipal() instanceof SpringJamUserDetails))) {
            logger.info("Non-authenticated user");
            return null;
        }
        User user = ((SpringJamUserDetails)(authentication.getPrincipal())).getUser();

        Concert concert = concertRepository.findOne(concertId);
        if (concert == null) {
            return "Could not find concert";
        }
        if (attended) {
            user.addConcert(concert);
            userRepository.save(user);
            return "Added";
        } else {
            user.removeConcert(concert);
            userRepository.save(user);
            return "Removed";
        }
    }



    @RequestMapping(value = "/{band}/seen")
    @ResponseBody
    List<Concert> usersSeenConcerts(@PathVariable Band band) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if ((authentication == null) ||
                (authentication.getPrincipal() == null) ||
                (!(authentication.getPrincipal() instanceof SpringJamUserDetails))) {
            logger.info("Non-authenticated user");
            return null;
        }
        User user = ((SpringJamUserDetails)(authentication.getPrincipal())).getUser();

        return user.getConcerts(band);

    }


}
