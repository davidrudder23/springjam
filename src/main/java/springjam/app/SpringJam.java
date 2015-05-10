package springjam.app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.orm.jpa.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.bind.annotation.*;

import springjam.band.Band;
import springjam.band.BandRepository;
import springjam.concert.Concert;
import springjam.concert.ConcertRepository;
import springjam.performance.Performance;
import springjam.performance.PerformanceRepository;
import springjam.song.Song;
import springjam.song.SongRepository;
import springjam.user.User;
import springjam.user.UserRepository;
import springjam.util.PhishDownloader;
import springjam.venue.Venue;
import springjam.venue.VenueRepository;


@SpringBootApplication
@RestController
@EnableAutoConfiguration
@EnableJpaRepositories(basePackages = { "springjam.user", "springjam.band", "springjam.concert", "springjam.performance", "springjam.song", "springjam.venue" })
@ComponentScan(basePackages = { "springjam.user", "springjam.band", "springjam.concert", "springjam.performance", "springjam.song", "springjam.venue" })
@EntityScan(basePackageClasses={ Band.class, User.class, Concert.class, Song.class, Performance.class, Venue.class})

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

    @RequestMapping(value = "/", method = RequestMethod.GET)
    @ResponseBody
    Iterable<User> home() {
		Iterable<User> users = userRepository.findAll();
        return users;
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
		System.out.println("getting band "+name);
		Band band = bandRepository.findByName(name);
        return band;
    }



    @RequestMapping(value="/registerUser", method=RequestMethod.POST)
    @ResponseBody String registerUser(@RequestBody User user) {

        User existingUser = userRepository.findByEmail(user.getEmail());
        if (existingUser != null) {
            return "A user with that email already exists";
        }

        user.generateSalt(4);
        user.setPassword(user.getPassword(), user.getSalt());

        userRepository.save(user);
        return "User registered";
    }

    @RequestMapping(value = "/concert", method = RequestMethod.GET)
    @ResponseBody
    Iterable<Concert> concerts() {
        Iterable<Concert> concerts = concertRepository.findAll();
        return concerts;
    }

    @RequestMapping(value = "/user", method = RequestMethod.GET)
    @ResponseBody
    User currentUser() {
        User user = userRepository.findOne(1l);
        return user;
    }

    @RequestMapping(value = "/concert/attended/{concertId}/{attended}", method = RequestMethod.GET)
    @ResponseBody
    String toggleAttended(@PathVariable Long concertId, @PathVariable Boolean attended) {
        User user = userRepository.findOne(1l);
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

    @RequestMapping(value = "/phishdownloader/{date}", method = RequestMethod.GET)
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
}
