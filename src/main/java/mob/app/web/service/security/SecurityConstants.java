package mob.app.web.service.security;

import mob.app.web.service.SpringApplicationContext;

public class SecurityConstants {

  public static final long EXPIRATION_TIME = 8640000000L; // 10 days ni miliseconds
  public static final String TOKEN_PREFIX = "Bearer ";
  public static final String HEADER_STRING = "Authorization";
  public static final String SIGN_UP_URL = "/users";

  public static String getTokenSecret() {
    AppProperties appProperties = (AppProperties) SpringApplicationContext.getBean("appProperties");

    return appProperties.getTokenSecret();
  }
}