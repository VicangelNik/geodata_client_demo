package gr.uoa.di;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.math.BigInteger;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublisher;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.function.Function;
import java.util.function.Supplier;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import com.google.gson.Gson;
import gr.uoa.di.error.ConnectionException;
import gr.uoa.di.error.ResponseProcessingException;
import gr.uoa.di.error.ServerResponseException;
import gr.uoa.di.model.Credentials;
import gr.uoa.di.model.DataResult;
import gr.uoa.di.model.SearchInput;
import gr.uoa.di.model.SearchResponse;

public class RestAPI {

  public static final String BASE_URL = "/demo/";
  private static final String CONTENT_TYPE_HEADER = "Content-Type";
  private static final String MEDIA_TYPE_JSON = "application/json";
  public static final String CUSTOM_HEADER = "x-api-token";
  private String token;
  private final String urlPrefix;
  private final HttpClient client;
  private static final TrustManager[] trustAllCerts = new TrustManager[]{
    new X509TrustManager() {
      public java.security.cert.X509Certificate[] getAcceptedIssuers() {
        return null;
      }

      public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) {
      }

      public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) {
      }
    }
  };

  static {
    System.setProperty("jdk.internal.httpclient.disableHostnameVerification", "true");
  }

  public RestAPI(String token) {
    this("localhost", 8080, token);
  }

  public RestAPI(String host, int port, String token) {
    try {
      this.client = newHttpClient();
    } catch (NoSuchAlgorithmException | KeyManagementException e) {
      throw new RuntimeException(e.getMessage());
    }
    this.urlPrefix = "https://" + host + ":" + port + BASE_URL;
    this.token = token;
  }

  public String login(String username, String password) {

    final var credentials = new Credentials(username, password);
    final String jsonBody = new Gson().toJson(credentials);

    HttpRequest request = HttpRequest.newBuilder()
      .uri(URI.create(getLoginUrl()))
      .header(CONTENT_TYPE_HEADER, MEDIA_TYPE_JSON).POST(BodyPublishers.ofString(jsonBody, StandardCharsets.UTF_8))
      .build();

    token = sendRequestAndParseResponseBodyAsUTF8Text(
      () -> request,
      ClientHelper::parseJsonToken
    );

    return token;
  }

  public boolean isLoggedIn() {
    return token != null;
  }

  public DataResult importPoisCategories(File file) throws IOException {

    String boundary = new BigInteger(256, new Random()).toString();

    Map<Object, Object> data = new HashMap<>();

    data.put("csvFile", file.toPath());

    HttpRequest request = HttpRequest.newBuilder()
      .uri(URI.create(getImportUrl()))
      .header(CONTENT_TYPE_HEADER, "multipart/form-data;boundary=" + boundary)
      .header(CUSTOM_HEADER, token).POST(oMultipartData(data, boundary))
      .build();

    return sendRequestAndParseResponseBodyAsUTF8Text(
      () -> request,
      ClientHelper::parseDataResult
    );
  }

  public SearchResponse searchPoisCategories(SearchInput searchInput) {

    final String jsonBody = new Gson().toJson(searchInput);

    HttpRequest request = HttpRequest.newBuilder()
      .uri(URI.create(getSearchUrl()))
      .header(CONTENT_TYPE_HEADER, MEDIA_TYPE_JSON)
      .header(CUSTOM_HEADER, token)
      .POST(BodyPublishers.ofString(jsonBody, StandardCharsets.UTF_8))
      .build();

    return sendRequestAndParseResponseBodyAsUTF8Text(
      () -> request,
      ClientHelper::parseSearchResponse
    );
  }

  public static BodyPublisher oMultipartData(Map<Object, Object> data,
                                             String boundary) throws IOException {
    var byteArrays = new ArrayList<byte[]>();
    byte[] separator = ("--" + boundary
                        + "\r\nContent-Disposition: form-data; name=")
      .getBytes(StandardCharsets.UTF_8);
    for (Map.Entry<Object, Object> entry : data.entrySet()) {
      byteArrays.add(separator);

      if (entry.getValue() instanceof Path) {
        var path = (Path) entry.getValue();
        String mimeType = Files.probeContentType(path);
        byteArrays.add(("\"" + entry.getKey() + "\"; filename=\""
                        + path.getFileName() + "\"\r\nContent-Type: " + mimeType
                        + "\r\n\r\n").getBytes(StandardCharsets.UTF_8));
        byteArrays.add(Files.readAllBytes(path));
        byteArrays.add("\r\n".getBytes(StandardCharsets.UTF_8));
      } else {
        byteArrays.add(
          ("\"" + entry.getKey() + "\"\r\n\r\n" + entry.getValue()
           + "\r\n").getBytes(StandardCharsets.UTF_8));
      }
    }
    byteArrays
      .add(("--" + boundary + "--").getBytes(StandardCharsets.UTF_8));
    return BodyPublishers.ofByteArrays(byteArrays);
  }

  private <T> T sendRequestAndParseResponseBodyAsUTF8Text(Supplier<HttpRequest> requestSupplier,
                                                          Function<Reader, T> bodyProcessor) {

    HttpRequest request = requestSupplier.get();

    try {
      System.out.println("Sending " + request.method() + " to " + request.uri());
      HttpResponse<InputStream> response = client.send(request, HttpResponse.BodyHandlers.ofInputStream());
      int statusCode = response.statusCode();
      if (statusCode == 200) {
        try {
          if (bodyProcessor != null) {
            return bodyProcessor.apply(new InputStreamReader(response.body(), StandardCharsets.UTF_8));
          } else {
            return null;
          }
        } catch (Exception e) {
          throw new ResponseProcessingException(e.getMessage(), e);
        }
      } else {
        throw new ServerResponseException(statusCode, ClientHelper.readContents(response.body()));
      }
    } catch (IOException | InterruptedException e) {
      throw new ConnectionException(e.getMessage(), e);
    }
  }

  private String getLoginUrl() {
    return urlPrefix + "login";
  }

  private String getImportUrl() {
    return urlPrefix + "import";
  }
  private String getSearchUrl() {
    return urlPrefix + "search/pois";
  }

  /**
   * Helper method to create a new http client that can tolerate self-signed or improper ssl certificates.
   */
  private static HttpClient newHttpClient() throws NoSuchAlgorithmException, KeyManagementException {

    SSLContext sslContext = SSLContext.getInstance("TLS");
    sslContext.init(null, trustAllCerts, new SecureRandom());

    return HttpClient.newBuilder().sslContext(sslContext).build();
  }
}
