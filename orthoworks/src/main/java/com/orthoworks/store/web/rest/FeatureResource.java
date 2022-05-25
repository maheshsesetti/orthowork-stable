package com.orthoworks.store.web.rest;

import com.orthoworks.store.domain.Feature;
import com.orthoworks.store.repository.FeatureRepository;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.reactive.ResponseUtil;

/**
 * REST controller for managing {@link com.orthoworks.store.domain.Feature}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class FeatureResource {

    private final Logger log = LoggerFactory.getLogger(FeatureResource.class);

    private static final String ENTITY_NAME = "feature";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final FeatureRepository featureRepository;

    public FeatureResource(FeatureRepository featureRepository) {
        this.featureRepository = featureRepository;
    }

    /**
     * {@code POST  /features} : Create a new feature.
     *
     * @param feature the feature to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new feature, or with status {@code 400 (Bad Request)} if the feature has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/features")
    public Mono<ResponseEntity<Feature>> createFeature(@Valid @RequestBody Feature feature) throws URISyntaxException {
        log.debug("REST request to save Feature : {}", feature);
        if (feature.getId() != null) {
            throw new BadRequestAlertException("A new feature cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return featureRepository
            .save(feature)
            .map(result -> {
                try {
                    return ResponseEntity
                        .created(new URI("/api/features/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /features/:id} : Updates an existing feature.
     *
     * @param id the id of the feature to save.
     * @param feature the feature to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated feature,
     * or with status {@code 400 (Bad Request)} if the feature is not valid,
     * or with status {@code 500 (Internal Server Error)} if the feature couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/features/{id}")
    public Mono<ResponseEntity<Feature>> updateFeature(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody Feature feature
    ) throws URISyntaxException {
        log.debug("REST request to update Feature : {}, {}", id, feature);
        if (feature.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, feature.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return featureRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return featureRepository
                    .save(feature)
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
     * {@code PATCH  /features/:id} : Partial updates given fields of an existing feature, field will ignore if it is null
     *
     * @param id the id of the feature to save.
     * @param feature the feature to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated feature,
     * or with status {@code 400 (Bad Request)} if the feature is not valid,
     * or with status {@code 404 (Not Found)} if the feature is not found,
     * or with status {@code 500 (Internal Server Error)} if the feature couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/features/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<Feature>> partialUpdateFeature(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Feature feature
    ) throws URISyntaxException {
        log.debug("REST request to partial update Feature partially : {}, {}", id, feature);
        if (feature.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, feature.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return featureRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<Feature> result = featureRepository
                    .findById(feature.getId())
                    .map(existingFeature -> {
                        if (feature.getName() != null) {
                            existingFeature.setName(feature.getName());
                        }
                        if (feature.getMandatory() != null) {
                            existingFeature.setMandatory(feature.getMandatory());
                        }

                        return existingFeature;
                    })
                    .flatMap(featureRepository::save);

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
     * {@code GET  /features} : get all the features.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of features in body.
     */
    @GetMapping("/features")
    @PreAuthorize("permitAll()")
    public Mono<List<Feature>> getAllFeatures() {
        log.debug("REST request to get all Features");
        return featureRepository.findAll().collectList();
    }

    /**
     * {@code GET  /features} : get all the features as a stream.
     * @return the {@link Flux} of features.
     */
    @GetMapping(value = "/features", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<Feature> getAllFeaturesAsStream() {
        log.debug("REST request to get all Features as a stream");
        return featureRepository.findAll();
    }

    /**
     * {@code GET  /features/:id} : get the "id" feature.
     *
     * @param id the id of the feature to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the feature, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/features/{id}")
    public Mono<ResponseEntity<Feature>> getFeature(@PathVariable Long id) {
        log.debug("REST request to get Feature : {}", id);
        Mono<Feature> feature = featureRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(feature);
    }

    /**
     * {@code DELETE  /features/:id} : delete the "id" feature.
     *
     * @param id the id of the feature to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/features/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public Mono<ResponseEntity<Void>> deleteFeature(@PathVariable Long id) {
        log.debug("REST request to delete Feature : {}", id);
        return featureRepository
            .deleteById(id)
            .map(result ->
                ResponseEntity
                    .noContent()
                    .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
                    .build()
            );
    }
}
