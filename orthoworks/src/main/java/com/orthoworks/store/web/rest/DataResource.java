package com.orthoworks.store.web.rest;

import com.orthoworks.store.domain.Data;
import com.orthoworks.store.repository.DataRepository;
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
 * REST controller for managing {@link com.orthoworks.store.domain.Data}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class DataResource {

    private final Logger log = LoggerFactory.getLogger(DataResource.class);

    private static final String ENTITY_NAME = "data";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final DataRepository dataRepository;

    public DataResource(DataRepository dataRepository) {
        this.dataRepository = dataRepository;
    }

    /**
     * {@code POST  /data} : Create a new data.
     *
     * @param data the data to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new data, or with status {@code 400 (Bad Request)} if the data has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/data")
    public Mono<ResponseEntity<Data>> createData(@Valid @RequestBody Data data) throws URISyntaxException {
        log.debug("REST request to save Data : {}", data);
        if (data.getId() != null) {
            throw new BadRequestAlertException("A new data cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return dataRepository
            .save(data)
            .map(result -> {
                try {
                    return ResponseEntity
                        .created(new URI("/api/data/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /data/:id} : Updates an existing data.
     *
     * @param id the id of the data to save.
     * @param data the data to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated data,
     * or with status {@code 400 (Bad Request)} if the data is not valid,
     * or with status {@code 500 (Internal Server Error)} if the data couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/data/{id}")
    public Mono<ResponseEntity<Data>> updateData(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody Data data
    ) throws URISyntaxException {
        log.debug("REST request to update Data : {}, {}", id, data);
        if (data.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, data.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return dataRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return dataRepository
                    .save(data)
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
     * {@code PATCH  /data/:id} : Partial updates given fields of an existing data, field will ignore if it is null
     *
     * @param id the id of the data to save.
     * @param data the data to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated data,
     * or with status {@code 400 (Bad Request)} if the data is not valid,
     * or with status {@code 404 (Not Found)} if the data is not found,
     * or with status {@code 500 (Internal Server Error)} if the data couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/data/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<Data>> partialUpdateData(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Data data
    ) throws URISyntaxException {
        log.debug("REST request to partial update Data partially : {}, {}", id, data);
        if (data.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, data.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return dataRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<Data> result = dataRepository
                    .findById(data.getId())
                    .map(existingData -> {
                        if (data.getName() != null) {
                            existingData.setName(data.getName());
                        }
                        if (data.getFile() != null) {
                            existingData.setFile(data.getFile());
                        }
                        if (data.getFileContentType() != null) {
                            existingData.setFileContentType(data.getFileContentType());
                        }

                        return existingData;
                    })
                    .flatMap(dataRepository::save);

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
     * {@code GET  /data} : get all the data.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of data in body.
     */
    @GetMapping("/data")
    public Mono<ResponseEntity<List<Data>>> getAllData(
        @org.springdoc.api.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        log.debug("REST request to get a page of Data");
        return dataRepository
            .count()
            .zipWith(dataRepository.findAllBy(pageable).collectList())
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
     * {@code GET  /data/:id} : get the "id" data.
     *
     * @param id the id of the data to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the data, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/data/{id}")
    public Mono<ResponseEntity<Data>> getData(@PathVariable Long id) {
        log.debug("REST request to get Data : {}", id);
        Mono<Data> data = dataRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(data);
    }

    /**
     * {@code DELETE  /data/:id} : delete the "id" data.
     *
     * @param id the id of the data to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/data/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public Mono<ResponseEntity<Void>> deleteData(@PathVariable Long id) {
        log.debug("REST request to delete Data : {}", id);
        return dataRepository
            .deleteById(id)
            .map(result ->
                ResponseEntity
                    .noContent()
                    .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
                    .build()
            );
    }
}
