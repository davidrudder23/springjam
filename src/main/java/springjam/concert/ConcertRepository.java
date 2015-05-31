package springjam.concert;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import springjam.band.Band;

import java.util.Date;
import java.util.List;

public interface ConcertRepository extends CrudRepository<Concert, Long> {

    public List<Concert> findByBand(Band band);

    @Query("SELECT c FROM Concert c WHERE band=:band AND YEAR(date)=:year")
    public List<Concert> findByBandAndYear(@Param("band") Band band, @Param("year") int year);

    public Concert findByBandAndDate(Band band, Date date);
}
