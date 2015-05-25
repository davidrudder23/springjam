package springjam.venue;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface VenueRepository extends CrudRepository<Venue, Long> {

    public Venue findByName(String name);
    public List<Venue> findByState(String state);

    public Venue findByNameAndCityAndState(String name, String city, String state);

}
