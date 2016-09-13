package gq.baijie.onetab;

import java.util.ArrayList;
import java.util.Date;
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

    private String id;

    private Date createDate;

    private List<Item> items;

    public String getId() {
      return id;
    }

    Section setId(String id) {
      this.id = id;
      return this;
    }

    public Date getCreateDate() {
      return createDate;
    }

    Section setCreateDate(Date createDate) {
      this.createDate = createDate;
      return this;
    }

    public List<Item> getItems() {
      return items;
    }

    Section setItems(List<Item> items) {
      this.items = items;
      return this;
    }

  }

  public static class Item {

    private String id;
    private String link;
    private String title;

    void setId(String id) {
      this.id = id;
    }

    public String getId() {
      return id;
    }

    void setLink(String link) {
      this.link = link;
    }

    public String getLink() {
      return link;
    }

    void setTitle(String title) {
      this.title = title;
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

    private String id;

    private Date createDate;

    final List<ItemBuilder> itemBuilders = new LinkedList<>();

    public SectionBuilder setId(String id) {
      this.id = id;
      return this;
    }

    public SectionBuilder setCreateDate(Date createDate) {
      this.createDate = createDate;
      return this;
    }

    public ItemBuilder item() {
      final ItemBuilder newItem = new ItemBuilder();
      itemBuilders.add(newItem);
      return newItem;
    }

    public Section build() {
      final List<Item> items = new ArrayList<>(itemBuilders.size());
      itemBuilders.forEach(i -> items.add(i.build()));
      final Section result = new Section();
      result.setId(id);
      result.setCreateDate(createDate);
      result.setItems(items);
      return result;
    }

  }

  public static class ItemBuilder {

    private String id;
    private String link;
    private String title;

    public ItemBuilder setId(String id) {
      this.id = id;
      return this;
    }

    public ItemBuilder setLink(String link) {
      this.link = link;
      return this;
    }

    public ItemBuilder setTitle(String title) {
      this.title = title;
      return this;
    }

    public Item build() {
      final Item result = new Item();
      result.setId(id);
      result.setLink(link);
      result.setTitle(title);
      return result;
    }
  }

}
