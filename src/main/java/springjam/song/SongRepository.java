package springjam.song;

import org.springframework.data.repository.CrudRepository;
import springjam.band.Band;

import java.util.List;

public interface SongRepository extends CrudRepository<Song, Long> {
	
	public List<Song> findByName(String name);

    public List<Song> findByBand(Band band);

    public Song findByBandAndName(Band band, String name);

}
