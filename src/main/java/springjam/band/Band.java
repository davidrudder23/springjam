package springjam.band;

import javax.persistence.*;
import java.util.List;

@Entity
public class Band {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private long id;
    
    String name;

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
		return name;
	}
	
}
