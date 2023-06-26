package gr.uoa.di;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import gr.uoa.di.model.DataResult;
import gr.uoa.di.model.SearchResponse;

public class ClientHelper {

  private ClientHelper() {
    // do not instantiate this class
  }

  static String parseJsonToken(Reader reader) {

    return parseJsonAndGetValueOfField(reader, "token");
  }

  private static String parseJsonAndGetValueOfField(Reader reader, String field) {

    Gson gson = new Gson();
    Map map = gson.fromJson(reader, Map.class);

    return (String) map.get(field);
  }

  static String readContents(InputStream inputStream) {
    return new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8)).
      lines().collect(Collectors.joining("\n"));
  }

  static DataResult parseDataResult(Reader reader) {
    return JsonHelper.gson.fromJson(reader, DataResult.class);
  }

  public static SearchResponse parseSearchResponse(Reader reader) {
    return JsonHelper.gson.fromJson(reader, SearchResponse.class);
  }
}