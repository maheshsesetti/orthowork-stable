package com.orthoworks.common.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.orthoworks.common.IntegrationTest;
import com.orthoworks.common.domain.BrandCategory;
import com.orthoworks.common.repository.BrandCategoryRepository;
import java.time.LocalDate;
import java.time.ZoneId;
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
 * Integration tests for the {@link BrandCategoryResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class BrandCategoryResourceIT {

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final Integer DEFAULT_SORT_ORDER = 1;
    private static final Integer UPDATED_SORT_ORDER = 2;

    private static final LocalDate DEFAULT_DATE_ADDED = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_DATE_ADDED = LocalDate.now(ZoneId.systemDefault());

    private static final LocalDate DEFAULT_DATE_MODIFIED = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_DATE_MODIFIED = LocalDate.now(ZoneId.systemDefault());

    private static final String ENTITY_API_URL = "/api/brand-categories";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private BrandCategoryRepository brandCategoryRepository;

    @Mock
    private BrandCategoryRepository brandCategoryRepositoryMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restBrandCategoryMockMvc;

    private BrandCategory brandCategory;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static BrandCategory createEntity(EntityManager em) {
        BrandCategory brandCategory = new BrandCategory()
            .description(DEFAULT_DESCRIPTION)
            .sortOrder(DEFAULT_SORT_ORDER)
            .dateAdded(DEFAULT_DATE_ADDED)
            .dateModified(DEFAULT_DATE_MODIFIED);
        return brandCategory;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static BrandCategory createUpdatedEntity(EntityManager em) {
        BrandCategory brandCategory = new BrandCategory()
            .description(UPDATED_DESCRIPTION)
            .sortOrder(UPDATED_SORT_ORDER)
            .dateAdded(UPDATED_DATE_ADDED)
            .dateModified(UPDATED_DATE_MODIFIED);
        return brandCategory;
    }

    @BeforeEach
    public void initTest() {
        brandCategory = createEntity(em);
    }

    @Test
    @Transactional
    void createBrandCategory() throws Exception {
        int databaseSizeBeforeCreate = brandCategoryRepository.findAll().size();
        // Create the BrandCategory
        restBrandCategoryMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(brandCategory)))
            .andExpect(status().isCreated());

        // Validate the BrandCategory in the database
        List<BrandCategory> brandCategoryList = brandCategoryRepository.findAll();
        assertThat(brandCategoryList).hasSize(databaseSizeBeforeCreate + 1);
        BrandCategory testBrandCategory = brandCategoryList.get(brandCategoryList.size() - 1);
        assertThat(testBrandCategory.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testBrandCategory.getSortOrder()).isEqualTo(DEFAULT_SORT_ORDER);
        assertThat(testBrandCategory.getDateAdded()).isEqualTo(DEFAULT_DATE_ADDED);
        assertThat(testBrandCategory.getDateModified()).isEqualTo(DEFAULT_DATE_MODIFIED);
    }

    @Test
    @Transactional
    void createBrandCategoryWithExistingId() throws Exception {
        // Create the BrandCategory with an existing ID
        brandCategory.setId(1L);

        int databaseSizeBeforeCreate = brandCategoryRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restBrandCategoryMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(brandCategory)))
            .andExpect(status().isBadRequest());

        // Validate the BrandCategory in the database
        List<BrandCategory> brandCategoryList = brandCategoryRepository.findAll();
        assertThat(brandCategoryList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkDescriptionIsRequired() throws Exception {
        int databaseSizeBeforeTest = brandCategoryRepository.findAll().size();
        // set the field null
        brandCategory.setDescription(null);

        // Create the BrandCategory, which fails.

        restBrandCategoryMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(brandCategory)))
            .andExpect(status().isBadRequest());

        List<BrandCategory> brandCategoryList = brandCategoryRepository.findAll();
        assertThat(brandCategoryList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllBrandCategories() throws Exception {
        // Initialize the database
        brandCategoryRepository.saveAndFlush(brandCategory);

        // Get all the brandCategoryList
        restBrandCategoryMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(brandCategory.getId().intValue())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].sortOrder").value(hasItem(DEFAULT_SORT_ORDER)))
            .andExpect(jsonPath("$.[*].dateAdded").value(hasItem(DEFAULT_DATE_ADDED.toString())))
            .andExpect(jsonPath("$.[*].dateModified").value(hasItem(DEFAULT_DATE_MODIFIED.toString())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllBrandCategoriesWithEagerRelationshipsIsEnabled() throws Exception {
        when(brandCategoryRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restBrandCategoryMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(brandCategoryRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllBrandCategoriesWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(brandCategoryRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restBrandCategoryMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(brandCategoryRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    @Transactional
    void getBrandCategory() throws Exception {
        // Initialize the database
        brandCategoryRepository.saveAndFlush(brandCategory);

        // Get the brandCategory
        restBrandCategoryMockMvc
            .perform(get(ENTITY_API_URL_ID, brandCategory.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(brandCategory.getId().intValue()))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.sortOrder").value(DEFAULT_SORT_ORDER))
            .andExpect(jsonPath("$.dateAdded").value(DEFAULT_DATE_ADDED.toString()))
            .andExpect(jsonPath("$.dateModified").value(DEFAULT_DATE_MODIFIED.toString()));
    }

    @Test
    @Transactional
    void getNonExistingBrandCategory() throws Exception {
        // Get the brandCategory
        restBrandCategoryMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewBrandCategory() throws Exception {
        // Initialize the database
        brandCategoryRepository.saveAndFlush(brandCategory);

        int databaseSizeBeforeUpdate = brandCategoryRepository.findAll().size();

        // Update the brandCategory
        BrandCategory updatedBrandCategory = brandCategoryRepository.findById(brandCategory.getId()).get();
        // Disconnect from session so that the updates on updatedBrandCategory are not directly saved in db
        em.detach(updatedBrandCategory);
        updatedBrandCategory
            .description(UPDATED_DESCRIPTION)
            .sortOrder(UPDATED_SORT_ORDER)
            .dateAdded(UPDATED_DATE_ADDED)
            .dateModified(UPDATED_DATE_MODIFIED);

        restBrandCategoryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedBrandCategory.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedBrandCategory))
            )
            .andExpect(status().isOk());

        // Validate the BrandCategory in the database
        List<BrandCategory> brandCategoryList = brandCategoryRepository.findAll();
        assertThat(brandCategoryList).hasSize(databaseSizeBeforeUpdate);
        BrandCategory testBrandCategory = brandCategoryList.get(brandCategoryList.size() - 1);
        assertThat(testBrandCategory.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testBrandCategory.getSortOrder()).isEqualTo(UPDATED_SORT_ORDER);
        assertThat(testBrandCategory.getDateAdded()).isEqualTo(UPDATED_DATE_ADDED);
        assertThat(testBrandCategory.getDateModified()).isEqualTo(UPDATED_DATE_MODIFIED);
    }

    @Test
    @Transactional
    void putNonExistingBrandCategory() throws Exception {
        int databaseSizeBeforeUpdate = brandCategoryRepository.findAll().size();
        brandCategory.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restBrandCategoryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, brandCategory.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(brandCategory))
            )
            .andExpect(status().isBadRequest());

        // Validate the BrandCategory in the database
        List<BrandCategory> brandCategoryList = brandCategoryRepository.findAll();
        assertThat(brandCategoryList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchBrandCategory() throws Exception {
        int databaseSizeBeforeUpdate = brandCategoryRepository.findAll().size();
        brandCategory.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBrandCategoryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(brandCategory))
            )
            .andExpect(status().isBadRequest());

        // Validate the BrandCategory in the database
        List<BrandCategory> brandCategoryList = brandCategoryRepository.findAll();
        assertThat(brandCategoryList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamBrandCategory() throws Exception {
        int databaseSizeBeforeUpdate = brandCategoryRepository.findAll().size();
        brandCategory.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBrandCategoryMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(brandCategory)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the BrandCategory in the database
        List<BrandCategory> brandCategoryList = brandCategoryRepository.findAll();
        assertThat(brandCategoryList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateBrandCategoryWithPatch() throws Exception {
        // Initialize the database
        brandCategoryRepository.saveAndFlush(brandCategory);

        int databaseSizeBeforeUpdate = brandCategoryRepository.findAll().size();

        // Update the brandCategory using partial update
        BrandCategory partialUpdatedBrandCategory = new BrandCategory();
        partialUpdatedBrandCategory.setId(brandCategory.getId());

        partialUpdatedBrandCategory.description(UPDATED_DESCRIPTION);

        restBrandCategoryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedBrandCategory.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedBrandCategory))
            )
            .andExpect(status().isOk());

        // Validate the BrandCategory in the database
        List<BrandCategory> brandCategoryList = brandCategoryRepository.findAll();
        assertThat(brandCategoryList).hasSize(databaseSizeBeforeUpdate);
        BrandCategory testBrandCategory = brandCategoryList.get(brandCategoryList.size() - 1);
        assertThat(testBrandCategory.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testBrandCategory.getSortOrder()).isEqualTo(DEFAULT_SORT_ORDER);
        assertThat(testBrandCategory.getDateAdded()).isEqualTo(DEFAULT_DATE_ADDED);
        assertThat(testBrandCategory.getDateModified()).isEqualTo(DEFAULT_DATE_MODIFIED);
    }

    @Test
    @Transactional
    void fullUpdateBrandCategoryWithPatch() throws Exception {
        // Initialize the database
        brandCategoryRepository.saveAndFlush(brandCategory);

        int databaseSizeBeforeUpdate = brandCategoryRepository.findAll().size();

        // Update the brandCategory using partial update
        BrandCategory partialUpdatedBrandCategory = new BrandCategory();
        partialUpdatedBrandCategory.setId(brandCategory.getId());

        partialUpdatedBrandCategory
            .description(UPDATED_DESCRIPTION)
            .sortOrder(UPDATED_SORT_ORDER)
            .dateAdded(UPDATED_DATE_ADDED)
            .dateModified(UPDATED_DATE_MODIFIED);

        restBrandCategoryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedBrandCategory.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedBrandCategory))
            )
            .andExpect(status().isOk());

        // Validate the BrandCategory in the database
        List<BrandCategory> brandCategoryList = brandCategoryRepository.findAll();
        assertThat(brandCategoryList).hasSize(databaseSizeBeforeUpdate);
        BrandCategory testBrandCategory = brandCategoryList.get(brandCategoryList.size() - 1);
        assertThat(testBrandCategory.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testBrandCategory.getSortOrder()).isEqualTo(UPDATED_SORT_ORDER);
        assertThat(testBrandCategory.getDateAdded()).isEqualTo(UPDATED_DATE_ADDED);
        assertThat(testBrandCategory.getDateModified()).isEqualTo(UPDATED_DATE_MODIFIED);
    }

    @Test
    @Transactional
    void patchNonExistingBrandCategory() throws Exception {
        int databaseSizeBeforeUpdate = brandCategoryRepository.findAll().size();
        brandCategory.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restBrandCategoryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, brandCategory.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(brandCategory))
            )
            .andExpect(status().isBadRequest());

        // Validate the BrandCategory in the database
        List<BrandCategory> brandCategoryList = brandCategoryRepository.findAll();
        assertThat(brandCategoryList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchBrandCategory() throws Exception {
        int databaseSizeBeforeUpdate = brandCategoryRepository.findAll().size();
        brandCategory.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBrandCategoryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(brandCategory))
            )
            .andExpect(status().isBadRequest());

        // Validate the BrandCategory in the database
        List<BrandCategory> brandCategoryList = brandCategoryRepository.findAll();
        assertThat(brandCategoryList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamBrandCategory() throws Exception {
        int databaseSizeBeforeUpdate = brandCategoryRepository.findAll().size();
        brandCategory.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBrandCategoryMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(brandCategory))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the BrandCategory in the database
        List<BrandCategory> brandCategoryList = brandCategoryRepository.findAll();
        assertThat(brandCategoryList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteBrandCategory() throws Exception {
        // Initialize the database
        brandCategoryRepository.saveAndFlush(brandCategory);

        int databaseSizeBeforeDelete = brandCategoryRepository.findAll().size();

        // Delete the brandCategory
        restBrandCategoryMockMvc
            .perform(delete(ENTITY_API_URL_ID, brandCategory.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<BrandCategory> brandCategoryList = brandCategoryRepository.findAll();
        assertThat(brandCategoryList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
