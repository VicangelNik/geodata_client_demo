package gr.uoa.di.model;

import java.util.Objects;

public class DataResult {

  private PointOfInterest pointOfInterest;
  private Category category;

  public PointOfInterest getPointOfInterest() {
    return pointOfInterest;
  }

  public void setPointOfInterest(PointOfInterest pointOfInterest) {
    this.pointOfInterest = pointOfInterest;
  }

  public Category getCategory() {
    return category;
  }

  public void setCategory(Category category) {
    this.category = category;
  }

  public static DataResult createExampleValue() {
    final var dataResult = new DataResult();
    Category category = new Category();
    category.setId(1);
    category.setName("cat1");
    dataResult.setCategory(category);
    PointOfInterest pointOfInterest = new PointOfInterest();
    pointOfInterest.setLatitude(0.243243232432);
    pointOfInterest.setLongitude(0.243243232432);
    pointOfInterest.setTitle("PoisExample");
    pointOfInterest.setTimestampAdded(2345436445L);
    dataResult.setPointOfInterest(pointOfInterest);
    return dataResult;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof DataResult that)) return false;
    return Objects.equals(pointOfInterest, that.pointOfInterest) && Objects.equals(category, that.category);
  }

  @Override
  public int hashCode() {
    return Objects.hash(pointOfInterest, category);
  }
}
