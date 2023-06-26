package gr.uoa.di.model;

import java.security.SecureRandom;
import java.util.Base64;

public class WebToken {

  private static final SecureRandom secureRandom = new SecureRandom(); //threadsafe
  private static final Base64.Encoder base64Encoder = Base64.getUrlEncoder(); //threadsafe
  private final String token;

  public WebToken() {
    byte[] randomBytes = new byte[24];
    secureRandom.nextBytes(randomBytes);
    this.token = base64Encoder.encodeToString(randomBytes);
  }

  public String getToken() {
    return token;
  }
}
