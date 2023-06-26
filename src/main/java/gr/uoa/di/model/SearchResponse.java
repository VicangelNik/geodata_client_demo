package gr.uoa.di.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SearchResponse {

  private int start;
  private int count;
  private int total;
  private List<PointOfInterest> data;

  public int getStart() {
    return start;
  }

  public void setStart(int start) {
    this.start = start;
  }

  public int getCount() {
    return count;
  }

  public void setCount(int count) {
    this.count = count;
  }

  public int getTotal() {
    return total;
  }

  public void setTotal(int total) {
    this.total = total;
  }

  public List<PointOfInterest> getData() {
    return data;
  }

  public void setData(List<PointOfInterest> data) {
    this.data = data;
  }

  public static SearchResponse createExampleValue() {
    final var searchResponse = new SearchResponse();
    searchResponse.setCount(5);
    searchResponse.setStart(1);
    searchResponse.setTotal(2);
    searchResponse.setData(new ArrayList<>());
    return searchResponse;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof SearchResponse that)) return false;
    return start == that.start && count == that.count && total == that.total && Objects.equals(data, that.data);
  }

  @Override
  public int hashCode() {
    return Objects.hash(start, count, total, data);
  }
}
