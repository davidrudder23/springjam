package springjam.band;

import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import springjam.concert.Concert;

import javax.persistence.*;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@Configurable
@Entity
public class Band {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private long id;
    
    String name;

    @JsonIgnore
    @OneToMany(mappedBy = "band")
    List<Concert> concerts;

    public Band() { }
    
	public Band(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
		return id;
	}

	public String toString() {
		return "#"+id+" - "+name;
	}

    public boolean equals(Object other) {
        if (!(other instanceof Band)) return false;

        return id == ((Band)other).getId();
    }
	
}
