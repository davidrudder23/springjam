package springjam.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

/**
 * Created by drig on 5/30/15.
 */
public class GratefulDeadDownloader {

    protected final Log logger = LogFactory.getLog(this.getClass());

    private static final String downloadDir = "/home/drig/grateful_dead_shows/www.cs.cmu.edu/~./gdead/dead-sets";

    public GratefulDeadDownloader() {

    }

    private String getNextLine(BufferedReader in) throws IOException {
        String line = "";
        while ((line != null) && (line.equals(""))) {
            line = in.readLine();
        }

        return line;
    }

    private Optional<Date> getDateFromFilename(String filename) {
        SimpleDateFormat sdf = new SimpleDateFormat("mm-dd-yy");
        if (filename.endsWith(".txt")) {
            filename = filename.replaceAll(".txt", "");
            logger.info("filename without extension="+filename);
        }

        try {
            Date date = sdf.parse(filename);
            return Optional.of(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return Optional.empty();
    }

    private Optional<Venue> getVenueFromTitle(String title, VenueRepository venueRepository) {
        if (!title.contains(",")) {
            return Optional.empty();
        }

        String venueName = title.substring(0, title.indexOf(","));
        logger.info("venue name="+venueName);

        title = title.substring(title.indexOf(",")+1, title.length()).trim();
        logger.info("without venue name="+title);

        if (title.indexOf("(")>0) {
            title = title.substring(0, title.indexOf("(")).trim();
        }
        logger.info("without date="+title);

        String city = title;
        String state = "";
        if (title.indexOf(",")>0) {
            city = title.substring(0, title.indexOf(",")).trim();
            state = title.substring(title.indexOf(",")+1, title.length()).trim();
        }

        logger.info ("name="+venueName+" city="+city+" state="+state);

        Venue venue = venueRepository.findByNameAndCityAndState(venueName, city, state);
        if (venue == null) {
            venue = venueRepository.findByName(venueName);
        }

        if (venue == null) {
            venue = new Venue();
            venue.setName(venueName);
            venue.setCity(city);
            venue.setState(state);
            venueRepository.save(venue);
        }

        return Optional.of(venue);
    }

    private Optional<Song> getSong(Band band, String songName, SongRepository songRepository) {
        if ((songName == null) || (songName.trim().length()==0)) {
            return Optional.empty();
        }

        logger.info("raw song name="+songName);

        songName = songName.replaceAll("->", "")
                .replaceAll("[\\(\\)]", "")
                .replaceAll("E:", "")
                .replaceAll("\\*", "")
                .trim();

        Song song = songRepository.findByBandAndName(band, songName);
        if (song == null) {
            song = new Song();
            song.setBand(band);
            song.setName(songName);
            songRepository.save(song);
        }
        return Optional.of(song);
    }

    public void download(BandRepository bandRepository,
                         SongRepository songRepository,
                         PerformanceRepository performanceRepository,
                         ConcertRepository concertRepository,
                         VenueRepository venueRepository) {
        Band band = bandRepository.findByName("Grateful Dead");
        if (band == null) {
            band = new Band();
            band.setName("Grateful Dead");
            bandRepository.save(band);
        }

        for (int year = 72; year <= 95; year++) {
            File directory = new File(downloadDir+"/"+year);

            File[] files = directory.listFiles();
            for (int fileNum = 0; fileNum < files.length; fileNum++) {
                try {
                    File file = files[fileNum];
                    Optional<Date> optionalDate = getDateFromFilename(file.getName());
                    if (!optionalDate.isPresent()) continue;

                    Date date = optionalDate.get();

                    BufferedReader in = new BufferedReader(new FileReader(file));
                    String title = in.readLine();

                    Optional<Venue> optionalVenue = getVenueFromTitle(title, venueRepository);
                    if (!optionalVenue.isPresent()) continue;

                    Venue venue = optionalVenue.get();

                    Concert concert = concertRepository.findByBandAndDate(band, date);
                    if (concert == null) {
                        concert = new Concert();
                        concert.setDate(date);
                        concert.setBand(band);
                        if (venue != null) {
                            concert.setVenue(venue);
                        }
                        concertRepository.save(concert);
                    }

                    String songTitle = getNextLine(in);

                    int position = 1;
                    while (songTitle != null) {
                        songTitle = songTitle.trim();
                        if ((songTitle.startsWith("*")) || (songTitle.endsWith("*")
                                || (songTitle.startsWith("+"))) || (songTitle.endsWith("+"))
                        ) {
                            logger.info("Skipping song "+songTitle+" because it starts with a star");
                            songTitle = getNextLine(in);
                            continue;
                        }

                        Optional<Song> optionalSong = getSong(band, songTitle, songRepository);
                        if (!optionalSong.isPresent()) {
                            logger.warn("Could not find song with title "+songTitle);
                        } else {

                            Performance performance = new Performance();
                            performance.setConcert(concert);
                            performance.setSong(optionalSong.get());
                            performance.setSongOrder(position++);
                            performanceRepository.save(performance);
                        }
                        songTitle = getNextLine(in);
                    }
                } catch (Exception anyExc) {
                    anyExc.printStackTrace();
                }
            }
        }
    }
}
