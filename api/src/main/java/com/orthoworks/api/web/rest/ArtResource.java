package com.orthoworks.api.web.rest;

import com.orthoworks.api.domain.Art;
import com.orthoworks.api.repository.ArtRepository;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.orthoworks.api.domain.Art}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class ArtResource {

    private final Logger log = LoggerFactory.getLogger(ArtResource.class);

    private static final String ENTITY_NAME = "apiArt";

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
    public ResponseEntity<Art> createArt(@Valid @RequestBody Art art) throws URISyntaxException {
        log.debug("REST request to save Art : {}", art);
        if (art.getId() != null) {
            throw new BadRequestAlertException("A new art cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Art result = artRepository.save(art);
        return ResponseEntity
            .created(new URI("/api/arts/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
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
    public ResponseEntity<Art> updateArt(@PathVariable(value = "id", required = false) final Long id, @Valid @RequestBody Art art)
        throws URISyntaxException {
        log.debug("REST request to update Art : {}, {}", id, art);
        if (art.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, art.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!artRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Art result = artRepository.save(art);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, art.getId().toString()))
            .body(result);
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
    public ResponseEntity<Art> partialUpdateArt(@PathVariable(value = "id", required = false) final Long id, @NotNull @RequestBody Art art)
        throws URISyntaxException {
        log.debug("REST request to partial update Art partially : {}, {}", id, art);
        if (art.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, art.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!artRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Art> result = artRepository
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
            .map(artRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, art.getId().toString())
        );
    }

    /**
     * {@code GET  /arts} : get all the arts.
     *
     * @param pageable the pagination information.
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of arts in body.
     */
    @GetMapping("/arts")
    public ResponseEntity<List<Art>> getAllArts(
        @org.springdoc.api.annotations.ParameterObject Pageable pageable,
        @RequestParam(required = false, defaultValue = "false") boolean eagerload
    ) {
        log.debug("REST request to get a page of Arts");
        Page<Art> page;
        if (eagerload) {
            page = artRepository.findAllWithEagerRelationships(pageable);
        } else {
            page = artRepository.findAll(pageable);
        }
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /arts/:id} : get the "id" art.
     *
     * @param id the id of the art to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the art, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/arts/{id}")
    public ResponseEntity<Art> getArt(@PathVariable Long id) {
        log.debug("REST request to get Art : {}", id);
        Optional<Art> art = artRepository.findOneWithEagerRelationships(id);
        return ResponseUtil.wrapOrNotFound(art);
    }

    /**
     * {@code DELETE  /arts/:id} : delete the "id" art.
     *
     * @param id the id of the art to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/arts/{id}")
    public ResponseEntity<Void> deleteArt(@PathVariable Long id) {
        log.debug("REST request to delete Art : {}", id);
        artRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
