package com.orthoworks.api.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.orthoworks.api.IntegrationTest;
import com.orthoworks.api.domain.Feature;
import com.orthoworks.api.repository.FeatureRepository;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link FeatureResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
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
    private MockMvc restFeatureMockMvc;

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

    @BeforeEach
    public void initTest() {
        feature = createEntity(em);
    }

    @Test
    @Transactional
    void createFeature() throws Exception {
        int databaseSizeBeforeCreate = featureRepository.findAll().size();
        // Create the Feature
        restFeatureMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(feature)))
            .andExpect(status().isCreated());

        // Validate the Feature in the database
        List<Feature> featureList = featureRepository.findAll();
        assertThat(featureList).hasSize(databaseSizeBeforeCreate + 1);
        Feature testFeature = featureList.get(featureList.size() - 1);
        assertThat(testFeature.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testFeature.getMandatory()).isEqualTo(DEFAULT_MANDATORY);
    }

    @Test
    @Transactional
    void createFeatureWithExistingId() throws Exception {
        // Create the Feature with an existing ID
        feature.setId(1L);

        int databaseSizeBeforeCreate = featureRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restFeatureMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(feature)))
            .andExpect(status().isBadRequest());

        // Validate the Feature in the database
        List<Feature> featureList = featureRepository.findAll();
        assertThat(featureList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = featureRepository.findAll().size();
        // set the field null
        feature.setName(null);

        // Create the Feature, which fails.

        restFeatureMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(feature)))
            .andExpect(status().isBadRequest());

        List<Feature> featureList = featureRepository.findAll();
        assertThat(featureList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkMandatoryIsRequired() throws Exception {
        int databaseSizeBeforeTest = featureRepository.findAll().size();
        // set the field null
        feature.setMandatory(null);

        // Create the Feature, which fails.

        restFeatureMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(feature)))
            .andExpect(status().isBadRequest());

        List<Feature> featureList = featureRepository.findAll();
        assertThat(featureList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllFeatures() throws Exception {
        // Initialize the database
        featureRepository.saveAndFlush(feature);

        // Get all the featureList
        restFeatureMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(feature.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].mandatory").value(hasItem(DEFAULT_MANDATORY.booleanValue())));
    }

    @Test
    @Transactional
    void getFeature() throws Exception {
        // Initialize the database
        featureRepository.saveAndFlush(feature);

        // Get the feature
        restFeatureMockMvc
            .perform(get(ENTITY_API_URL_ID, feature.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(feature.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.mandatory").value(DEFAULT_MANDATORY.booleanValue()));
    }

    @Test
    @Transactional
    void getNonExistingFeature() throws Exception {
        // Get the feature
        restFeatureMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewFeature() throws Exception {
        // Initialize the database
        featureRepository.saveAndFlush(feature);

        int databaseSizeBeforeUpdate = featureRepository.findAll().size();

        // Update the feature
        Feature updatedFeature = featureRepository.findById(feature.getId()).get();
        // Disconnect from session so that the updates on updatedFeature are not directly saved in db
        em.detach(updatedFeature);
        updatedFeature.name(UPDATED_NAME).mandatory(UPDATED_MANDATORY);

        restFeatureMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedFeature.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedFeature))
            )
            .andExpect(status().isOk());

        // Validate the Feature in the database
        List<Feature> featureList = featureRepository.findAll();
        assertThat(featureList).hasSize(databaseSizeBeforeUpdate);
        Feature testFeature = featureList.get(featureList.size() - 1);
        assertThat(testFeature.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testFeature.getMandatory()).isEqualTo(UPDATED_MANDATORY);
    }

    @Test
    @Transactional
    void putNonExistingFeature() throws Exception {
        int databaseSizeBeforeUpdate = featureRepository.findAll().size();
        feature.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restFeatureMockMvc
            .perform(
                put(ENTITY_API_URL_ID, feature.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(feature))
            )
            .andExpect(status().isBadRequest());

        // Validate the Feature in the database
        List<Feature> featureList = featureRepository.findAll();
        assertThat(featureList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchFeature() throws Exception {
        int databaseSizeBeforeUpdate = featureRepository.findAll().size();
        feature.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFeatureMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(feature))
            )
            .andExpect(status().isBadRequest());

        // Validate the Feature in the database
        List<Feature> featureList = featureRepository.findAll();
        assertThat(featureList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamFeature() throws Exception {
        int databaseSizeBeforeUpdate = featureRepository.findAll().size();
        feature.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFeatureMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(feature)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Feature in the database
        List<Feature> featureList = featureRepository.findAll();
        assertThat(featureList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateFeatureWithPatch() throws Exception {
        // Initialize the database
        featureRepository.saveAndFlush(feature);

        int databaseSizeBeforeUpdate = featureRepository.findAll().size();

        // Update the feature using partial update
        Feature partialUpdatedFeature = new Feature();
        partialUpdatedFeature.setId(feature.getId());

        partialUpdatedFeature.name(UPDATED_NAME).mandatory(UPDATED_MANDATORY);

        restFeatureMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedFeature.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedFeature))
            )
            .andExpect(status().isOk());

        // Validate the Feature in the database
        List<Feature> featureList = featureRepository.findAll();
        assertThat(featureList).hasSize(databaseSizeBeforeUpdate);
        Feature testFeature = featureList.get(featureList.size() - 1);
        assertThat(testFeature.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testFeature.getMandatory()).isEqualTo(UPDATED_MANDATORY);
    }

    @Test
    @Transactional
    void fullUpdateFeatureWithPatch() throws Exception {
        // Initialize the database
        featureRepository.saveAndFlush(feature);

        int databaseSizeBeforeUpdate = featureRepository.findAll().size();

        // Update the feature using partial update
        Feature partialUpdatedFeature = new Feature();
        partialUpdatedFeature.setId(feature.getId());

        partialUpdatedFeature.name(UPDATED_NAME).mandatory(UPDATED_MANDATORY);

        restFeatureMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedFeature.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedFeature))
            )
            .andExpect(status().isOk());

        // Validate the Feature in the database
        List<Feature> featureList = featureRepository.findAll();
        assertThat(featureList).hasSize(databaseSizeBeforeUpdate);
        Feature testFeature = featureList.get(featureList.size() - 1);
        assertThat(testFeature.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testFeature.getMandatory()).isEqualTo(UPDATED_MANDATORY);
    }

    @Test
    @Transactional
    void patchNonExistingFeature() throws Exception {
        int databaseSizeBeforeUpdate = featureRepository.findAll().size();
        feature.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restFeatureMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, feature.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(feature))
            )
            .andExpect(status().isBadRequest());

        // Validate the Feature in the database
        List<Feature> featureList = featureRepository.findAll();
        assertThat(featureList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchFeature() throws Exception {
        int databaseSizeBeforeUpdate = featureRepository.findAll().size();
        feature.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFeatureMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(feature))
            )
            .andExpect(status().isBadRequest());

        // Validate the Feature in the database
        List<Feature> featureList = featureRepository.findAll();
        assertThat(featureList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamFeature() throws Exception {
        int databaseSizeBeforeUpdate = featureRepository.findAll().size();
        feature.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFeatureMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(feature)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Feature in the database
        List<Feature> featureList = featureRepository.findAll();
        assertThat(featureList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteFeature() throws Exception {
        // Initialize the database
        featureRepository.saveAndFlush(feature);

        int databaseSizeBeforeDelete = featureRepository.findAll().size();

        // Delete the feature
        restFeatureMockMvc
            .perform(delete(ENTITY_API_URL_ID, feature.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Feature> featureList = featureRepository.findAll();
        assertThat(featureList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
