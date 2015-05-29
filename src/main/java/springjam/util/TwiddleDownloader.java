package springjam.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import springjam.band.Band;
import springjam.band.BandRepository;
import springjam.concert.Concert;
import springjam.concert.ConcertRepository;
import springjam.performance.Performance;
import springjam.performance.PerformanceRepository;
import springjam.song.Song;
import springjam.song.SongRepository;
import springjam.venue.Venue;
import springjam.venue.VenueRepository;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by drig on 5/24/15.
 */
public class TwiddleDownloader {
    protected final Log logger = LogFactory.getLog(this.getClass());



    public void download(BandRepository bandRepository,
                         SongRepository songRepository,
                         PerformanceRepository performanceRepository,
                         ConcertRepository concertRepository,
                         VenueRepository venueRepository) {
        try {
            SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd");

            Band twiddle = bandRepository.findByName("Twiddle");

            List<Concert> concerts = new ArrayList<Concert>();

            logger.info("in twiddle downloader");
            for (int year = 2015; year <= 2015; year++) {
                Document doc = Jsoup.connect("http://www.utwiddle.net/"+year+".html").get();

                Elements shows = doc.select(".popbox");

                logger.info("Found " + shows.size() + " shows");
                Iterator showIterator = shows.iterator();
                while (showIterator.hasNext()) {
                    Element show = (Element) showIterator.next();

                    String id = show.attr("id");
                    logger.info("show id=" + id);
                    if ((id == null) || (!id.startsWith("pop"))) {
                        continue;
                    }

                    String dateString = id.substring(3);

                    Date date = parser.parse(dateString);
                    logger.info("date=" + date);


                    // Get the venue
                    Venue venue = null;
                    venue = getVenue(venueRepository, doc, id, venue);

                    Concert concert = concertRepository.findByBandAndDate(twiddle, date);
                    if (concert == null) {
                        concert = new Concert();
                        concert.setDate(date);
                        concert.setBand(twiddle);
                        if (venue != null) {
                            concert.setVenue(venue);
                        }
                        concertRepository.save(concert);
                    }

                    Elements songs = show.select("h2>a");
                    Iterator<Element> songIterator = songs.iterator();
                    int position = 1;
                    while (songIterator.hasNext()) {
                        Element songElement = songIterator.next();
                        String songName = songElement.text();

                        logger.info("Song=" + songName);

                        Song song = songRepository.findByBandAndName(twiddle, songName);
                        if (song == null) {
                            song = new Song();
                            song.setBand(twiddle);
                            song.setName(songName);
                            songRepository.save(song);
                        }

                        Performance performance = new Performance();
                        performance.setConcert(concert);
                        performance.setSong(song);
                        performance.setSongOrder(position++);
                        performanceRepository.save(performance);
                    }

                }
            }
        } catch (IOException|ParseException ioExc) {
            ioExc.printStackTrace();
        }
    }

    private Venue getVenue(VenueRepository venueRepository, Document doc, String id, Venue venue) {
        Elements venueElements = doc.select("a[data-popbox=" + id);

        if ((venueElements == null) || (venueElements.size()<=0)) return null;

        Element venueElement = venueElements.first();
        if (venueElement == null) return null;

        String myOrder = venueElement.attr("data-myorder");
        if ((myOrder == null) || (myOrder.indexOf(":: ")<0)) return null;

        String venueNameAndDate = myOrder.substring(myOrder.indexOf(":: ") + 3);


        StringTokenizer venueTok = new StringTokenizer(venueNameAndDate, ",");

        if (!venueTok.hasMoreTokens()) return null;
        String venueName = venueTok.nextToken().trim();

        String city = "";
        if (venueTok.hasMoreTokens()) {
            city = venueTok.nextToken().trim();
        }
        String state = "";
        if (venueTok.hasMoreTokens()) {
            state = venueTok.nextToken().trim();
        }

        logger.info("venue: " + venueName + " in the city of " + city + " in state " + state);
        venue = venueRepository.findByNameAndCityAndState(venueName, city, state);
        if (venue == null) {
            venue = new Venue();
            venue.setName(venueName);
            venue.setCity(city);
            venue.setState(state);
            venueRepository.save(venue);
        }
        return venue;
    }
}
