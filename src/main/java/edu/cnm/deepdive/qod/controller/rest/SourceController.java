package edu.cnm.deepdive.qod.controller.rest;

import edu.cnm.deepdive.qod.controller.exception.SearchTermTooShortException;
import edu.cnm.deepdive.qod.model.entity.Quote;
import edu.cnm.deepdive.qod.model.entity.Source;
import edu.cnm.deepdive.qod.service.QuoteRepository;
import edu.cnm.deepdive.qod.service.SourceRepository;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.server.ExposesResourceFor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sources")
@ExposesResourceFor(Source.class)
public class SourceController {

  private final SourceRepository sourceRepository;
  private final QuoteRepository quoteRepository;

  @Autowired
  public SourceController(SourceRepository sourceRepository,
      QuoteRepository quoteRepository) {
    this.sourceRepository = sourceRepository;
    this.quoteRepository = quoteRepository;
  }

  @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Source> post(@RequestBody Source source) {
    sourceRepository.save(source);
    return ResponseEntity.created(source.getHref()).body(source);
  }

  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  public Iterable<Source> get(
      @RequestParam(required = false, defaultValue = "false") boolean includeNull) {
    if (includeNull) {
      List<Source> sources = new LinkedList<>();
          for (Source source : sourceRepository.findAllByOrderByName()) {
            sources.add(source);
          }
      Source nullSource = new Source();
      for (Quote quote : quoteRepository.getAllBySourceOrderByTextAsc(null)) {
        nullSource.getQuotes().add(quote);
      }
      sources.add(nullSource);
      return sources;
    } else {
      return sourceRepository.findAllByOrderByName();
    }
  }

  @GetMapping(value = "/search", produces = MediaType.APPLICATION_JSON_VALUE)
  public Iterable<Source> search(@RequestParam("q") String fragment) {
    if (fragment.length() < 3) {
      throw new SearchTermTooShortException();
    }
    return sourceRepository.getAllByNameContainsOrderByNameAsc(fragment);
  }

  @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  public Source get(@PathVariable UUID id) {
    return sourceRepository.findById(id).get();
  }

  @DeleteMapping(value = "/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@PathVariable UUID id) {
    sourceRepository.findById(id).ifPresent((source) -> {
      Set<Quote> quotes = source.getQuotes();
      quotes.forEach((quote) -> quote.setSource(null));
      quotes.clear();
      sourceRepository.delete(source);
    });
  }

  @PutMapping(value = "/{id}",
      consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
  public Source put(@PathVariable UUID id, @RequestBody Source updated) {
    Source source = get(id);
    source.setName(updated.getName());
    return sourceRepository.save(source);
  }

}
