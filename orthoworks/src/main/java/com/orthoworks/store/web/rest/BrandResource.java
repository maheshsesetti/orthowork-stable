package com.orthoworks.store.web.rest;

import com.orthoworks.store.domain.Brand;
import com.orthoworks.store.repository.BrandRepository;
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
 * REST controller for managing {@link com.orthoworks.store.domain.Brand}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class BrandResource {

    private final Logger log = LoggerFactory.getLogger(BrandResource.class);

    private static final String ENTITY_NAME = "brand";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final BrandRepository brandRepository;

    public BrandResource(BrandRepository brandRepository) {
        this.brandRepository = brandRepository;
    }

    /**
     * {@code POST  /brands} : Create a new brand.
     *
     * @param brand the brand to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new brand, or with status {@code 400 (Bad Request)} if the brand has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/brands")
    public Mono<ResponseEntity<Brand>> createBrand(@Valid @RequestBody Brand brand) throws URISyntaxException {
        log.debug("REST request to save Brand : {}", brand);
        if (brand.getId() != null) {
            throw new BadRequestAlertException("A new brand cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return brandRepository
            .save(brand)
            .map(result -> {
                try {
                    return ResponseEntity
                        .created(new URI("/api/brands/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /brands/:id} : Updates an existing brand.
     *
     * @param id the id of the brand to save.
     * @param brand the brand to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated brand,
     * or with status {@code 400 (Bad Request)} if the brand is not valid,
     * or with status {@code 500 (Internal Server Error)} if the brand couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/brands/{id}")
    public Mono<ResponseEntity<Brand>> updateBrand(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody Brand brand
    ) throws URISyntaxException {
        log.debug("REST request to update Brand : {}, {}", id, brand);
        if (brand.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, brand.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return brandRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return brandRepository
                    .save(brand)
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
     * {@code PATCH  /brands/:id} : Partial updates given fields of an existing brand, field will ignore if it is null
     *
     * @param id the id of the brand to save.
     * @param brand the brand to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated brand,
     * or with status {@code 400 (Bad Request)} if the brand is not valid,
     * or with status {@code 404 (Not Found)} if the brand is not found,
     * or with status {@code 500 (Internal Server Error)} if the brand couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/brands/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<Brand>> partialUpdateBrand(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Brand brand
    ) throws URISyntaxException {
        log.debug("REST request to partial update Brand partially : {}, {}", id, brand);
        if (brand.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, brand.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return brandRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<Brand> result = brandRepository
                    .findById(brand.getId())
                    .map(existingBrand -> {
                        if (brand.getTitle() != null) {
                            existingBrand.setTitle(brand.getTitle());
                        }
                        if (brand.getKeywords() != null) {
                            existingBrand.setKeywords(brand.getKeywords());
                        }
                        if (brand.getDescription() != null) {
                            existingBrand.setDescription(brand.getDescription());
                        }
                        if (brand.getImage() != null) {
                            existingBrand.setImage(brand.getImage());
                        }
                        if (brand.getImageContentType() != null) {
                            existingBrand.setImageContentType(brand.getImageContentType());
                        }
                        if (brand.getRating() != null) {
                            existingBrand.setRating(brand.getRating());
                        }
                        if (brand.getStatus() != null) {
                            existingBrand.setStatus(brand.getStatus());
                        }
                        if (brand.getPrice() != null) {
                            existingBrand.setPrice(brand.getPrice());
                        }
                        if (brand.getBrandSize() != null) {
                            existingBrand.setBrandSize(brand.getBrandSize());
                        }
                        if (brand.getDateAdded() != null) {
                            existingBrand.setDateAdded(brand.getDateAdded());
                        }
                        if (brand.getDateModified() != null) {
                            existingBrand.setDateModified(brand.getDateModified());
                        }

                        return existingBrand;
                    })
                    .flatMap(brandRepository::save);

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
     * {@code GET  /brands} : get all the brands.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of brands in body.
     */
    @GetMapping("/brands")
    public Mono<ResponseEntity<List<Brand>>> getAllBrands(
        @org.springdoc.api.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        log.debug("REST request to get a page of Brands");
        return brandRepository
            .count()
            .zipWith(brandRepository.findAllBy(pageable).collectList())
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
     * {@code GET  /brands/:id} : get the "id" brand.
     *
     * @param id the id of the brand to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the brand, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/brands/{id}")
    public Mono<ResponseEntity<Brand>> getBrand(@PathVariable Long id) {
        log.debug("REST request to get Brand : {}", id);
        Mono<Brand> brand = brandRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(brand);
    }

    /**
     * {@code DELETE  /brands/:id} : delete the "id" brand.
     *
     * @param id the id of the brand to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/brands/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public Mono<ResponseEntity<Void>> deleteBrand(@PathVariable Long id) {
        log.debug("REST request to delete Brand : {}", id);
        return brandRepository
            .deleteById(id)
            .map(result ->
                ResponseEntity
                    .noContent()
                    .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
                    .build()
            );
    }
}
