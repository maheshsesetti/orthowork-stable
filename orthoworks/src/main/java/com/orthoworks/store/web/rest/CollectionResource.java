package com.orthoworks.store.web.rest;

import com.orthoworks.store.domain.Collection;
import com.orthoworks.store.repository.CollectionRepository;
import com.orthoworks.store.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.reactive.ResponseUtil;

/**
 * REST controller for managing {@link com.orthoworks.store.domain.Collection}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class CollectionResource {

    private final Logger log = LoggerFactory.getLogger(CollectionResource.class);

    private static final String ENTITY_NAME = "collection";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final CollectionRepository collectionRepository;

    public CollectionResource(CollectionRepository collectionRepository) {
        this.collectionRepository = collectionRepository;
    }

    /**
     * {@code POST  /collections} : Create a new collection.
     *
     * @param collection the collection to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new collection, or with status {@code 400 (Bad Request)} if the collection has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/collections")
    public Mono<ResponseEntity<Collection>> createCollection(@Valid @RequestBody Collection collection) throws URISyntaxException {
        log.debug("REST request to save Collection : {}", collection);
        if (collection.getId() != null) {
            throw new BadRequestAlertException("A new collection cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return collectionRepository
            .save(collection)
            .map(result -> {
                try {
                    return ResponseEntity
                        .created(new URI("/api/collections/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /collections/:id} : Updates an existing collection.
     *
     * @param id the id of the collection to save.
     * @param collection the collection to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated collection,
     * or with status {@code 400 (Bad Request)} if the collection is not valid,
     * or with status {@code 500 (Internal Server Error)} if the collection couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/collections/{id}")
    public Mono<ResponseEntity<Collection>> updateCollection(
        @PathVariable(value = "id", required = false) final String id,
        @Valid @RequestBody Collection collection
    ) throws URISyntaxException {
        log.debug("REST request to update Collection : {}, {}", id, collection);
        if (collection.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, collection.getId().toString())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return collectionRepository
            .existsById(collection.getId())
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return collectionRepository
                    .save(collection)
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
     * {@code PATCH  /collections/:id} : Partial updates given fields of an existing collection, field will ignore if it is null
     *
     * @param id the id of the collection to save.
     * @param collection the collection to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated collection,
     * or with status {@code 400 (Bad Request)} if the collection is not valid,
     * or with status {@code 404 (Not Found)} if the collection is not found,
     * or with status {@code 500 (Internal Server Error)} if the collection couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/collections/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<Collection>> partialUpdateCollection(
        @PathVariable(value = "id", required = false) final String id,
        @NotNull @RequestBody Collection collection
    ) throws URISyntaxException {
        log.debug("REST request to partial update Collection partially : {}, {}", id, collection);
        if (collection.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, collection.getId().toString())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return collectionRepository
            .existsById(collection.getId())
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<Collection> result = collectionRepository
                    .findById(collection.getId())
                    .map(existingCollection -> {
                        if (collection.getName() != null) {
                            existingCollection.setName(collection.getName());
                        }
                        if (collection.getTitle() != null) {
                            existingCollection.setTitle(collection.getTitle());
                        }
                        if (collection.getCount() != null) {
                            existingCollection.setCount(collection.getCount());
                        }
                        if (collection.getCollectionType() != null) {
                            existingCollection.setCollectionType(collection.getCollectionType());
                        }
                        if (collection.getAuctionType() != null) {
                            existingCollection.setAuctionType(collection.getAuctionType());
                        }
                        if (collection.getMinRange() != null) {
                            existingCollection.setMinRange(collection.getMinRange());
                        }
                        if (collection.getMaxRange() != null) {
                            existingCollection.setMaxRange(collection.getMaxRange());
                        }
                        if (collection.getCurrency() != null) {
                            existingCollection.setCurrency(collection.getCurrency());
                        }
                        if (collection.getOwner() != null) {
                            existingCollection.setOwner(collection.getOwner());
                        }

                        return existingCollection;
                    })
                    .flatMap(collectionRepository::save);

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
     * {@code GET  /collections} : get all the collections.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of collections in body.
     */
    @GetMapping("/collections")
    @PreAuthorize("permitAll()")
    public Mono<ResponseEntity<List<Collection>>> getAllCollections(
        @org.springdoc.api.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        log.debug("REST request to get a page of Collections");
        return collectionRepository
            .count()
            .zipWith(collectionRepository.findAllBy(pageable).collectList())
            .map(countWithEntities ->
                ResponseEntity
                    .ok()
                    .headers(
                        PaginationUtil.generatePaginationHttpHeaders(
                            UriComponentsBuilder.fromHttpRequest(request),
                            new PageImpl<>(countWithEntities.getT2(), pageable, countWithEntities.getT1())
                        )
                    )
                    .body(countWithEntities.getT2())
            );
    }

    /**
     * {@code GET  /collections/:id} : get the "id" collection.
     *
     * @param id the id of the collection to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the collection, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/collections/{id}")
    public Mono<ResponseEntity<Collection>> getCollection(@PathVariable String id) {
        log.debug("REST request to get Collection : {}", id);
        Mono<Collection> collection = collectionRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(collection);
    }

    /**
     * {@code DELETE  /collections/:id} : delete the "id" collection.
     *
     * @param id the id of the collection to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/collections/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public Mono<ResponseEntity<Void>> deleteCollection(@PathVariable Long id) {
        log.debug("REST request to delete Collection : {}", id);
        return collectionRepository
            .deleteById(id)
            .map(result ->
                ResponseEntity
                    .noContent()
                    .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
                    .build()
            );
    }
}
