package com.orthoworks.store.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import com.orthoworks.store.IntegrationTest;
import com.orthoworks.store.domain.BrandCategory;
import com.orthoworks.store.repository.BrandCategoryRepository;
import com.orthoworks.store.repository.EntityManager;
import java.time.Duration;
import java.time.LocalDate;
import java.time.ZoneId;
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
 * Integration tests for the {@link BrandCategoryResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
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
    private WebTestClient webTestClient;

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

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll("rel_brand_category__brand").block();
            em.deleteAll(BrandCategory.class).block();
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
        brandCategory = createEntity(em);
    }

    @Test
    void createBrandCategory() throws Exception {
        int databaseSizeBeforeCreate = brandCategoryRepository.findAll().collectList().block().size();
        // Create the BrandCategory
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(brandCategory))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the BrandCategory in the database
        List<BrandCategory> brandCategoryList = brandCategoryRepository.findAll().collectList().block();
        assertThat(brandCategoryList).hasSize(databaseSizeBeforeCreate + 1);
        BrandCategory testBrandCategory = brandCategoryList.get(brandCategoryList.size() - 1);
        assertThat(testBrandCategory.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testBrandCategory.getSortOrder()).isEqualTo(DEFAULT_SORT_ORDER);
        assertThat(testBrandCategory.getDateAdded()).isEqualTo(DEFAULT_DATE_ADDED);
        assertThat(testBrandCategory.getDateModified()).isEqualTo(DEFAULT_DATE_MODIFIED);
    }

    @Test
    void createBrandCategoryWithExistingId() throws Exception {
        // Create the BrandCategory with an existing ID
        brandCategory.setId(1L);

        int databaseSizeBeforeCreate = brandCategoryRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(brandCategory))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the BrandCategory in the database
        List<BrandCategory> brandCategoryList = brandCategoryRepository.findAll().collectList().block();
        assertThat(brandCategoryList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void checkDescriptionIsRequired() throws Exception {
        int databaseSizeBeforeTest = brandCategoryRepository.findAll().collectList().block().size();
        // set the field null
        brandCategory.setDescription(null);

        // Create the BrandCategory, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(brandCategory))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<BrandCategory> brandCategoryList = brandCategoryRepository.findAll().collectList().block();
        assertThat(brandCategoryList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void getAllBrandCategories() {
        // Initialize the database
        brandCategoryRepository.save(brandCategory).block();

        // Get all the brandCategoryList
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
            .value(hasItem(brandCategory.getId().intValue()))
            .jsonPath("$.[*].description")
            .value(hasItem(DEFAULT_DESCRIPTION))
            .jsonPath("$.[*].sortOrder")
            .value(hasItem(DEFAULT_SORT_ORDER))
            .jsonPath("$.[*].dateAdded")
            .value(hasItem(DEFAULT_DATE_ADDED.toString()))
            .jsonPath("$.[*].dateModified")
            .value(hasItem(DEFAULT_DATE_MODIFIED.toString()));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllBrandCategoriesWithEagerRelationshipsIsEnabled() {
        when(brandCategoryRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient.get().uri(ENTITY_API_URL + "?eagerload=true").exchange().expectStatus().isOk();

        verify(brandCategoryRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllBrandCategoriesWithEagerRelationshipsIsNotEnabled() {
        when(brandCategoryRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient.get().uri(ENTITY_API_URL + "?eagerload=true").exchange().expectStatus().isOk();

        verify(brandCategoryRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    void getBrandCategory() {
        // Initialize the database
        brandCategoryRepository.save(brandCategory).block();

        // Get the brandCategory
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, brandCategory.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(brandCategory.getId().intValue()))
            .jsonPath("$.description")
            .value(is(DEFAULT_DESCRIPTION))
            .jsonPath("$.sortOrder")
            .value(is(DEFAULT_SORT_ORDER))
            .jsonPath("$.dateAdded")
            .value(is(DEFAULT_DATE_ADDED.toString()))
            .jsonPath("$.dateModified")
            .value(is(DEFAULT_DATE_MODIFIED.toString()));
    }

    @Test
    void getNonExistingBrandCategory() {
        // Get the brandCategory
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewBrandCategory() throws Exception {
        // Initialize the database
        brandCategoryRepository.save(brandCategory).block();

        int databaseSizeBeforeUpdate = brandCategoryRepository.findAll().collectList().block().size();

        // Update the brandCategory
        BrandCategory updatedBrandCategory = brandCategoryRepository.findById(brandCategory.getId()).block();
        updatedBrandCategory
            .description(UPDATED_DESCRIPTION)
            .sortOrder(UPDATED_SORT_ORDER)
            .dateAdded(UPDATED_DATE_ADDED)
            .dateModified(UPDATED_DATE_MODIFIED);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedBrandCategory.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedBrandCategory))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the BrandCategory in the database
        List<BrandCategory> brandCategoryList = brandCategoryRepository.findAll().collectList().block();
        assertThat(brandCategoryList).hasSize(databaseSizeBeforeUpdate);
        BrandCategory testBrandCategory = brandCategoryList.get(brandCategoryList.size() - 1);
        assertThat(testBrandCategory.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testBrandCategory.getSortOrder()).isEqualTo(UPDATED_SORT_ORDER);
        assertThat(testBrandCategory.getDateAdded()).isEqualTo(UPDATED_DATE_ADDED);
        assertThat(testBrandCategory.getDateModified()).isEqualTo(UPDATED_DATE_MODIFIED);
    }

    @Test
    void putNonExistingBrandCategory() throws Exception {
        int databaseSizeBeforeUpdate = brandCategoryRepository.findAll().collectList().block().size();
        brandCategory.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, brandCategory.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(brandCategory))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the BrandCategory in the database
        List<BrandCategory> brandCategoryList = brandCategoryRepository.findAll().collectList().block();
        assertThat(brandCategoryList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchBrandCategory() throws Exception {
        int databaseSizeBeforeUpdate = brandCategoryRepository.findAll().collectList().block().size();
        brandCategory.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(brandCategory))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the BrandCategory in the database
        List<BrandCategory> brandCategoryList = brandCategoryRepository.findAll().collectList().block();
        assertThat(brandCategoryList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamBrandCategory() throws Exception {
        int databaseSizeBeforeUpdate = brandCategoryRepository.findAll().collectList().block().size();
        brandCategory.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(brandCategory))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the BrandCategory in the database
        List<BrandCategory> brandCategoryList = brandCategoryRepository.findAll().collectList().block();
        assertThat(brandCategoryList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateBrandCategoryWithPatch() throws Exception {
        // Initialize the database
        brandCategoryRepository.save(brandCategory).block();

        int databaseSizeBeforeUpdate = brandCategoryRepository.findAll().collectList().block().size();

        // Update the brandCategory using partial update
        BrandCategory partialUpdatedBrandCategory = new BrandCategory();
        partialUpdatedBrandCategory.setId(brandCategory.getId());

        partialUpdatedBrandCategory.description(UPDATED_DESCRIPTION);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedBrandCategory.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedBrandCategory))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the BrandCategory in the database
        List<BrandCategory> brandCategoryList = brandCategoryRepository.findAll().collectList().block();
        assertThat(brandCategoryList).hasSize(databaseSizeBeforeUpdate);
        BrandCategory testBrandCategory = brandCategoryList.get(brandCategoryList.size() - 1);
        assertThat(testBrandCategory.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testBrandCategory.getSortOrder()).isEqualTo(DEFAULT_SORT_ORDER);
        assertThat(testBrandCategory.getDateAdded()).isEqualTo(DEFAULT_DATE_ADDED);
        assertThat(testBrandCategory.getDateModified()).isEqualTo(DEFAULT_DATE_MODIFIED);
    }

    @Test
    void fullUpdateBrandCategoryWithPatch() throws Exception {
        // Initialize the database
        brandCategoryRepository.save(brandCategory).block();

        int databaseSizeBeforeUpdate = brandCategoryRepository.findAll().collectList().block().size();

        // Update the brandCategory using partial update
        BrandCategory partialUpdatedBrandCategory = new BrandCategory();
        partialUpdatedBrandCategory.setId(brandCategory.getId());

        partialUpdatedBrandCategory
            .description(UPDATED_DESCRIPTION)
            .sortOrder(UPDATED_SORT_ORDER)
            .dateAdded(UPDATED_DATE_ADDED)
            .dateModified(UPDATED_DATE_MODIFIED);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedBrandCategory.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedBrandCategory))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the BrandCategory in the database
        List<BrandCategory> brandCategoryList = brandCategoryRepository.findAll().collectList().block();
        assertThat(brandCategoryList).hasSize(databaseSizeBeforeUpdate);
        BrandCategory testBrandCategory = brandCategoryList.get(brandCategoryList.size() - 1);
        assertThat(testBrandCategory.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testBrandCategory.getSortOrder()).isEqualTo(UPDATED_SORT_ORDER);
        assertThat(testBrandCategory.getDateAdded()).isEqualTo(UPDATED_DATE_ADDED);
        assertThat(testBrandCategory.getDateModified()).isEqualTo(UPDATED_DATE_MODIFIED);
    }

    @Test
    void patchNonExistingBrandCategory() throws Exception {
        int databaseSizeBeforeUpdate = brandCategoryRepository.findAll().collectList().block().size();
        brandCategory.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, brandCategory.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(brandCategory))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the BrandCategory in the database
        List<BrandCategory> brandCategoryList = brandCategoryRepository.findAll().collectList().block();
        assertThat(brandCategoryList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchBrandCategory() throws Exception {
        int databaseSizeBeforeUpdate = brandCategoryRepository.findAll().collectList().block().size();
        brandCategory.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(brandCategory))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the BrandCategory in the database
        List<BrandCategory> brandCategoryList = brandCategoryRepository.findAll().collectList().block();
        assertThat(brandCategoryList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamBrandCategory() throws Exception {
        int databaseSizeBeforeUpdate = brandCategoryRepository.findAll().collectList().block().size();
        brandCategory.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(brandCategory))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the BrandCategory in the database
        List<BrandCategory> brandCategoryList = brandCategoryRepository.findAll().collectList().block();
        assertThat(brandCategoryList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteBrandCategory() {
        // Initialize the database
        brandCategoryRepository.save(brandCategory).block();

        int databaseSizeBeforeDelete = brandCategoryRepository.findAll().collectList().block().size();

        // Delete the brandCategory
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, brandCategory.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<BrandCategory> brandCategoryList = brandCategoryRepository.findAll().collectList().block();
        assertThat(brandCategoryList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
