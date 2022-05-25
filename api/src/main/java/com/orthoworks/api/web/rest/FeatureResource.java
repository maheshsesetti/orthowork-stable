package com.orthoworks.api.web.rest;

import com.orthoworks.api.domain.Feature;
import com.orthoworks.api.repository.FeatureRepository;
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
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.orthoworks.api.domain.Feature}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class FeatureResource {

    private final Logger log = LoggerFactory.getLogger(FeatureResource.class);

    private static final String ENTITY_NAME = "apiFeature";

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
    public ResponseEntity<Feature> createFeature(@Valid @RequestBody Feature feature) throws URISyntaxException {
        log.debug("REST request to save Feature : {}", feature);
        if (feature.getId() != null) {
            throw new BadRequestAlertException("A new feature cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Feature result = featureRepository.save(feature);
        return ResponseEntity
            .created(new URI("/api/features/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
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
    public ResponseEntity<Feature> updateFeature(
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

        if (!featureRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Feature result = featureRepository.save(feature);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, feature.getId().toString()))
            .body(result);
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
    public ResponseEntity<Feature> partialUpdateFeature(
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

        if (!featureRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Feature> result = featureRepository
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
            .map(featureRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, feature.getId().toString())
        );
    }

    /**
     * {@code GET  /features} : get all the features.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of features in body.
     */
    @GetMapping("/features")
    public List<Feature> getAllFeatures() {
        log.debug("REST request to get all Features");
        return featureRepository.findAll();
    }

    /**
     * {@code GET  /features/:id} : get the "id" feature.
     *
     * @param id the id of the feature to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the feature, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/features/{id}")
    public ResponseEntity<Feature> getFeature(@PathVariable Long id) {
        log.debug("REST request to get Feature : {}", id);
        Optional<Feature> feature = featureRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(feature);
    }

    /**
     * {@code DELETE  /features/:id} : delete the "id" feature.
     *
     * @param id the id of the feature to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/features/{id}")
    public ResponseEntity<Void> deleteFeature(@PathVariable Long id) {
        log.debug("REST request to delete Feature : {}", id);
        featureRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
