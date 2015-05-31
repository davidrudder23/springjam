package springjam.song;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import springjam.auth.SpringJamUserDetails;
import springjam.band.Band;
import springjam.concert.Concert;
import springjam.performance.Performance;
import springjam.user.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by drig on 5/30/15.
 */
@RestController
@RequestMapping(value = "/api/song")
public class SongResource {

    @Autowired
    SongRepository songRepository;

    protected final Log logger = LogFactory.getLog(this.getClass());


    @RequestMapping(value = "")
    @ResponseBody
    Iterable<Song> songs() {
        return songRepository.findAll();
    }

    @RequestMapping(value = "/{id}")
    @ResponseBody
    Song song(@PathVariable Long id) {
        return songRepository.findOne(id);
    }

    @RequestMapping(value = "/{band}/seen")
    @ResponseBody
    List<Song> usersSeenSongs(@PathVariable Band band) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if ((authentication == null) ||
                (authentication.getPrincipal() == null) ||
                (!(authentication.getPrincipal() instanceof SpringJamUserDetails))) {
            logger.info("Non-authenticated user");
            return null;
        }
        User user = ((SpringJamUserDetails)(authentication.getPrincipal())).getUser();

        List<Concert> attendedConcerts = user.getConcerts();
        List<Song> seenSongs = new ArrayList<Song>();
        for (Concert attendedConcert : attendedConcerts) {

            // If we supplied a valid band, filter out songs that this band did not make
            System.out.println("Looking for band " + band + " and looking at band " + attendedConcert.getBand());
            if ((band != null) && (!attendedConcert.getBand().equals(band))) {
                continue;
            }

            System.out.println("Matched!");
            List<Performance> performances = attendedConcert.getPerformances();
            for (Performance performance : performances) {
                Song song = performance.getSong();
                if (!seenSongs.contains(song)) {
                    seenSongs.add(song);
                }
            }
        }

        return seenSongs;

    }

}
