package springjam.band;

import org.springframework.data.repository.CrudRepository;

public interface BandRepository extends CrudRepository<Band, Long> {
	
	public Band findByName(String name);

}
