package com.orthoworks.api.web.rest;

import com.orthoworks.api.domain.Output;
import com.orthoworks.api.repository.OutputRepository;
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
 * REST controller for managing {@link com.orthoworks.api.domain.Output}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class OutputResource {

    private final Logger log = LoggerFactory.getLogger(OutputResource.class);

    private static final String ENTITY_NAME = "apiOutput";

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
    public ResponseEntity<Output> createOutput(@Valid @RequestBody Output output) throws URISyntaxException {
        log.debug("REST request to save Output : {}", output);
        if (output.getId() != null) {
            throw new BadRequestAlertException("A new output cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Output result = outputRepository.save(output);
        return ResponseEntity
            .created(new URI("/api/outputs/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
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
    public ResponseEntity<Output> updateOutput(
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

        if (!outputRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Output result = outputRepository.save(output);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, output.getId().toString()))
            .body(result);
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
    public ResponseEntity<Output> partialUpdateOutput(
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

        if (!outputRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Output> result = outputRepository
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
            .map(outputRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, output.getId().toString())
        );
    }

    /**
     * {@code GET  /outputs} : get all the outputs.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of outputs in body.
     */
    @GetMapping("/outputs")
    public ResponseEntity<List<Output>> getAllOutputs(@org.springdoc.api.annotations.ParameterObject Pageable pageable) {
        log.debug("REST request to get a page of Outputs");
        Page<Output> page = outputRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /outputs/:id} : get the "id" output.
     *
     * @param id the id of the output to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the output, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/outputs/{id}")
    public ResponseEntity<Output> getOutput(@PathVariable Long id) {
        log.debug("REST request to get Output : {}", id);
        Optional<Output> output = outputRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(output);
    }

    /**
     * {@code DELETE  /outputs/:id} : delete the "id" output.
     *
     * @param id the id of the output to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/outputs/{id}")
    public ResponseEntity<Void> deleteOutput(@PathVariable Long id) {
        log.debug("REST request to delete Output : {}", id);
        outputRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
