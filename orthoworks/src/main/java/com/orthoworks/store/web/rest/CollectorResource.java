package com.orthoworks.store.web.rest;

import com.orthoworks.store.domain.Collector;
import com.orthoworks.store.repository.CollectorRepository;
import com.orthoworks.store.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.reactive.ResponseUtil;

/**
 * REST controller for managing {@link com.orthoworks.store.domain.Collector}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class CollectorResource {

    private final Logger log = LoggerFactory.getLogger(CollectorResource.class);

    private static final String ENTITY_NAME = "collector";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final CollectorRepository collectorRepository;

    public CollectorResource(CollectorRepository collectorRepository) {
        this.collectorRepository = collectorRepository;
    }

    /**
     * {@code POST  /collectors} : Create a new collector.
     *
     * @param collector the collector to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new collector, or with status {@code 400 (Bad Request)} if the collector has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/collectors")
    public Mono<ResponseEntity<Collector>> createCollector(@Valid @RequestBody Collector collector) throws URISyntaxException {
        log.debug("REST request to save Collector : {}", collector);
        if (collector.getId() != null) {
            throw new BadRequestAlertException("A new collector cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return collectorRepository
            .save(collector)
            .map(result -> {
                try {
                    return ResponseEntity
                        .created(new URI("/api/collectors/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /collectors/:id} : Updates an existing collector.
     *
     * @param id the id of the collector to save.
     * @param collector the collector to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated collector,
     * or with status {@code 400 (Bad Request)} if the collector is not valid,
     * or with status {@code 500 (Internal Server Error)} if the collector couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/collectors/{id}")
    public Mono<ResponseEntity<Collector>> updateCollector(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody Collector collector
    ) throws URISyntaxException {
        log.debug("REST request to update Collector : {}, {}", id, collector);
        if (collector.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, collector.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return collectorRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return collectorRepository
                    .save(collector)
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(result ->
                        ResponseEntity
                            .ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                            .body(result)
                    );
            });
    }

    /**
     * {@code PATCH  /collectors/:id} : Partial updates given fields of an existing collector, field will ignore if it is null
     *
     * @param id the id of the collector to save.
     * @param collector the collector to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated collector,
     * or with status {@code 400 (Bad Request)} if the collector is not valid,
     * or with status {@code 404 (Not Found)} if the collector is not found,
     * or with status {@code 500 (Internal Server Error)} if the collector couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/collectors/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<Collector>> partialUpdateCollector(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Collector collector
    ) throws URISyntaxException {
        log.debug("REST request to partial update Collector partially : {}, {}", id, collector);
        if (collector.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, collector.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return collectorRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<Collector> result = collectorRepository
                    .findById(collector.getId())
                    .map(existingCollector -> {
                        if (collector.getFirstName() != null) {
                            existingCollector.setFirstName(collector.getFirstName());
                        }
                        if (collector.getLastName() != null) {
                            existingCollector.setLastName(collector.getLastName());
                        }
                        if (collector.getGender() != null) {
                            existingCollector.setGender(collector.getGender());
                        }
                        if (collector.getEmail() != null) {
                            existingCollector.setEmail(collector.getEmail());
                        }
                        if (collector.getPhone() != null) {
                            existingCollector.setPhone(collector.getPhone());
                        }
                        if (collector.getAddressLine1() != null) {
                            existingCollector.setAddressLine1(collector.getAddressLine1());
                        }
                        if (collector.getAddressLine2() != null) {
                            existingCollector.setAddressLine2(collector.getAddressLine2());
                        }
                        if (collector.getCity() != null) {
                            existingCollector.setCity(collector.getCity());
                        }
                        if (collector.getCountry() != null) {
                            existingCollector.setCountry(collector.getCountry());
                        }

                        return existingCollector;
                    })
                    .flatMap(collectorRepository::save);

                return result
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(res ->
                        ResponseEntity
                            .ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, res.getId().toString()))
                            .body(res)
                    );
            });
    }

    /**
     * {@code GET  /collectors} : get all the collectors.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of collectors in body.
     */
    @GetMapping("/collectors")
    public Mono<List<Collector>> getAllCollectors() {
        log.debug("REST request to get all Collectors");
        return collectorRepository.findAll().collectList();
    }

    /**
     * {@code GET  /collectors} : get all the collectors as a stream.
     * @return the {@link Flux} of collectors.
     */
    @GetMapping(value = "/collectors", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<Collector> getAllCollectorsAsStream() {
        log.debug("REST request to get all Collectors as a stream");
        return collectorRepository.findAll();
    }

    /**
     * {@code GET  /collectors/:id} : get the "id" collector.
     *
     * @param id the id of the collector to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the collector, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/collectors/{id}")
    public Mono<ResponseEntity<Collector>> getCollector(@PathVariable Long id) {
        log.debug("REST request to get Collector : {}", id);
        Mono<Collector> collector = collectorRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(collector);
    }

    /**
     * {@code DELETE  /collectors/:id} : delete the "id" collector.
     *
     * @param id the id of the collector to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/collectors/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public Mono<ResponseEntity<Void>> deleteCollector(@PathVariable Long id) {
        log.debug("REST request to delete Collector : {}", id);
        return collectorRepository
            .deleteById(id)
            .map(result ->
                ResponseEntity
                    .noContent()
                    .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
                    .build()
            );
    }
}
