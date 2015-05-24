package springjam.app;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.orm.jpa.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import springjam.auth.SpringJamUserDetails;
import springjam.band.Band;
import springjam.band.BandRepository;
import springjam.concert.Concert;
import springjam.concert.ConcertRepository;
import springjam.performance.Performance;
import springjam.performance.PerformanceRepository;
import springjam.song.Song;
import springjam.song.SongRepository;
import springjam.user.RegistrationDTO;
import springjam.user.User;
import springjam.user.UserRepository;
import springjam.util.PhishDownloader;
import springjam.venue.Venue;
import springjam.venue.VenueRepository;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;


@SpringBootApplication
@RestController
@EnableAutoConfiguration
@EnableJpaRepositories(basePackages = { "springjam.user", "springjam.band", "springjam.concert", "springjam.performance", "springjam.song", "springjam.venue" })
@ComponentScan(basePackages = { "springjam.user", "springjam.band", "springjam.concert", "springjam.performance", "springjam.song", "springjam.venue", "springjam.auth" })
@EntityScan(basePackageClasses={ Band.class, User.class, Concert.class, Song.class, Performance.class, Venue.class})
@RequestMapping(value = "/api")
public class SpringJam {

    @Autowired
    BandRepository bandRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ConcertRepository concertRepository;

    @Autowired
    VenueRepository venueRepository;

    @Autowired
    PerformanceRepository performanceRepository;

    @Autowired
    SongRepository songRepository;

    protected final Log logger = LogFactory.getLog(this.getClass());


    @RequestMapping(value = "/", method = RequestMethod.GET)
    @ResponseBody
    User home() {
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

    @RequestMapping(value = "/band", method = RequestMethod.GET)
    @ResponseBody
    Iterable<Band> band() {
        System.out.println("getting all bands");
        Iterable<Band> bands = bandRepository.findAll();
        return bands;
    }

    @RequestMapping(value = "/band/{name}", method = RequestMethod.GET)
    @ResponseBody
    Band band(@PathVariable String name) {
        System.out.println("getting band " + name);
        Band band = bandRepository.findByName(name);
        return band;
    }


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

    @RequestMapping(value = "/concert", method = RequestMethod.GET)
    @ResponseBody
    Iterable<Concert> concerts() {
        Iterable<Concert> concerts = concertRepository.findAll();
        return concerts;
    }

    @RequestMapping(value = "/concert/{id}", method = RequestMethod.GET)
    @ResponseBody
    Concert concert(@PathVariable Long id) {
        Concert concert = concertRepository.findOne(id);
        return concert;
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

    @RequestMapping(value = "/concert/attended/{concertId}/{attended}", method = RequestMethod.GET)
    @ResponseBody
    String toggleAttended(@PathVariable Long concertId, @PathVariable Boolean attended) {
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

    @RequestMapping(value = "/song")
    @ResponseBody
    Iterable<Song> songs() {
        return songRepository.findAll();
    }

    @RequestMapping(value = "/song/{id}")
    @ResponseBody
    Song song(@PathVariable Long id) {
        return songRepository.findOne(id);
    }

    @RequestMapping(value = "/song/{band}/seen")
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


    @RequestMapping(value = "/concert/{band}/seen")
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

    @RequestMapping(value = "/performances/{song}")
    @ResponseBody
    Iterable<Performance> performances(@PathVariable Song song) {
        List<Concert> concerts = new ArrayList<Concert>();
        Iterable<Performance> performances = performanceRepository.findBySong(song);

        return performances;

    }

    @RequestMapping(value = "/noauth/phishdownloader/{date}", method = RequestMethod.GET)
    @ResponseBody
    String toggleAttended(@PathVariable String date) {
        PhishDownloader phishDownloader = new PhishDownloader();
        phishDownloader.setBandRepository(bandRepository);
        phishDownloader.setVenueRepository(venueRepository);
        phishDownloader.setUserRepository(userRepository);
        phishDownloader.setConcertRepository(concertRepository);
        phishDownloader.setPerformanceRepository(performanceRepository);
        phishDownloader.setSongRepository(songRepository);
        phishDownloader.setDate(date);

        phishDownloader.importShow();
        return "";
    }

    public static void main(String[] args) throws Exception {
        SpringApplication.run(SpringJam.class, args);
    }


    private static final String RESOURCE_ID = "springjam";

}
