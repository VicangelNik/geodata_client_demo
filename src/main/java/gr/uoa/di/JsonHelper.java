package gr.uoa.di;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

/**
 * @author Nikiforos Xylogiannopoulos
 * Failed making field 'java.util.Optional#value' accessible; either increase its visibility or write a custom TypeAdapter for its declaring type.
 */
public class JsonHelper {

  private JsonHelper() {
    // should not be instantiated
  }

  public static final Gson gson = new GsonBuilder()
    .disableHtmlEscaping()
    .setDateFormat("yyyy-MM-dd HH:mm:ss")
    .registerTypeAdapterFactory(OptionalTypeAdapter.FACTORY)
    .registerTypeAdapterFactory(ListTypeAdapter.FACTORY)
    .create();

  public static class OptionalTypeAdapter<E> extends TypeAdapter<Optional<E>> {

    public static final TypeAdapterFactory FACTORY = new TypeAdapterFactory() {
      @Override
      public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
        Class<T> rawType = (Class<T>) type.getRawType();
        if (rawType != Optional.class) {
          return null;
        }
        final ParameterizedType parameterizedType = (ParameterizedType) type.getType();
        final Type actualType = parameterizedType.getActualTypeArguments()[0];
        final TypeAdapter<?> adapter = gson.getAdapter(TypeToken.get(actualType));
        return new OptionalTypeAdapter(adapter);
      }
    };
    private final TypeAdapter<E> adapter;

    public OptionalTypeAdapter(TypeAdapter<E> adapter) {
      this.adapter = adapter;
    }

    @Override
    public Optional<E> read(JsonReader in) throws IOException {
      if (in.peek() != JsonToken.NULL) {
        return Optional.ofNullable(adapter.read(in));
      } else {
        in.nextNull();
        return Optional.empty();
      }
    }

    @Override
    public void write(JsonWriter out, Optional<E> value) throws IOException {
      if (value != null && value.isPresent()) {
        adapter.write(out, value.get());
      } else {
        out.nullValue();
      }
    }
  }

  public static class ListTypeAdapter<E> extends TypeAdapter<List<E>> {

    public static final TypeAdapterFactory FACTORY = new TypeAdapterFactory() {
      @Override
      public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
        Class<T> rawType = (Class<T>) type.getRawType();
        if (rawType != List.class) {
          return null;
        }
        final ParameterizedType parameterizedType = (ParameterizedType) type.getType();
        final Type actualType = parameterizedType.getActualTypeArguments()[0];
        final TypeAdapter<?> adapter = gson.getAdapter(TypeToken.get(actualType));
        return new ListTypeAdapter(adapter);
      }
    };
    private final TypeAdapter<E> adapter;

    public ListTypeAdapter(TypeAdapter<E> adapter) {
      this.adapter = adapter;
    }

    @Override
    public List<E> read(JsonReader in) throws IOException {
      in.beginArray();
      ArrayList<E> ls = new ArrayList<>();
      while (in.peek() != JsonToken.END_ARRAY) {
        ls.add(adapter.read(in));
      }
      in.endArray();
      return ls;
    }

    @Override
    public void write(JsonWriter out, List<E> value) throws IOException {
      out.beginArray();
      if (value != null) {
        for (E e : value) {
          adapter.write(out, e);
        }
      }
      out.endArray();
    }
  }
}
