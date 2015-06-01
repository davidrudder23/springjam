package springjam.band;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BandRepository extends CrudRepository<Band, Long> {

	public Band findByName(String name);


    @Query("SELECT distinct(YEAR(date)) FROM Concert WHERE band=:band ORDER BY date")
    public List<Integer> getYearsPlayed(@Param("band") Band band);
}
