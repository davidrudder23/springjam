package springjam;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.orm.jpa.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;
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
import springjam.util.GratefulDeadDownloader;
import springjam.util.PhishDownloader;
import springjam.util.TwiddleDownloader;
import springjam.venue.Venue;
import springjam.venue.VenueRepository;

import java.util.ArrayList;
import java.util.List;


@SpringBootApplication
@RestController
@EnableAutoConfiguration
@EnableJpaRepositories(basePackages = { "springjam" })
@ComponentScan(basePackages = { "springjam" })
@EntityScan(basePackageClasses={ Band.class, User.class, Concert.class, Song.class, Performance.class, Venue.class})
@RequestMapping(value = "/api")
@EnableScheduling
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

    @RequestMapping(value = "/noauth/phishdownloader/{date}", method = RequestMethod.GET)
    @ResponseBody
    String phishDownloader(@PathVariable String date) {
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

    @RequestMapping(value = "/noauth/twiddledownloader", method = RequestMethod.GET)
    @ResponseBody
    String twiddleDownloader() {
        new TwiddleDownloader().download(bandRepository,
                songRepository,
                performanceRepository,
                concertRepository,
                venueRepository);
        return "";
    }

    @RequestMapping(value = "/noauth/gratefuldeaddownloader", method = RequestMethod.GET)
    @ResponseBody
    String gratefulDeadDownloader() {
        new GratefulDeadDownloader().download(bandRepository,
                songRepository,
                performanceRepository,
                concertRepository,
                venueRepository);
        return "";
    }

    public static void main(String[] args) throws Exception {
        SpringApplication.run(SpringJam.class, args);
    }


    private static final String RESOURCE_ID = "springjam";

}
