package mob.app.web.service.shared;

import java.security.SecureRandom;
import java.util.Date;
import java.util.Random;

import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import mob.app.web.service.security.SecurityConstants;

@Component
public class Utils {

  private final Random RANDOM = new SecureRandom();
  private final String ALPHABETH = "0123456789abcdefghijklmnopqrstuwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

  public String generateUserId(int length) {
    return generateRandomString(length);
  }

  public String generateAddressId(int length) {
    return generateRandomString(length);
  }

  private String generateRandomString(int length) {
    StringBuilder returnValue = new StringBuilder();

    for (int i = 0; i < length; i++) {
      returnValue.append(ALPHABETH.charAt(RANDOM.nextInt(ALPHABETH.length())));
    }

    return returnValue.toString();
  }

  public static boolean hasTokenExpired(String token) {

    boolean returnValue = false;

    try {
      Claims claims = Jwts.parser().setSigningKey(SecurityConstants.getTokenSecret()).parseClaimsJws(token).getBody();

      Date tokenExpirationDate = claims.getExpiration();
      Date todayDate = new Date();

      returnValue = tokenExpirationDate.before(todayDate);
    } catch (ExpiredJwtException ex) {
      returnValue = true;
    }

    return returnValue;
  }
}