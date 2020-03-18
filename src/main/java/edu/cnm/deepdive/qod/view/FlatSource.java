package edu.cnm.deepdive.qod.view;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.net.URI;
import java.util.Date;
import java.util.UUID;
import org.springframework.lang.NonNull;

@JsonPropertyOrder({"id", "created", "updated", "name", "href"})
public interface FlatSource {

  UUID getId();

  Date getCreated();

  Date getUpdated();

  String getName();

  URI getHref();

}
