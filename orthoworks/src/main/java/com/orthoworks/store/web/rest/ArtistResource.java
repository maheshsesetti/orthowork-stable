package com.orthoworks.store.web.rest;

import com.orthoworks.store.domain.Artist;
import com.orthoworks.store.repository.ArtistRepository;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.reactive.ResponseUtil;

/**
 * REST controller for managing {@link com.orthoworks.store.domain.Artist}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class ArtistResource {

    private final Logger log = LoggerFactory.getLogger(ArtistResource.class);

    private static final String ENTITY_NAME = "artist";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ArtistRepository artistRepository;

    public ArtistResource(ArtistRepository artistRepository) {
        this.artistRepository = artistRepository;
    }

    /**
     * {@code POST  /artists} : Create a new artist.
     *
     * @param artist the artist to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new artist, or with status {@code 400 (Bad Request)} if the artist has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/artists")
    public Mono<ResponseEntity<Artist>> createArtist(@Valid @RequestBody Artist artist) throws URISyntaxException {
        log.debug("REST request to save Artist : {}", artist);
        if (artist.getId() != null) {
            throw new BadRequestAlertException("A new artist cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return artistRepository
            .save(artist)
            .map(result -> {
                try {
                    return ResponseEntity
                        .created(new URI("/api/artists/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /artists/:id} : Updates an existing artist.
     *
     * @param id the id of the artist to save.
     * @param artist the artist to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated artist,
     * or with status {@code 400 (Bad Request)} if the artist is not valid,
     * or with status {@code 500 (Internal Server Error)} if the artist couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/artists/{id}")
    public Mono<ResponseEntity<Artist>> updateArtist(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody Artist artist
    ) throws URISyntaxException {
        log.debug("REST request to update Artist : {}, {}", id, artist);
        if (artist.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, artist.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return artistRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return artistRepository
                    .save(artist)
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
     * {@code PATCH  /artists/:id} : Partial updates given fields of an existing artist, field will ignore if it is null
     *
     * @param id the id of the artist to save.
     * @param artist the artist to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated artist,
     * or with status {@code 400 (Bad Request)} if the artist is not valid,
     * or with status {@code 404 (Not Found)} if the artist is not found,
     * or with status {@code 500 (Internal Server Error)} if the artist couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/artists/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<Artist>> partialUpdateArtist(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Artist artist
    ) throws URISyntaxException {
        log.debug("REST request to partial update Artist partially : {}, {}", id, artist);
        if (artist.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, artist.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return artistRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<Artist> result = artistRepository
                    .findById(artist.getId())
                    .map(existingArtist -> {
                        if (artist.getFirstName() != null) {
                            existingArtist.setFirstName(artist.getFirstName());
                        }
                        if (artist.getLastName() != null) {
                            existingArtist.setLastName(artist.getLastName());
                        }
                        if (artist.getGender() != null) {
                            existingArtist.setGender(artist.getGender());
                        }
                        if (artist.getEmail() != null) {
                            existingArtist.setEmail(artist.getEmail());
                        }
                        if (artist.getPhone() != null) {
                            existingArtist.setPhone(artist.getPhone());
                        }
                        if (artist.getAddressLine1() != null) {
                            existingArtist.setAddressLine1(artist.getAddressLine1());
                        }
                        if (artist.getAddressLine2() != null) {
                            existingArtist.setAddressLine2(artist.getAddressLine2());
                        }
                        if (artist.getCity() != null) {
                            existingArtist.setCity(artist.getCity());
                        }
                        if (artist.getCountry() != null) {
                            existingArtist.setCountry(artist.getCountry());
                        }

                        return existingArtist;
                    })
                    .flatMap(artistRepository::save);

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
     * {@code GET  /artists} : get all the artists.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of artists in body.
     */
    @GetMapping("/artists")
    public Mono<List<Artist>> getAllArtists() {
        log.debug("REST request to get all Artists");
        return artistRepository.findAll().collectList();
    }

    /**
     * {@code GET  /artists} : get all the artists as a stream.
     * @return the {@link Flux} of artists.
     */
    @GetMapping(value = "/artists", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<Artist> getAllArtistsAsStream() {
        log.debug("REST request to get all Artists as a stream");
        return artistRepository.findAll();
    }

    /**
     * {@code GET  /artists/:id} : get the "id" artist.
     *
     * @param id the id of the artist to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the artist, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/artists/{id}")
    public Mono<ResponseEntity<Artist>> getArtist(@PathVariable Long id) {
        log.debug("REST request to get Artist : {}", id);
        Mono<Artist> artist = artistRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(artist);
    }

    /**
     * {@code DELETE  /artists/:id} : delete the "id" artist.
     *
     * @param id the id of the artist to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/artists/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public Mono<ResponseEntity<Void>> deleteArtist(@PathVariable Long id) {
        log.debug("REST request to delete Artist : {}", id);
        return artistRepository
            .deleteById(id)
            .map(result ->
                ResponseEntity
                    .noContent()
                    .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
                    .build()
            );
    }
}
