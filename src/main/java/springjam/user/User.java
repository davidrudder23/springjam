package springjam.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.tomcat.util.buf.HexUtils;
import springjam.band.Band;
import springjam.concert.Concert;

import javax.persistence.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Entity
public class User {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    @JsonIgnore
    private long id;
    
    private String firstName;
    private String lastName;
    private String email;

    @JsonIgnore
    private String hashedPassword;

    @JsonIgnore
    private String salt;

    @ManyToMany(fetch = FetchType.EAGER)
    @JsonIgnore
    private List<Band> favoriteBands;

    @ManyToMany(fetch = FetchType.EAGER)
    @JsonIgnore
    private List<Concert> concerts;

    @Transient
    @JsonIgnore
    private String password;

    @Transient
    protected final Log logger = LogFactory.getLog(this.getClass());

    public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
    public String getHashedPassword() {
        return hashedPassword;
    }

    public void setHashedPassword(String hashedPassword) {
        this.hashedPassword = hashedPassword;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public String getSalt() {
        return salt;
    }

    public String getPassword() {
        return password;
    }

    public List<Band> getFavoriteBands() {
        return favoriteBands;
    }

    public void setFavoriteBands(List<Band> favoriteBands) {
        this.favoriteBands = favoriteBands;
    }

    public void setId(long id) {
        this.id = id;
    }

    public List<Concert> getConcerts() {
        return concerts;
    }

    public List<Concert> getConcerts(Band band) {
        logger.info("Getting concerts for band "+band);
        if (band == null) return concerts;

        List<Concert> bandsConcerts = concerts.stream()
                .filter(c->c.getBand().equals(band))
                .collect(Collectors.toList());

        return bandsConcerts;
    }

    public void setConcerts(List<Concert> concerts) {
        this.concerts = concerts;
    }

    public long getId() {
        return id;
    }

    public String toString() {
        return email+" <"+firstName+" "+lastName+">";
    }

    public void removeConcert(Concert concert) {
        if (concerts == null) return;
        concerts.remove(concert);
    }

    public void addConcert(Concert concert) {
        if (concerts == null) concerts = new ArrayList<Concert>();
        if (concert == null) return;

        boolean found = concerts.stream().filter(c->c.equals(concert)).findAny().isPresent();

        if (!found) {
            concerts.add(concert);
        }
    }

    public void setPassword(String password, String salt) {
        logger.info("Encoding password "+password);
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(password.getBytes());
            String hashedPassword = HexUtils.toHexString(md.digest());
            setHashedPassword(hashedPassword);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    public void generateSalt(int size) {
        char[] alphabet = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k',
                'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};

        String salt = "";
        try {

            byte[] seed = (""+System.currentTimeMillis()).getBytes();
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
            random.setSeed(seed);
            for (int i = 0; i < size; i++) {
                salt += alphabet[Math.abs(random.nextInt()%26)]+"";
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        this.salt = salt;
    }

}
