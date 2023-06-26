package gr.uoa.di.model;

public class SearchInput {

  private int start;
  private int count;
  private String freeText;
  private Filters filters;

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

  public String getFreeText() {
    return freeText;
  }

  public void setFreeText(String freeText) {
    this.freeText = freeText;
  }

  public Filters getFilters() {
    return filters;
  }

  public void setFilters(Filters filters) {
    this.filters = filters;
  }

  public static SearchInput createExampleValue() {
    SearchInput searchInput = new SearchInput();

    Filters filters = new Filters();
    searchInput.setFilters(filters);
    searchInput.setCount(5);
    searchInput.setFreeText("freeText");
    searchInput.setStart(0);

    return searchInput;
  }
}
