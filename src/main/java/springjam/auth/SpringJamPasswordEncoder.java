package springjam.auth;

import org.springframework.security.crypto.codec.Hex;
import org.springframework.stereotype.Service;
import org.springframework.security.authentication.encoding.PasswordEncoder;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by drig on 5/16/15.
 */
@Service
@SuppressWarnings("deprecation")
public class SpringJamPasswordEncoder implements PasswordEncoder {

    @Override
    public String encodePassword(String password, Object salt) {
        return encodePassword(password, effectiveSalt(salt), "SHA-256");
    }

    @Override
    public boolean isPasswordValid(String encPass, String rawPass, Object salt) {
        return encPass.equalsIgnoreCase(encodePassword(rawPass, salt));
    }

    private String effectiveSalt(Object salt) {
        if (salt == null) return "nosalt";
        return (String)salt;
    }

    private String encodePassword(String password, String salt, String type) {
        try {
            MessageDigest digest = MessageDigest.getInstance(type);

            digest.update(password.getBytes());
            //digest.update(":".getBytes());
            //digest.update(salt.getBytes());

            byte[] digestBytes = digest.digest();
            System.out.println("Encoding "+password+":"+salt);
            System.out.println("Encoded password="+new String(Hex.encode(digestBytes)));
            return new String(Hex.encode(digestBytes));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("The JVM doesn't know what " + type + " is, unable to continue");
        }
    }
}

