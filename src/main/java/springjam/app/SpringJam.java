package springjam.app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.orm.jpa.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.bind.annotation.*;

import springjam.model.*;

import java.security.MessageDigest;


@SpringBootApplication
@RestController
@EnableAutoConfiguration
@EnableJpaRepositories("springjam.model")
@ComponentScan(basePackages = { "springjam.net" })
@EntityScan(basePackageClasses=Band.class)

public class SpringJam {
	
	@Autowired
	BandRepository bandRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ConcertRepository concertRepository;

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
		Iterable<Band> bands = bandRepository.findByName(name);
        Band band = null;
        if (bands.iterator().hasNext()) {
            band = bands.iterator().next();
        }
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
        return user.getPassword()+":"+user.getSalt();
    }

    @RequestMapping(value = "/concert", method = RequestMethod.GET)
    @ResponseBody
    Iterable<Concert> concerts() {
        Iterable<Concert> concerts = concertRepository.findAll();
        return concerts;
    }


    public static void main(String[] args) throws Exception {
        SpringApplication.run(SpringJam.class, args);
    }
}
