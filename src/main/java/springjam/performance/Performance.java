package springjam.performance;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import springjam.concert.Concert;
import springjam.song.Song;

import javax.persistence.*;

/**
 * Created by drig on 5/4/15.
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
@Entity
public class Performance {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private long id;

    @JsonBackReference()
    @ManyToOne
    private Concert concert;

    @ManyToOne
    private Song song;

    private int songOrder;

    private boolean goesInto;

    private String notes;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Concert getConcert() {
        return concert;
    }

    public void setConcert(Concert concert) {
        this.concert = concert;
    }

    public int getSongOrder() {
        return songOrder;
    }

    public void setSongOrder(int songOrder) {
        this.songOrder = songOrder;
    }

    public boolean isGoesInto() {
        return goesInto;
    }

    public void setGoesInto(boolean goesInto) {
        this.goesInto = goesInto;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Song getSong() {
        return song;
    }

    public void setSong(Song song) {
        this.song = song;
    }
}
