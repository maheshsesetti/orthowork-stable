package com.orthoworks.common.web.rest;

import com.orthoworks.common.domain.BrandCategory;
import com.orthoworks.common.repository.BrandCategoryRepository;
import com.orthoworks.common.web.rest.errors.BadRequestAlertException;
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
 * REST controller for managing {@link com.orthoworks.common.domain.BrandCategory}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class BrandCategoryResource {

    private final Logger log = LoggerFactory.getLogger(BrandCategoryResource.class);

    private static final String ENTITY_NAME = "commonBrandCategory";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final BrandCategoryRepository brandCategoryRepository;

    public BrandCategoryResource(BrandCategoryRepository brandCategoryRepository) {
        this.brandCategoryRepository = brandCategoryRepository;
    }

    /**
     * {@code POST  /brand-categories} : Create a new brandCategory.
     *
     * @param brandCategory the brandCategory to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new brandCategory, or with status {@code 400 (Bad Request)} if the brandCategory has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/brand-categories")
    public ResponseEntity<BrandCategory> createBrandCategory(@Valid @RequestBody BrandCategory brandCategory) throws URISyntaxException {
        log.debug("REST request to save BrandCategory : {}", brandCategory);
        if (brandCategory.getId() != null) {
            throw new BadRequestAlertException("A new brandCategory cannot already have an ID", ENTITY_NAME, "idexists");
        }
        BrandCategory result = brandCategoryRepository.save(brandCategory);
        return ResponseEntity
            .created(new URI("/api/brand-categories/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /brand-categories/:id} : Updates an existing brandCategory.
     *
     * @param id the id of the brandCategory to save.
     * @param brandCategory the brandCategory to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated brandCategory,
     * or with status {@code 400 (Bad Request)} if the brandCategory is not valid,
     * or with status {@code 500 (Internal Server Error)} if the brandCategory couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/brand-categories/{id}")
    public ResponseEntity<BrandCategory> updateBrandCategory(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody BrandCategory brandCategory
    ) throws URISyntaxException {
        log.debug("REST request to update BrandCategory : {}, {}", id, brandCategory);
        if (brandCategory.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, brandCategory.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!brandCategoryRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        BrandCategory result = brandCategoryRepository.save(brandCategory);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, brandCategory.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /brand-categories/:id} : Partial updates given fields of an existing brandCategory, field will ignore if it is null
     *
     * @param id the id of the brandCategory to save.
     * @param brandCategory the brandCategory to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated brandCategory,
     * or with status {@code 400 (Bad Request)} if the brandCategory is not valid,
     * or with status {@code 404 (Not Found)} if the brandCategory is not found,
     * or with status {@code 500 (Internal Server Error)} if the brandCategory couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/brand-categories/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<BrandCategory> partialUpdateBrandCategory(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody BrandCategory brandCategory
    ) throws URISyntaxException {
        log.debug("REST request to partial update BrandCategory partially : {}, {}", id, brandCategory);
        if (brandCategory.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, brandCategory.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!brandCategoryRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<BrandCategory> result = brandCategoryRepository
            .findById(brandCategory.getId())
            .map(existingBrandCategory -> {
                if (brandCategory.getDescription() != null) {
                    existingBrandCategory.setDescription(brandCategory.getDescription());
                }
                if (brandCategory.getSortOrder() != null) {
                    existingBrandCategory.setSortOrder(brandCategory.getSortOrder());
                }
                if (brandCategory.getDateAdded() != null) {
                    existingBrandCategory.setDateAdded(brandCategory.getDateAdded());
                }
                if (brandCategory.getDateModified() != null) {
                    existingBrandCategory.setDateModified(brandCategory.getDateModified());
                }

                return existingBrandCategory;
            })
            .map(brandCategoryRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, brandCategory.getId().toString())
        );
    }

    /**
     * {@code GET  /brand-categories} : get all the brandCategories.
     *
     * @param pageable the pagination information.
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of brandCategories in body.
     */
    @GetMapping("/brand-categories")
    public ResponseEntity<List<BrandCategory>> getAllBrandCategories(
        @org.springdoc.api.annotations.ParameterObject Pageable pageable,
        @RequestParam(required = false, defaultValue = "false") boolean eagerload
    ) {
        log.debug("REST request to get a page of BrandCategories");
        Page<BrandCategory> page;
        if (eagerload) {
            page = brandCategoryRepository.findAllWithEagerRelationships(pageable);
        } else {
            page = brandCategoryRepository.findAll(pageable);
        }
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /brand-categories/:id} : get the "id" brandCategory.
     *
     * @param id the id of the brandCategory to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the brandCategory, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/brand-categories/{id}")
    public ResponseEntity<BrandCategory> getBrandCategory(@PathVariable Long id) {
        log.debug("REST request to get BrandCategory : {}", id);
        Optional<BrandCategory> brandCategory = brandCategoryRepository.findOneWithEagerRelationships(id);
        return ResponseUtil.wrapOrNotFound(brandCategory);
    }

    /**
     * {@code DELETE  /brand-categories/:id} : delete the "id" brandCategory.
     *
     * @param id the id of the brandCategory to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/brand-categories/{id}")
    public ResponseEntity<Void> deleteBrandCategory(@PathVariable Long id) {
        log.debug("REST request to delete BrandCategory : {}", id);
        brandCategoryRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
