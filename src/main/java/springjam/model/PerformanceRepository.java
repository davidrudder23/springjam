package springjam.model;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface PerformanceRepository extends CrudRepository<Performance, Long> {

    public List<Performance> findByConcert(Concert concert);
}
