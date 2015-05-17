package springjam.auth;

import org.apache.commons.codec.binary.Hex;
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
        return encodePassword(password, effectiveSalt(salt), "SHA-1");
    }

    @Override
    public boolean isPasswordValid(String encPass, String rawPass, Object salt) {
        return encPass.equalsIgnoreCase(encodePassword(rawPass, salt))
                || encPass.equalsIgnoreCase(encodePassword(rawPass, effectiveSalt(salt), "SHA-512"));
    }

    private String effectiveSalt(Object salt) {
        // salt can be null when dummy operations are performed - spring security runs password checks even
        // when a principal is not found to prevent timing attacks.
        return salt != null ? (String) salt : "placeholder";
    }

    private String encodePassword(String password, String salt, String type) {
        try {
            MessageDigest digest = MessageDigest.getInstance(type);

            digest.update(password.getBytes());
            digest.update(":".getBytes());
            digest.update(salt.getBytes());

            return new String(Hex.encodeHex(digest.digest()));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("The JVM doesn't know what " + type + " is, unable to continue");
        }
    }
}

