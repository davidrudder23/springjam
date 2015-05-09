package springjam.performance;

import org.springframework.data.repository.CrudRepository;
import springjam.concert.Concert;

import java.util.List;

public interface PerformanceRepository extends CrudRepository<Performance, Long> {

    public List<Performance> findByConcert(Concert concert);
}
