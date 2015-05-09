package springjam.band;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

public interface BandRepository extends CrudRepository<Band, Long> {
	
	public List<Band> findByName(String name);

}
