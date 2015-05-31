package springjam.performance;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import springjam.concert.Concert;
import springjam.song.Song;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by drig on 5/30/15.
 */
@RestController
@RequestMapping(value = "/api/performance")
public class PerformanceResource {

    @Autowired
    PerformanceRepository performanceRepository;

    @RequestMapping(value = "/{song}")
    @ResponseBody
    Iterable<Performance> performances(@PathVariable Song song) {
        Iterable<Performance> performances = performanceRepository.findBySong(song);

        return performances;

    }

}


