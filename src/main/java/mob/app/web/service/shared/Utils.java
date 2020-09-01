package mob.app.web.service.shared;

import java.security.SecureRandom;
import java.util.Random;

import org.springframework.stereotype.Component;

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
}