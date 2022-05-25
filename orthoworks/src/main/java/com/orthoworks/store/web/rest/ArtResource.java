package com.orthoworks.store.web.rest;

import com.orthoworks.store.domain.Art;
import com.orthoworks.store.repository.ArtRepository;
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
 * REST controller for managing {@link com.orthoworks.store.domain.Art}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class ArtResource {

    private final Logger log = LoggerFactory.getLogger(ArtResource.class);

    private static final String ENTITY_NAME = "art";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ArtRepository artRepository;

    public ArtResource(ArtRepository artRepository) {
        this.artRepository = artRepository;
    }

    /**
     * {@code POST  /arts} : Create a new art.
     *
     * @param art the art to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new art, or with status {@code 400 (Bad Request)} if the art has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/arts")
    public Mono<ResponseEntity<Art>> createArt(@Valid @RequestBody Art art) throws URISyntaxException {
        log.debug("REST request to save Art : {}", art);
        if (art.getId() != null) {
            throw new BadRequestAlertException("A new art cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return artRepository
            .save(art)
            .map(result -> {
                try {
                    return ResponseEntity
                        .created(new URI("/api/arts/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /arts/:id} : Updates an existing art.
     *
     * @param id the id of the art to save.
     * @param art the art to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated art,
     * or with status {@code 400 (Bad Request)} if the art is not valid,
     * or with status {@code 500 (Internal Server Error)} if the art couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/arts/{id}")
    public Mono<ResponseEntity<Art>> updateArt(@PathVariable(value = "id", required = false) final Long id, @Valid @RequestBody Art art)
        throws URISyntaxException {
        log.debug("REST request to update Art : {}, {}", id, art);
        if (art.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, art.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return artRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return artRepository
                    .save(art)
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
     * {@code PATCH  /arts/:id} : Partial updates given fields of an existing art, field will ignore if it is null
     *
     * @param id the id of the art to save.
     * @param art the art to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated art,
     * or with status {@code 400 (Bad Request)} if the art is not valid,
     * or with status {@code 404 (Not Found)} if the art is not found,
     * or with status {@code 500 (Internal Server Error)} if the art couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/arts/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<Art>> partialUpdateArt(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Art art
    ) throws URISyntaxException {
        log.debug("REST request to partial update Art partially : {}, {}", id, art);
        if (art.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, art.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return artRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<Art> result = artRepository
                    .findById(art.getId())
                    .map(existingArt -> {
                        if (art.getName() != null) {
                            existingArt.setName(art.getName());
                        }
                        if (art.getHandle() != null) {
                            existingArt.setHandle(art.getHandle());
                        }
                        if (art.getAssetType() != null) {
                            existingArt.setAssetType(art.getAssetType());
                        }
                        if (art.getType() != null) {
                            existingArt.setType(art.getType());
                        }

                        return existingArt;
                    })
                    .flatMap(artRepository::save);

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
     * {@code GET  /arts} : get all the arts.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of arts in body.
     */
    @GetMapping("/arts")
    public Mono<ResponseEntity<List<Art>>> getAllArts(
        @org.springdoc.api.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request,
        @RequestParam(required = false, defaultValue = "false") boolean eagerload
    ) {
        log.debug("REST request to get a page of Arts");
        return artRepository
            .count()
            .zipWith(artRepository.findAllBy(pageable).collectList())
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
     * {@code GET  /arts/:id} : get the "id" art.
     *
     * @param id the id of the art to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the art, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/arts/{id}")
    public Mono<ResponseEntity<Art>> getArt(@PathVariable Long id) {
        log.debug("REST request to get Art : {}", id);
        Mono<Art> art = artRepository.findOneWithEagerRelationships(id);
        return ResponseUtil.wrapOrNotFound(art);
    }

    /**
     * {@code DELETE  /arts/:id} : delete the "id" art.
     *
     * @param id the id of the art to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/arts/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public Mono<ResponseEntity<Void>> deleteArt(@PathVariable Long id) {
        log.debug("REST request to delete Art : {}", id);
        return artRepository
            .deleteById(id)
            .map(result ->
                ResponseEntity
                    .noContent()
                    .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
                    .build()
            );
    }
}
