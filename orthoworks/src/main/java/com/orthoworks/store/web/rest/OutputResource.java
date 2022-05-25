package com.orthoworks.store.web.rest;

import com.orthoworks.store.domain.Output;
import com.orthoworks.store.repository.OutputRepository;
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
 * REST controller for managing {@link com.orthoworks.store.domain.Output}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class OutputResource {

    private final Logger log = LoggerFactory.getLogger(OutputResource.class);

    private static final String ENTITY_NAME = "output";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final OutputRepository outputRepository;

    public OutputResource(OutputRepository outputRepository) {
        this.outputRepository = outputRepository;
    }

    /**
     * {@code POST  /outputs} : Create a new output.
     *
     * @param output the output to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new output, or with status {@code 400 (Bad Request)} if the output has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/outputs")
    public Mono<ResponseEntity<Output>> createOutput(@Valid @RequestBody Output output) throws URISyntaxException {
        log.debug("REST request to save Output : {}", output);
        if (output.getId() != null) {
            throw new BadRequestAlertException("A new output cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return outputRepository
            .save(output)
            .map(result -> {
                try {
                    return ResponseEntity
                        .created(new URI("/api/outputs/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /outputs/:id} : Updates an existing output.
     *
     * @param id the id of the output to save.
     * @param output the output to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated output,
     * or with status {@code 400 (Bad Request)} if the output is not valid,
     * or with status {@code 500 (Internal Server Error)} if the output couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/outputs/{id}")
    public Mono<ResponseEntity<Output>> updateOutput(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody Output output
    ) throws URISyntaxException {
        log.debug("REST request to update Output : {}, {}", id, output);
        if (output.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, output.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return outputRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return outputRepository
                    .save(output)
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
     * {@code PATCH  /outputs/:id} : Partial updates given fields of an existing output, field will ignore if it is null
     *
     * @param id the id of the output to save.
     * @param output the output to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated output,
     * or with status {@code 400 (Bad Request)} if the output is not valid,
     * or with status {@code 404 (Not Found)} if the output is not found,
     * or with status {@code 500 (Internal Server Error)} if the output couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/outputs/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<Output>> partialUpdateOutput(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Output output
    ) throws URISyntaxException {
        log.debug("REST request to partial update Output partially : {}, {}", id, output);
        if (output.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, output.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return outputRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<Output> result = outputRepository
                    .findById(output.getId())
                    .map(existingOutput -> {
                        if (output.getDate() != null) {
                            existingOutput.setDate(output.getDate());
                        }
                        if (output.getResult() != null) {
                            existingOutput.setResult(output.getResult());
                        }

                        return existingOutput;
                    })
                    .flatMap(outputRepository::save);

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
     * {@code GET  /outputs} : get all the outputs.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of outputs in body.
     */
    @GetMapping("/outputs")
    public Mono<ResponseEntity<List<Output>>> getAllOutputs(
        @org.springdoc.api.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        log.debug("REST request to get a page of Outputs");
        return outputRepository
            .count()
            .zipWith(outputRepository.findAllBy(pageable).collectList())
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
     * {@code GET  /outputs/:id} : get the "id" output.
     *
     * @param id the id of the output to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the output, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/outputs/{id}")
    public Mono<ResponseEntity<Output>> getOutput(@PathVariable Long id) {
        log.debug("REST request to get Output : {}", id);
        Mono<Output> output = outputRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(output);
    }

    /**
     * {@code DELETE  /outputs/:id} : delete the "id" output.
     *
     * @param id the id of the output to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/outputs/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public Mono<ResponseEntity<Void>> deleteOutput(@PathVariable Long id) {
        log.debug("REST request to delete Output : {}", id);
        return outputRepository
            .deleteById(id)
            .map(result ->
                ResponseEntity
                    .noContent()
                    .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
                    .build()
            );
    }
}
