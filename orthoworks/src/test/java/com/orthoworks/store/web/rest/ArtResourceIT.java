package com.orthoworks.store.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import com.orthoworks.store.IntegrationTest;
import com.orthoworks.store.domain.Art;
import com.orthoworks.store.domain.enumeration.AssetType;
import com.orthoworks.store.domain.enumeration.Type;
import com.orthoworks.store.repository.ArtRepository;
import com.orthoworks.store.repository.EntityManager;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Integration tests for the {@link ArtResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class ArtResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_HANDLE = "AAAAAAAAAA";
    private static final String UPDATED_HANDLE = "BBBBBBBBBB";

    private static final AssetType DEFAULT_ASSET_TYPE = AssetType.IMAGE;
    private static final AssetType UPDATED_ASSET_TYPE = AssetType.VIDEO;

    private static final Type DEFAULT_TYPE = Type.PHYGITAL;
    private static final Type UPDATED_TYPE = Type.MINIATURE_COLLECTIBLE;

    private static final String ENTITY_API_URL = "/api/arts";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ArtRepository artRepository;

    @Mock
    private ArtRepository artRepositoryMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Art art;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Art createEntity(EntityManager em) {
        Art art = new Art().name(DEFAULT_NAME).handle(DEFAULT_HANDLE).assetType(DEFAULT_ASSET_TYPE).type(DEFAULT_TYPE);
        return art;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Art createUpdatedEntity(EntityManager em) {
        Art art = new Art().name(UPDATED_NAME).handle(UPDATED_HANDLE).assetType(UPDATED_ASSET_TYPE).type(UPDATED_TYPE);
        return art;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll("rel_art__collection").block();
            em.deleteAll(Art.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @AfterEach
    public void cleanup() {
        deleteEntities(em);
    }

    @BeforeEach
    public void initTest() {
        deleteEntities(em);
        art = createEntity(em);
    }

    @Test
    void createArt() throws Exception {
        int databaseSizeBeforeCreate = artRepository.findAll().collectList().block().size();
        // Create the Art
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(art))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Art in the database
        List<Art> artList = artRepository.findAll().collectList().block();
        assertThat(artList).hasSize(databaseSizeBeforeCreate + 1);
        Art testArt = artList.get(artList.size() - 1);
        assertThat(testArt.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testArt.getHandle()).isEqualTo(DEFAULT_HANDLE);
        assertThat(testArt.getAssetType()).isEqualTo(DEFAULT_ASSET_TYPE);
        assertThat(testArt.getType()).isEqualTo(DEFAULT_TYPE);
    }

    @Test
    void createArtWithExistingId() throws Exception {
        // Create the Art with an existing ID
        art.setId(1L);

        int databaseSizeBeforeCreate = artRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(art))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Art in the database
        List<Art> artList = artRepository.findAll().collectList().block();
        assertThat(artList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = artRepository.findAll().collectList().block().size();
        // set the field null
        art.setName(null);

        // Create the Art, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(art))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Art> artList = artRepository.findAll().collectList().block();
        assertThat(artList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkHandleIsRequired() throws Exception {
        int databaseSizeBeforeTest = artRepository.findAll().collectList().block().size();
        // set the field null
        art.setHandle(null);

        // Create the Art, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(art))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Art> artList = artRepository.findAll().collectList().block();
        assertThat(artList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkAssetTypeIsRequired() throws Exception {
        int databaseSizeBeforeTest = artRepository.findAll().collectList().block().size();
        // set the field null
        art.setAssetType(null);

        // Create the Art, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(art))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Art> artList = artRepository.findAll().collectList().block();
        assertThat(artList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void getAllArts() {
        // Initialize the database
        artRepository.save(art).block();

        // Get all the artList
        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?sort=id,desc")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(art.getId().intValue()))
            .jsonPath("$.[*].name")
            .value(hasItem(DEFAULT_NAME))
            .jsonPath("$.[*].handle")
            .value(hasItem(DEFAULT_HANDLE))
            .jsonPath("$.[*].assetType")
            .value(hasItem(DEFAULT_ASSET_TYPE.toString()))
            .jsonPath("$.[*].type")
            .value(hasItem(DEFAULT_TYPE.toString()));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllArtsWithEagerRelationshipsIsEnabled() {
        when(artRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient.get().uri(ENTITY_API_URL + "?eagerload=true").exchange().expectStatus().isOk();

        verify(artRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllArtsWithEagerRelationshipsIsNotEnabled() {
        when(artRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient.get().uri(ENTITY_API_URL + "?eagerload=true").exchange().expectStatus().isOk();

        verify(artRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    void getArt() {
        // Initialize the database
        artRepository.save(art).block();

        // Get the art
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, art.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(art.getId().intValue()))
            .jsonPath("$.name")
            .value(is(DEFAULT_NAME))
            .jsonPath("$.handle")
            .value(is(DEFAULT_HANDLE))
            .jsonPath("$.assetType")
            .value(is(DEFAULT_ASSET_TYPE.toString()))
            .jsonPath("$.type")
            .value(is(DEFAULT_TYPE.toString()));
    }

    @Test
    void getNonExistingArt() {
        // Get the art
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewArt() throws Exception {
        // Initialize the database
        artRepository.save(art).block();

        int databaseSizeBeforeUpdate = artRepository.findAll().collectList().block().size();

        // Update the art
        Art updatedArt = artRepository.findById(art.getId()).block();
        updatedArt.name(UPDATED_NAME).handle(UPDATED_HANDLE).assetType(UPDATED_ASSET_TYPE).type(UPDATED_TYPE);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedArt.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedArt))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Art in the database
        List<Art> artList = artRepository.findAll().collectList().block();
        assertThat(artList).hasSize(databaseSizeBeforeUpdate);
        Art testArt = artList.get(artList.size() - 1);
        assertThat(testArt.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testArt.getHandle()).isEqualTo(UPDATED_HANDLE);
        assertThat(testArt.getAssetType()).isEqualTo(UPDATED_ASSET_TYPE);
        assertThat(testArt.getType()).isEqualTo(UPDATED_TYPE);
    }

    @Test
    void putNonExistingArt() throws Exception {
        int databaseSizeBeforeUpdate = artRepository.findAll().collectList().block().size();
        art.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, art.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(art))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Art in the database
        List<Art> artList = artRepository.findAll().collectList().block();
        assertThat(artList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchArt() throws Exception {
        int databaseSizeBeforeUpdate = artRepository.findAll().collectList().block().size();
        art.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(art))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Art in the database
        List<Art> artList = artRepository.findAll().collectList().block();
        assertThat(artList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamArt() throws Exception {
        int databaseSizeBeforeUpdate = artRepository.findAll().collectList().block().size();
        art.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(art))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Art in the database
        List<Art> artList = artRepository.findAll().collectList().block();
        assertThat(artList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateArtWithPatch() throws Exception {
        // Initialize the database
        artRepository.save(art).block();

        int databaseSizeBeforeUpdate = artRepository.findAll().collectList().block().size();

        // Update the art using partial update
        Art partialUpdatedArt = new Art();
        partialUpdatedArt.setId(art.getId());

        partialUpdatedArt.handle(UPDATED_HANDLE).assetType(UPDATED_ASSET_TYPE).type(UPDATED_TYPE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedArt.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedArt))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Art in the database
        List<Art> artList = artRepository.findAll().collectList().block();
        assertThat(artList).hasSize(databaseSizeBeforeUpdate);
        Art testArt = artList.get(artList.size() - 1);
        assertThat(testArt.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testArt.getHandle()).isEqualTo(UPDATED_HANDLE);
        assertThat(testArt.getAssetType()).isEqualTo(UPDATED_ASSET_TYPE);
        assertThat(testArt.getType()).isEqualTo(UPDATED_TYPE);
    }

    @Test
    void fullUpdateArtWithPatch() throws Exception {
        // Initialize the database
        artRepository.save(art).block();

        int databaseSizeBeforeUpdate = artRepository.findAll().collectList().block().size();

        // Update the art using partial update
        Art partialUpdatedArt = new Art();
        partialUpdatedArt.setId(art.getId());

        partialUpdatedArt.name(UPDATED_NAME).handle(UPDATED_HANDLE).assetType(UPDATED_ASSET_TYPE).type(UPDATED_TYPE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedArt.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedArt))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Art in the database
        List<Art> artList = artRepository.findAll().collectList().block();
        assertThat(artList).hasSize(databaseSizeBeforeUpdate);
        Art testArt = artList.get(artList.size() - 1);
        assertThat(testArt.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testArt.getHandle()).isEqualTo(UPDATED_HANDLE);
        assertThat(testArt.getAssetType()).isEqualTo(UPDATED_ASSET_TYPE);
        assertThat(testArt.getType()).isEqualTo(UPDATED_TYPE);
    }

    @Test
    void patchNonExistingArt() throws Exception {
        int databaseSizeBeforeUpdate = artRepository.findAll().collectList().block().size();
        art.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, art.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(art))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Art in the database
        List<Art> artList = artRepository.findAll().collectList().block();
        assertThat(artList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchArt() throws Exception {
        int databaseSizeBeforeUpdate = artRepository.findAll().collectList().block().size();
        art.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(art))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Art in the database
        List<Art> artList = artRepository.findAll().collectList().block();
        assertThat(artList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamArt() throws Exception {
        int databaseSizeBeforeUpdate = artRepository.findAll().collectList().block().size();
        art.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(art))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Art in the database
        List<Art> artList = artRepository.findAll().collectList().block();
        assertThat(artList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteArt() {
        // Initialize the database
        artRepository.save(art).block();

        int databaseSizeBeforeDelete = artRepository.findAll().collectList().block().size();

        // Delete the art
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, art.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Art> artList = artRepository.findAll().collectList().block();
        assertThat(artList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
