package com.orthoworks.store.web.rest;

import com.orthoworks.store.domain.BrandCategory;
import com.orthoworks.store.repository.BrandCategoryRepository;
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
 * REST controller for managing {@link com.orthoworks.store.domain.BrandCategory}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class BrandCategoryResource {

    private final Logger log = LoggerFactory.getLogger(BrandCategoryResource.class);

    private static final String ENTITY_NAME = "brandCategory";

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
    public Mono<ResponseEntity<BrandCategory>> createBrandCategory(@Valid @RequestBody BrandCategory brandCategory)
        throws URISyntaxException {
        log.debug("REST request to save BrandCategory : {}", brandCategory);
        if (brandCategory.getId() != null) {
            throw new BadRequestAlertException("A new brandCategory cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return brandCategoryRepository
            .save(brandCategory)
            .map(result -> {
                try {
                    return ResponseEntity
                        .created(new URI("/api/brand-categories/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
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
    public Mono<ResponseEntity<BrandCategory>> updateBrandCategory(
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

        return brandCategoryRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return brandCategoryRepository
                    .save(brandCategory)
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
    public Mono<ResponseEntity<BrandCategory>> partialUpdateBrandCategory(
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

        return brandCategoryRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<BrandCategory> result = brandCategoryRepository
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
                    .flatMap(brandCategoryRepository::save);

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
     * {@code GET  /brand-categories} : get all the brandCategories.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of brandCategories in body.
     */
    @GetMapping("/brand-categories")
    public Mono<ResponseEntity<List<BrandCategory>>> getAllBrandCategories(
        @org.springdoc.api.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request,
        @RequestParam(required = false, defaultValue = "false") boolean eagerload
    ) {
        log.debug("REST request to get a page of BrandCategories");
        return brandCategoryRepository
            .count()
            .zipWith(brandCategoryRepository.findAllBy(pageable).collectList())
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
     * {@code GET  /brand-categories/:id} : get the "id" brandCategory.
     *
     * @param id the id of the brandCategory to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the brandCategory, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/brand-categories/{id}")
    public Mono<ResponseEntity<BrandCategory>> getBrandCategory(@PathVariable Long id) {
        log.debug("REST request to get BrandCategory : {}", id);
        Mono<BrandCategory> brandCategory = brandCategoryRepository.findOneWithEagerRelationships(id);
        return ResponseUtil.wrapOrNotFound(brandCategory);
    }

    /**
     * {@code DELETE  /brand-categories/:id} : delete the "id" brandCategory.
     *
     * @param id the id of the brandCategory to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/brand-categories/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public Mono<ResponseEntity<Void>> deleteBrandCategory(@PathVariable Long id) {
        log.debug("REST request to delete BrandCategory : {}", id);
        return brandCategoryRepository
            .deleteById(id)
            .map(result ->
                ResponseEntity
                    .noContent()
                    .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
                    .build()
            );
    }
}
