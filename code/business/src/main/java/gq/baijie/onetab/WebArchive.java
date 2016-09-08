package gq.baijie.onetab;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.Nonnull;

public class WebArchive {

  private List<Section> sections;

  @Nonnull
  public static WebArchiveBuilder builder() {
    return new WebArchiveBuilder();
  }

  public WebArchive(List<Section> sections) {
    this.sections = sections;
  }

  public List<Section> getSections() {
    return sections;
  }

  public static class Section {
    private List<Item> items;

    public Section(List<Item> items) {
      this.items = items;
    }

    public List<Item> getItems() {
      return items;
    }

  }

  public static class Item {
    private String link;
    private String title;

    public Item(String link, String title) {
      this.link = link;
      this.title = title;
    }

    public String getLink() {
      return link;
    }

    public String getTitle() {
      return title;
    }

  }

  public static class WebArchiveBuilder {
    final List<SectionBuilder> sectionBuilders = new LinkedList<>();

    public SectionBuilder section() {
      final SectionBuilder newSection = new SectionBuilder();
      sectionBuilders.add(newSection);
      return newSection;
    }

    public WebArchive build() {
      final List<Section> sections = new ArrayList<>(sectionBuilders.size());
      sectionBuilders.forEach(s->sections.add(s.build()));
      return new WebArchive(sections);
    }
  }

  public static class SectionBuilder {
    final List<ItemBuilder> itemBuilders = new LinkedList<>();

    public ItemBuilder item() {
      final ItemBuilder newItem = new ItemBuilder();
      itemBuilders.add(newItem);
      return newItem;
    }

    public Section build() {
      final List<Item> items = new ArrayList<>(itemBuilders.size());
      itemBuilders.forEach(i -> items.add(i.build()));
      return new Section(items);
    }

  }

  public static class ItemBuilder {
    private String link;
    private String title;

    public ItemBuilder setLink(String link) {
      this.link = link;
      return this;
    }

    public ItemBuilder setTitle(String title) {
      this.title = title;
      return this;
    }

    public Item build() {
      return new Item(link, title);
    }
  }

}
