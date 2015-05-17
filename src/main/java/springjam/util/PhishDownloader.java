package springjam.util;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.springframework.http.*;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.client.HttpMessageConverterExtractor;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.RestTemplate;
import springjam.band.Band;
import springjam.band.BandRepository;
import springjam.concert.Concert;
import springjam.concert.ConcertRepository;
import springjam.performance.Performance;
import springjam.performance.PerformanceRepository;
import springjam.song.Song;
import springjam.song.SongRepository;
import springjam.user.UserRepository;
import springjam.venue.Venue;
import springjam.venue.VenueRepository;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by drig on 5/9/15.
 */

public class PhishDownloader {
    BandRepository bandRepository;
    UserRepository userRepository;
    ConcertRepository concertRepository;
    VenueRepository venueRepository;
    PerformanceRepository performanceRepository;
    SongRepository songRepository;

    String date;

    public PhishDownloader() {
    }

    public BandRepository getBandRepository() {
        return bandRepository;
    }

    public void setBandRepository(BandRepository bandRepository) {
        this.bandRepository = bandRepository;
    }

    public UserRepository getUserRepository() {
        return userRepository;
    }

    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public ConcertRepository getConcertRepository() {
        return concertRepository;
    }

    public void setConcertRepository(ConcertRepository concertRepository) {
        this.concertRepository = concertRepository;
    }

    public VenueRepository getVenueRepository() {
        return venueRepository;
    }

    public void setVenueRepository(VenueRepository venueRepository) {
        this.venueRepository = venueRepository;
    }

    public PerformanceRepository getPerformanceRepository() {
        return performanceRepository;
    }

    public void setPerformanceRepository(PerformanceRepository performanceRepository) {
        this.performanceRepository = performanceRepository;
    }

    public SongRepository getSongRepository() {
        return songRepository;
    }

    public void setSongRepository(SongRepository songRepository) {
        this.songRepository = songRepository;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void importShow() {
        Band band = bandRepository.findByName("Phish");
        if (band == null) {
            band = new Band();
            band.setName("Phish");
            bandRepository.save(band);
        }

        try {
            SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd");
            Date concertDate = parser.parse(date);
            if (concertRepository.findByBandAndDate(band, concertDate) != null) {
                System.out.println ("This concert has already been loaded");
                return;
            }

        } catch (ParseException parseExc) {
            parseExc.printStackTrace();
            return;
        }

        RestTemplate restTemplate = new RestTemplate();
        Show show = restTemplate.execute("http://www.phishtracks.com/api/v1/shows/"+date, HttpMethod.GET, new MyRequestCallback(),
                new MyResponseExtractor(Show.class, restTemplate.getMessageConverters()));

        Venue venue = venueRepository.findByName(show.getLocation());
        if (venue == null) {
            venue = new Venue();
            venue.setName(show.getLocation());
            venueRepository.save(venue);
        }

        Concert concert = new Concert();
        concert.setBand(band);
        concert.setVenue(venue);
        concert.setDate(show.getDate());
        concertRepository.save(concert);

        for (Set set: show.getSets()) {
            for (Track track: set.getTracks()) {
                Song song = songRepository.findByBandAndName(band, track.getTitle());
                if (song == null) {
                    song = new Song();
                    song.setName(track.getTitle());
                    song.setBand(band);
                    songRepository.save(song);
                }

                Performance performance = new Performance();
                performance.setConcert(concert);
                performance.setSong(song);
                performance.setSongOrder(track.getPosition());
                performanceRepository.save(performance);
                System.out.println (track.getTitle());
            }
        }

    }
}

class MyRequestCallback implements RequestCallback {
    @Override
    public void doWithRequest(ClientHttpRequest request) throws IOException {
        request.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        List<MediaType> accepts = new ArrayList<MediaType>();
        accepts.add(MediaType.APPLICATION_JSON);
        request.getHeaders().setAccept(accepts);
    }
}

class Show {
    private String location;
    private Date date;

    private List<Set> sets;

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Date getDate() {
        return date;
    }

    @JsonDeserialize(using=DateDeserializer.class)
    @JsonProperty("show_date")
    public void setDate(Date date) {
        this.date = date;
    }

    public List<Set> getSets() {
        return sets;
    }

    public void setSets(List<Set> sets) {
        this.sets = sets;
    }
}

class DateDeserializer extends JsonDeserializer<Date> {
    @Override
    public Date deserialize(JsonParser jsonParser, DeserializationContext context) throws IOException, JsonProcessingException {

        System.out.println("Parsing date "+jsonParser.getText());

        SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd");

        try {
            Date date = parser.parse(jsonParser.getText());
            return date;
        } catch (ParseException parseExc) {
            parseExc.printStackTrace();;
        }

        return null;

    }
}
class Set {
    private String title;

    private List<Track> tracks;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<Track> getTracks() {
        return tracks;
    }

    public void setTracks(List<Track> tracks) {
        this.tracks = tracks;
    }
}

class Track {
    private String title;
    private int position;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}

class MyResponseExtractor extends HttpMessageConverterExtractor<Show> {

    public MyResponseExtractor (Class<Show> responseType,
                                List<HttpMessageConverter<?>> messageConverters) {
        super(responseType, messageConverters);
    }

    @Override
    public Show extractData(ClientHttpResponse response) throws IOException {

        Show result;

        System.out.println("Got status code "+response.getStatusCode());
        if (response.getStatusCode() == HttpStatus.OK) {
            response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
            result = super.extractData(response);
        } else {
            result = null;
        }

        return result;
    }
}
