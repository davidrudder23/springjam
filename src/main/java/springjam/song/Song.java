package springjam.song;

import com.fasterxml.jackson.annotation.JsonIgnore;
import springjam.performance.Performance;
import springjam.band.Band;

import javax.persistence.*;
import java.util.List;

/**
 * Created by drig on 5/4/15.
 */
@Entity
public class Song {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private long id;

    private String name;

    @ManyToOne
    private Band band;

    @JsonIgnore
    @OneToMany(mappedBy = "song", fetch = FetchType.LAZY)
    List<Performance> performances;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }


    public Band getBand() {
        return band;
    }

    public void setBand(Band band) {
        this.band = band;
    }

    public List<Performance> getPerformances() {
        return performances;
    }

    public void setPerformances(List<Performance> performances) {
        this.performances = performances;
    }
}
