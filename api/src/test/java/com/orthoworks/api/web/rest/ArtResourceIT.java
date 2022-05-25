package com.orthoworks.api.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.orthoworks.api.IntegrationTest;
import com.orthoworks.api.domain.Art;
import com.orthoworks.api.domain.enumeration.AssetType;
import com.orthoworks.api.domain.enumeration.Type;
import com.orthoworks.api.repository.ArtRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link ArtResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
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
    private MockMvc restArtMockMvc;

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

    @BeforeEach
    public void initTest() {
        art = createEntity(em);
    }

    @Test
    @Transactional
    void createArt() throws Exception {
        int databaseSizeBeforeCreate = artRepository.findAll().size();
        // Create the Art
        restArtMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(art)))
            .andExpect(status().isCreated());

        // Validate the Art in the database
        List<Art> artList = artRepository.findAll();
        assertThat(artList).hasSize(databaseSizeBeforeCreate + 1);
        Art testArt = artList.get(artList.size() - 1);
        assertThat(testArt.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testArt.getHandle()).isEqualTo(DEFAULT_HANDLE);
        assertThat(testArt.getAssetType()).isEqualTo(DEFAULT_ASSET_TYPE);
        assertThat(testArt.getType()).isEqualTo(DEFAULT_TYPE);
    }

    @Test
    @Transactional
    void createArtWithExistingId() throws Exception {
        // Create the Art with an existing ID
        art.setId(1L);

        int databaseSizeBeforeCreate = artRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restArtMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(art)))
            .andExpect(status().isBadRequest());

        // Validate the Art in the database
        List<Art> artList = artRepository.findAll();
        assertThat(artList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = artRepository.findAll().size();
        // set the field null
        art.setName(null);

        // Create the Art, which fails.

        restArtMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(art)))
            .andExpect(status().isBadRequest());

        List<Art> artList = artRepository.findAll();
        assertThat(artList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkHandleIsRequired() throws Exception {
        int databaseSizeBeforeTest = artRepository.findAll().size();
        // set the field null
        art.setHandle(null);

        // Create the Art, which fails.

        restArtMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(art)))
            .andExpect(status().isBadRequest());

        List<Art> artList = artRepository.findAll();
        assertThat(artList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkAssetTypeIsRequired() throws Exception {
        int databaseSizeBeforeTest = artRepository.findAll().size();
        // set the field null
        art.setAssetType(null);

        // Create the Art, which fails.

        restArtMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(art)))
            .andExpect(status().isBadRequest());

        List<Art> artList = artRepository.findAll();
        assertThat(artList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllArts() throws Exception {
        // Initialize the database
        artRepository.saveAndFlush(art);

        // Get all the artList
        restArtMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(art.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].handle").value(hasItem(DEFAULT_HANDLE)))
            .andExpect(jsonPath("$.[*].assetType").value(hasItem(DEFAULT_ASSET_TYPE.toString())))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllArtsWithEagerRelationshipsIsEnabled() throws Exception {
        when(artRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restArtMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(artRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllArtsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(artRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restArtMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(artRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    @Transactional
    void getArt() throws Exception {
        // Initialize the database
        artRepository.saveAndFlush(art);

        // Get the art
        restArtMockMvc
            .perform(get(ENTITY_API_URL_ID, art.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(art.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.handle").value(DEFAULT_HANDLE))
            .andExpect(jsonPath("$.assetType").value(DEFAULT_ASSET_TYPE.toString()))
            .andExpect(jsonPath("$.type").value(DEFAULT_TYPE.toString()));
    }

    @Test
    @Transactional
    void getNonExistingArt() throws Exception {
        // Get the art
        restArtMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewArt() throws Exception {
        // Initialize the database
        artRepository.saveAndFlush(art);

        int databaseSizeBeforeUpdate = artRepository.findAll().size();

        // Update the art
        Art updatedArt = artRepository.findById(art.getId()).get();
        // Disconnect from session so that the updates on updatedArt are not directly saved in db
        em.detach(updatedArt);
        updatedArt.name(UPDATED_NAME).handle(UPDATED_HANDLE).assetType(UPDATED_ASSET_TYPE).type(UPDATED_TYPE);

        restArtMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedArt.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedArt))
            )
            .andExpect(status().isOk());

        // Validate the Art in the database
        List<Art> artList = artRepository.findAll();
        assertThat(artList).hasSize(databaseSizeBeforeUpdate);
        Art testArt = artList.get(artList.size() - 1);
        assertThat(testArt.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testArt.getHandle()).isEqualTo(UPDATED_HANDLE);
        assertThat(testArt.getAssetType()).isEqualTo(UPDATED_ASSET_TYPE);
        assertThat(testArt.getType()).isEqualTo(UPDATED_TYPE);
    }

    @Test
    @Transactional
    void putNonExistingArt() throws Exception {
        int databaseSizeBeforeUpdate = artRepository.findAll().size();
        art.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restArtMockMvc
            .perform(
                put(ENTITY_API_URL_ID, art.getId()).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(art))
            )
            .andExpect(status().isBadRequest());

        // Validate the Art in the database
        List<Art> artList = artRepository.findAll();
        assertThat(artList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchArt() throws Exception {
        int databaseSizeBeforeUpdate = artRepository.findAll().size();
        art.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restArtMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(art))
            )
            .andExpect(status().isBadRequest());

        // Validate the Art in the database
        List<Art> artList = artRepository.findAll();
        assertThat(artList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamArt() throws Exception {
        int databaseSizeBeforeUpdate = artRepository.findAll().size();
        art.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restArtMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(art)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Art in the database
        List<Art> artList = artRepository.findAll();
        assertThat(artList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateArtWithPatch() throws Exception {
        // Initialize the database
        artRepository.saveAndFlush(art);

        int databaseSizeBeforeUpdate = artRepository.findAll().size();

        // Update the art using partial update
        Art partialUpdatedArt = new Art();
        partialUpdatedArt.setId(art.getId());

        partialUpdatedArt.handle(UPDATED_HANDLE).assetType(UPDATED_ASSET_TYPE).type(UPDATED_TYPE);

        restArtMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedArt.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedArt))
            )
            .andExpect(status().isOk());

        // Validate the Art in the database
        List<Art> artList = artRepository.findAll();
        assertThat(artList).hasSize(databaseSizeBeforeUpdate);
        Art testArt = artList.get(artList.size() - 1);
        assertThat(testArt.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testArt.getHandle()).isEqualTo(UPDATED_HANDLE);
        assertThat(testArt.getAssetType()).isEqualTo(UPDATED_ASSET_TYPE);
        assertThat(testArt.getType()).isEqualTo(UPDATED_TYPE);
    }

    @Test
    @Transactional
    void fullUpdateArtWithPatch() throws Exception {
        // Initialize the database
        artRepository.saveAndFlush(art);

        int databaseSizeBeforeUpdate = artRepository.findAll().size();

        // Update the art using partial update
        Art partialUpdatedArt = new Art();
        partialUpdatedArt.setId(art.getId());

        partialUpdatedArt.name(UPDATED_NAME).handle(UPDATED_HANDLE).assetType(UPDATED_ASSET_TYPE).type(UPDATED_TYPE);

        restArtMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedArt.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedArt))
            )
            .andExpect(status().isOk());

        // Validate the Art in the database
        List<Art> artList = artRepository.findAll();
        assertThat(artList).hasSize(databaseSizeBeforeUpdate);
        Art testArt = artList.get(artList.size() - 1);
        assertThat(testArt.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testArt.getHandle()).isEqualTo(UPDATED_HANDLE);
        assertThat(testArt.getAssetType()).isEqualTo(UPDATED_ASSET_TYPE);
        assertThat(testArt.getType()).isEqualTo(UPDATED_TYPE);
    }

    @Test
    @Transactional
    void patchNonExistingArt() throws Exception {
        int databaseSizeBeforeUpdate = artRepository.findAll().size();
        art.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restArtMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, art.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(art))
            )
            .andExpect(status().isBadRequest());

        // Validate the Art in the database
        List<Art> artList = artRepository.findAll();
        assertThat(artList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchArt() throws Exception {
        int databaseSizeBeforeUpdate = artRepository.findAll().size();
        art.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restArtMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(art))
            )
            .andExpect(status().isBadRequest());

        // Validate the Art in the database
        List<Art> artList = artRepository.findAll();
        assertThat(artList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamArt() throws Exception {
        int databaseSizeBeforeUpdate = artRepository.findAll().size();
        art.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restArtMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(art)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Art in the database
        List<Art> artList = artRepository.findAll();
        assertThat(artList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteArt() throws Exception {
        // Initialize the database
        artRepository.saveAndFlush(art);

        int databaseSizeBeforeDelete = artRepository.findAll().size();

        // Delete the art
        restArtMockMvc.perform(delete(ENTITY_API_URL_ID, art.getId()).accept(MediaType.APPLICATION_JSON)).andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Art> artList = artRepository.findAll();
        assertThat(artList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
