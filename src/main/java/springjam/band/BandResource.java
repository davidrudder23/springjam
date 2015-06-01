package springjam.band;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springjam.concert.Concert;
import springjam.concert.ConcertRepository;
import springjam.song.Song;
import springjam.song.SongRepository;

import java.util.List;

/**
 * Created by drig on 5/30/15.
 */
@RestController
@RequestMapping(value = "/api/band")
public class BandResource {
    @Autowired
    BandRepository bandRepository;

    @Autowired
    ConcertRepository concertRepository;

    @Autowired
    SongRepository songRepository;

    protected final Log logger = LogFactory.getLog(this.getClass());

    @RequestMapping(value = "", method = RequestMethod.GET)
    @ResponseBody
    Iterable<Band> band() {
        System.out.println("getting all bands");
        Iterable<Band> bands = bandRepository.findAll();
        return bands;
    }

    @RequestMapping(value = "/{name}", method = RequestMethod.GET)
    @ResponseBody
    Band band(@PathVariable String name) {
        System.out.println("getting band " + name);
        Band band = bandRepository.findByName(name);

        if (band == null) {
            band = bandRepository.findOne(Long.parseLong(name));
        }
        return band;
    }

    @RequestMapping(value = "/{id}/yearsplayed", method = RequestMethod.GET)
    @ResponseBody
    List<Integer> yearsPlayed(@PathVariable Long id) {
        Band band = bandRepository.findOne(id);

        return bandRepository.getYearsPlayed(band);
    }

    @RequestMapping(value = "/{id}/concerts", method = RequestMethod.GET)
    @ResponseBody
    Iterable<Concert> bandsConcerts(@PathVariable Long id) {
        Band band = bandRepository.findOne(id);
        logger.info("looking for concerts for band "+band);
        if (band == null) return null;

        Iterable<Concert> concerts = concertRepository.findByBand(band);
        return concerts;
    }

    @RequestMapping(value = "/{id}/concerts/{year}", method = RequestMethod.GET)
    @ResponseBody
    Iterable<Concert> bandsConcertsInYear(@PathVariable Long id, @PathVariable Integer year) {
        Band band = bandRepository.findOne(id);
        logger.info("looking for concerts for band "+band);
        if (band == null) return null;

        Iterable<Concert> concerts = concertRepository.findByBandAndYear(band, year);
        return concerts;
    }

    @RequestMapping(value = "/{id}/songs", method = RequestMethod.GET)
    @ResponseBody
    Iterable<Song> bandsSongs(@PathVariable Long id) {
        Band band = bandRepository.findOne(id);
        logger.info("looking for songs for band "+band);
        if (band == null) return null;

        Iterable<Song> songs = songRepository.findByBand(band);
        return songs;
    }

}
