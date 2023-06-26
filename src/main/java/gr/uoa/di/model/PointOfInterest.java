package gr.uoa.di.model;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class PointOfInterest {

  private long timestampAdded; // unix epoch
  private String title;
  private Optional<String> description;
  private double latitude;
  private double longitude;
  private Optional<List<String>> keywords;
  private Optional<List<String>> categories;

  public void setTimestampAdded(long timestampAdded) {
    this.timestampAdded = timestampAdded;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public void setDescription(Optional<String> description) {
    this.description = description;
  }

  public void setLatitude(double latitude) {
    this.latitude = latitude;
  }

  public void setLongitude(double longitude) {
    this.longitude = longitude;
  }

  public void setKeywords(Optional<List<String>> keywords) {
    this.keywords = keywords;
  }

  public void setCategories(Optional<List<String>> categories) {
    this.categories = categories;
  }

  public long getTimestampAdded() {
    return timestampAdded;
  }

  public String getTitle() {
    return title;
  }

  public Optional<String> getDescription() {
    return description;
  }

  public double getLatitude() {
    return latitude;
  }

  public double getLongitude() {
    return longitude;
  }

  public Optional<List<String>> getKeywords() {
    return keywords;
  }

  public Optional<List<String>> getCategories() {
    return categories;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof PointOfInterest that)) return false;
    return timestampAdded == that.timestampAdded && Double.compare(that.latitude, latitude) == 0
           && Double.compare(that.longitude, longitude) == 0 && Objects.equals(title, that.title)
           && Objects.equals(description, that.description) && Objects.equals(keywords, that.keywords)
           && Objects.equals(categories, that.categories);
  }

  @Override
  public int hashCode() {
    return Objects.hash(timestampAdded, title, description, latitude, longitude, keywords, categories);
  }
}
