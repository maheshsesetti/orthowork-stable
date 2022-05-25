package com.orthoworks.api.web.rest;

import com.orthoworks.api.domain.Collection;
import com.orthoworks.api.repository.CollectionRepository;
import com.orthoworks.api.web.rest.errors.BadRequestAlertException;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.orthoworks.api.domain.Collection}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class CollectionResource {

    private final Logger log = LoggerFactory.getLogger(CollectionResource.class);

    private static final String ENTITY_NAME = "apiCollection";

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
    public ResponseEntity<Collection> createCollection(@Valid @RequestBody Collection collection) throws URISyntaxException {
        log.debug("REST request to save Collection : {}", collection);
        if (collection.getId() != null) {
            throw new BadRequestAlertException("A new collection cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Collection result = collectionRepository.save(collection);
        return ResponseEntity
            .created(new URI("/api/collections/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
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
    public ResponseEntity<Collection> updateCollection(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody Collection collection
    ) throws URISyntaxException {
        log.debug("REST request to update Collection : {}, {}", id, collection);
        if (collection.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, collection.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!collectionRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Collection result = collectionRepository.save(collection);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, collection.getId().toString()))
            .body(result);
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
    public ResponseEntity<Collection> partialUpdateCollection(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Collection collection
    ) throws URISyntaxException {
        log.debug("REST request to partial update Collection partially : {}, {}", id, collection);
        if (collection.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, collection.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!collectionRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Collection> result = collectionRepository
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
            .map(collectionRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, collection.getId().toString())
        );
    }

    /**
     * {@code GET  /collections} : get all the collections.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of collections in body.
     */
    @GetMapping("/collections")
    @PreAuthorize("permitAll()")
    public ResponseEntity<List<Collection>> getAllCollections(@org.springdoc.api.annotations.ParameterObject Pageable pageable) {
        log.debug("REST request to get a page of Collections");
        Page<Collection> page = collectionRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /collections/:id} : get the "id" collection.
     *
     * @param id the id of the collection to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the collection, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/collections/{id}")
    public ResponseEntity<Collection> getCollection(@PathVariable String id) {
        log.debug("REST request to get Collection : {}", id);
        Optional<Collection> collection = collectionRepository.findBySlug(id);
        return ResponseUtil.wrapOrNotFound(collection);
    }

    /**
     * {@code DELETE  /collections/:id} : delete the "id" collection.
     *
     * @param id the id of the collection to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/collections/{id}")
    public ResponseEntity<Void> deleteCollection(@PathVariable Long id) {
        log.debug("REST request to delete Collection : {}", id);
        collectionRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
