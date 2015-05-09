package springjam.app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;

import springjam.band.Band;
import springjam.band.BandRepository;


@SpringBootApplication
@RestController
@EnableAutoConfiguration
@RequestMapping("/band")
public class BandController {
	
	@Autowired
	BandRepository bandRepository;

	@RequestMapping(method=RequestMethod.GET)
    @ResponseBody
    Band home() {
		Band band = new Band("Test");
		band = bandRepository.save(band);
        return band;
    }

	@RequestMapping("/{id}")
    @ResponseBody
    Band band(@PathVariable Long id) {
		Band band = bandRepository.findOne(id);
        return band;
    }

    public static void main(String[] args) throws Exception {
        SpringApplication.run(BandController.class, args);
    }
}
