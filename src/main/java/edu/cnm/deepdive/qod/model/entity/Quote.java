package edu.cnm.deepdive.qod.model.entity;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import edu.cnm.deepdive.qod.view.FlatQuote;
import edu.cnm.deepdive.qod.view.FlatSource;
import java.net.URI;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;
import javax.annotation.PostConstruct;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.server.EntityLinks;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Component
@Entity
@Table(
    indexes = {
        @Index(columnList = "created"),
        @Index(columnList = "text"),
        @Index(columnList = "source_id, text", unique = true)
    }
)
public class Quote implements FlatQuote {

  private static EntityLinks entityLinks;

  @NonNull
  @Id@GeneratedValue(generator = "uuid2")
  @GenericGenerator(name = "uuid2", strategy = "uuid2")
  @Column(name = "quote_id", columnDefinition = "CHAR(16) FOR BIT DATA",
      nullable = false, updatable = false)
  private UUID id;

  @NonNull
  @CreationTimestamp
  @Temporal(TemporalType.TIMESTAMP)
  @Column(nullable = false, updatable = false)
  private Date created;

  @NonNull
  @UpdateTimestamp
  @Temporal(TemporalType.TIMESTAMP)
  @Column(nullable = false)
  private Date updated;

  @NonNull
  @Column(length = 4096, nullable = false)
  private String text;

  @ManyToOne(fetch = FetchType.EAGER,
      cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH}) // CascadeType.PERSIST lets a JSON post write both quote & source
  @JoinColumn(name = "source_id")
  @JsonSerialize(as = FlatSource.class)
  private Source source;

  @Override
  public UUID getId() {
    return id;
  }

  @Override
  public Date getCreated() {
    return created;
  }

  @Override
  public Date getUpdated() {
    return updated;
  }

  @Override
  public String getText() {
    return text;
  }

  public void setText(@NonNull String text) {
    this.text = text;
  }

  public Source getSource() {
    return source;
  }

  public void setSource(Source source) {
    this.source = source;
  }

  @Override
  public URI getHref() {
    return entityLinks.linkForItemResource(Quote.class, id).toUri();
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, text); // TODO Compute lazily & cache.
  }

  @Override
  public boolean equals(Object obj) {
    boolean result = false;
    if (obj == this) {
      result = true;
    } else if (obj instanceof Quote && obj.hashCode() == hashCode()) {
      Quote other = (Quote) obj;
      result = id.equals(other.id) && text.equals(other.text);
    }
    return result;
  }

  @PostConstruct
  private void init() {
    entityLinks.toString();
  }

  @Autowired
  private void setEntityLinks(EntityLinks entityLinks) {
    Quote.entityLinks = entityLinks;
  }

}
