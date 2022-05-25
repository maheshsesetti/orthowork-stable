package com.orthoworks.store.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.orthoworks.store.IntegrationTest;
import com.orthoworks.store.domain.Feature;
import com.orthoworks.store.repository.EntityManager;
import com.orthoworks.store.repository.FeatureRepository;
import java.time.Duration;
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
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;

/**
 * Integration tests for the {@link FeatureResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class FeatureResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final Boolean DEFAULT_MANDATORY = false;
    private static final Boolean UPDATED_MANDATORY = true;

    private static final String ENTITY_API_URL = "/api/features";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private FeatureRepository featureRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Feature feature;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Feature createEntity(EntityManager em) {
        Feature feature = new Feature().name(DEFAULT_NAME).mandatory(DEFAULT_MANDATORY);
        return feature;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Feature createUpdatedEntity(EntityManager em) {
        Feature feature = new Feature().name(UPDATED_NAME).mandatory(UPDATED_MANDATORY);
        return feature;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Feature.class).block();
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
        feature = createEntity(em);
    }

    @Test
    void createFeature() throws Exception {
        int databaseSizeBeforeCreate = featureRepository.findAll().collectList().block().size();
        // Create the Feature
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(feature))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Feature in the database
        List<Feature> featureList = featureRepository.findAll().collectList().block();
        assertThat(featureList).hasSize(databaseSizeBeforeCreate + 1);
        Feature testFeature = featureList.get(featureList.size() - 1);
        assertThat(testFeature.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testFeature.getMandatory()).isEqualTo(DEFAULT_MANDATORY);
    }

    @Test
    void createFeatureWithExistingId() throws Exception {
        // Create the Feature with an existing ID
        feature.setId(1L);

        int databaseSizeBeforeCreate = featureRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(feature))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Feature in the database
        List<Feature> featureList = featureRepository.findAll().collectList().block();
        assertThat(featureList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = featureRepository.findAll().collectList().block().size();
        // set the field null
        feature.setName(null);

        // Create the Feature, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(feature))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Feature> featureList = featureRepository.findAll().collectList().block();
        assertThat(featureList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkMandatoryIsRequired() throws Exception {
        int databaseSizeBeforeTest = featureRepository.findAll().collectList().block().size();
        // set the field null
        feature.setMandatory(null);

        // Create the Feature, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(feature))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Feature> featureList = featureRepository.findAll().collectList().block();
        assertThat(featureList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void getAllFeaturesAsStream() {
        // Initialize the database
        featureRepository.save(feature).block();

        List<Feature> featureList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(Feature.class)
            .getResponseBody()
            .filter(feature::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(featureList).isNotNull();
        assertThat(featureList).hasSize(1);
        Feature testFeature = featureList.get(0);
        assertThat(testFeature.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testFeature.getMandatory()).isEqualTo(DEFAULT_MANDATORY);
    }

    @Test
    void getAllFeatures() {
        // Initialize the database
        featureRepository.save(feature).block();

        // Get all the featureList
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
            .value(hasItem(feature.getId().intValue()))
            .jsonPath("$.[*].name")
            .value(hasItem(DEFAULT_NAME))
            .jsonPath("$.[*].mandatory")
            .value(hasItem(DEFAULT_MANDATORY.booleanValue()));
    }

    @Test
    void getFeature() {
        // Initialize the database
        featureRepository.save(feature).block();

        // Get the feature
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, feature.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(feature.getId().intValue()))
            .jsonPath("$.name")
            .value(is(DEFAULT_NAME))
            .jsonPath("$.mandatory")
            .value(is(DEFAULT_MANDATORY.booleanValue()));
    }

    @Test
    void getNonExistingFeature() {
        // Get the feature
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewFeature() throws Exception {
        // Initialize the database
        featureRepository.save(feature).block();

        int databaseSizeBeforeUpdate = featureRepository.findAll().collectList().block().size();

        // Update the feature
        Feature updatedFeature = featureRepository.findById(feature.getId()).block();
        updatedFeature.name(UPDATED_NAME).mandatory(UPDATED_MANDATORY);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedFeature.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedFeature))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Feature in the database
        List<Feature> featureList = featureRepository.findAll().collectList().block();
        assertThat(featureList).hasSize(databaseSizeBeforeUpdate);
        Feature testFeature = featureList.get(featureList.size() - 1);
        assertThat(testFeature.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testFeature.getMandatory()).isEqualTo(UPDATED_MANDATORY);
    }

    @Test
    void putNonExistingFeature() throws Exception {
        int databaseSizeBeforeUpdate = featureRepository.findAll().collectList().block().size();
        feature.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, feature.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(feature))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Feature in the database
        List<Feature> featureList = featureRepository.findAll().collectList().block();
        assertThat(featureList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchFeature() throws Exception {
        int databaseSizeBeforeUpdate = featureRepository.findAll().collectList().block().size();
        feature.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(feature))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Feature in the database
        List<Feature> featureList = featureRepository.findAll().collectList().block();
        assertThat(featureList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamFeature() throws Exception {
        int databaseSizeBeforeUpdate = featureRepository.findAll().collectList().block().size();
        feature.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(feature))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Feature in the database
        List<Feature> featureList = featureRepository.findAll().collectList().block();
        assertThat(featureList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateFeatureWithPatch() throws Exception {
        // Initialize the database
        featureRepository.save(feature).block();

        int databaseSizeBeforeUpdate = featureRepository.findAll().collectList().block().size();

        // Update the feature using partial update
        Feature partialUpdatedFeature = new Feature();
        partialUpdatedFeature.setId(feature.getId());

        partialUpdatedFeature.name(UPDATED_NAME).mandatory(UPDATED_MANDATORY);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedFeature.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedFeature))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Feature in the database
        List<Feature> featureList = featureRepository.findAll().collectList().block();
        assertThat(featureList).hasSize(databaseSizeBeforeUpdate);
        Feature testFeature = featureList.get(featureList.size() - 1);
        assertThat(testFeature.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testFeature.getMandatory()).isEqualTo(UPDATED_MANDATORY);
    }

    @Test
    void fullUpdateFeatureWithPatch() throws Exception {
        // Initialize the database
        featureRepository.save(feature).block();

        int databaseSizeBeforeUpdate = featureRepository.findAll().collectList().block().size();

        // Update the feature using partial update
        Feature partialUpdatedFeature = new Feature();
        partialUpdatedFeature.setId(feature.getId());

        partialUpdatedFeature.name(UPDATED_NAME).mandatory(UPDATED_MANDATORY);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedFeature.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedFeature))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Feature in the database
        List<Feature> featureList = featureRepository.findAll().collectList().block();
        assertThat(featureList).hasSize(databaseSizeBeforeUpdate);
        Feature testFeature = featureList.get(featureList.size() - 1);
        assertThat(testFeature.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testFeature.getMandatory()).isEqualTo(UPDATED_MANDATORY);
    }

    @Test
    void patchNonExistingFeature() throws Exception {
        int databaseSizeBeforeUpdate = featureRepository.findAll().collectList().block().size();
        feature.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, feature.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(feature))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Feature in the database
        List<Feature> featureList = featureRepository.findAll().collectList().block();
        assertThat(featureList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchFeature() throws Exception {
        int databaseSizeBeforeUpdate = featureRepository.findAll().collectList().block().size();
        feature.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(feature))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Feature in the database
        List<Feature> featureList = featureRepository.findAll().collectList().block();
        assertThat(featureList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamFeature() throws Exception {
        int databaseSizeBeforeUpdate = featureRepository.findAll().collectList().block().size();
        feature.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(feature))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Feature in the database
        List<Feature> featureList = featureRepository.findAll().collectList().block();
        assertThat(featureList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteFeature() {
        // Initialize the database
        featureRepository.save(feature).block();

        int databaseSizeBeforeDelete = featureRepository.findAll().collectList().block().size();

        // Delete the feature
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, feature.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Feature> featureList = featureRepository.findAll().collectList().block();
        assertThat(featureList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
