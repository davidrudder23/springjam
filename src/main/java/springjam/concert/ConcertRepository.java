package springjam.concert;

import org.springframework.data.repository.CrudRepository;
import springjam.band.Band;

import java.util.List;

public interface ConcertRepository extends CrudRepository<Concert, Long> {

    public List<Concert> findByBand(Band band);
}
