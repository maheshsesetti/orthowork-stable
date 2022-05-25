package com.orthoworks.store.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.orthoworks.store.IntegrationTest;
import com.orthoworks.store.domain.Data;
import com.orthoworks.store.repository.DataRepository;
import com.orthoworks.store.repository.EntityManager;
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
import org.springframework.util.Base64Utils;

/**
 * Integration tests for the {@link DataResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class DataResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final byte[] DEFAULT_FILE = TestUtil.createByteArray(1, "0");
    private static final byte[] UPDATED_FILE = TestUtil.createByteArray(1, "1");
    private static final String DEFAULT_FILE_CONTENT_TYPE = "image/jpg";
    private static final String UPDATED_FILE_CONTENT_TYPE = "image/png";

    private static final String ENTITY_API_URL = "/api/data";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private DataRepository dataRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Data data;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Data createEntity(EntityManager em) {
        Data data = new Data().name(DEFAULT_NAME).file(DEFAULT_FILE).fileContentType(DEFAULT_FILE_CONTENT_TYPE);
        return data;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Data createUpdatedEntity(EntityManager em) {
        Data data = new Data().name(UPDATED_NAME).file(UPDATED_FILE).fileContentType(UPDATED_FILE_CONTENT_TYPE);
        return data;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Data.class).block();
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
        data = createEntity(em);
    }

    @Test
    void createData() throws Exception {
        int databaseSizeBeforeCreate = dataRepository.findAll().collectList().block().size();
        // Create the Data
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(data))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Data in the database
        List<Data> dataList = dataRepository.findAll().collectList().block();
        assertThat(dataList).hasSize(databaseSizeBeforeCreate + 1);
        Data testData = dataList.get(dataList.size() - 1);
        assertThat(testData.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testData.getFile()).isEqualTo(DEFAULT_FILE);
        assertThat(testData.getFileContentType()).isEqualTo(DEFAULT_FILE_CONTENT_TYPE);
    }

    @Test
    void createDataWithExistingId() throws Exception {
        // Create the Data with an existing ID
        data.setId(1L);

        int databaseSizeBeforeCreate = dataRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(data))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Data in the database
        List<Data> dataList = dataRepository.findAll().collectList().block();
        assertThat(dataList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = dataRepository.findAll().collectList().block().size();
        // set the field null
        data.setName(null);

        // Create the Data, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(data))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Data> dataList = dataRepository.findAll().collectList().block();
        assertThat(dataList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void getAllData() {
        // Initialize the database
        dataRepository.save(data).block();

        // Get all the dataList
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
            .value(hasItem(data.getId().intValue()))
            .jsonPath("$.[*].name")
            .value(hasItem(DEFAULT_NAME))
            .jsonPath("$.[*].fileContentType")
            .value(hasItem(DEFAULT_FILE_CONTENT_TYPE))
            .jsonPath("$.[*].file")
            .value(hasItem(Base64Utils.encodeToString(DEFAULT_FILE)));
    }

    @Test
    void getData() {
        // Initialize the database
        dataRepository.save(data).block();

        // Get the data
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, data.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(data.getId().intValue()))
            .jsonPath("$.name")
            .value(is(DEFAULT_NAME))
            .jsonPath("$.fileContentType")
            .value(is(DEFAULT_FILE_CONTENT_TYPE))
            .jsonPath("$.file")
            .value(is(Base64Utils.encodeToString(DEFAULT_FILE)));
    }

    @Test
    void getNonExistingData() {
        // Get the data
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewData() throws Exception {
        // Initialize the database
        dataRepository.save(data).block();

        int databaseSizeBeforeUpdate = dataRepository.findAll().collectList().block().size();

        // Update the data
        Data updatedData = dataRepository.findById(data.getId()).block();
        updatedData.name(UPDATED_NAME).file(UPDATED_FILE).fileContentType(UPDATED_FILE_CONTENT_TYPE);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedData.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedData))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Data in the database
        List<Data> dataList = dataRepository.findAll().collectList().block();
        assertThat(dataList).hasSize(databaseSizeBeforeUpdate);
        Data testData = dataList.get(dataList.size() - 1);
        assertThat(testData.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testData.getFile()).isEqualTo(UPDATED_FILE);
        assertThat(testData.getFileContentType()).isEqualTo(UPDATED_FILE_CONTENT_TYPE);
    }

    @Test
    void putNonExistingData() throws Exception {
        int databaseSizeBeforeUpdate = dataRepository.findAll().collectList().block().size();
        data.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, data.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(data))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Data in the database
        List<Data> dataList = dataRepository.findAll().collectList().block();
        assertThat(dataList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchData() throws Exception {
        int databaseSizeBeforeUpdate = dataRepository.findAll().collectList().block().size();
        data.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(data))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Data in the database
        List<Data> dataList = dataRepository.findAll().collectList().block();
        assertThat(dataList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamData() throws Exception {
        int databaseSizeBeforeUpdate = dataRepository.findAll().collectList().block().size();
        data.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(data))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Data in the database
        List<Data> dataList = dataRepository.findAll().collectList().block();
        assertThat(dataList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateDataWithPatch() throws Exception {
        // Initialize the database
        dataRepository.save(data).block();

        int databaseSizeBeforeUpdate = dataRepository.findAll().collectList().block().size();

        // Update the data using partial update
        Data partialUpdatedData = new Data();
        partialUpdatedData.setId(data.getId());

        partialUpdatedData.name(UPDATED_NAME).file(UPDATED_FILE).fileContentType(UPDATED_FILE_CONTENT_TYPE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedData.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedData))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Data in the database
        List<Data> dataList = dataRepository.findAll().collectList().block();
        assertThat(dataList).hasSize(databaseSizeBeforeUpdate);
        Data testData = dataList.get(dataList.size() - 1);
        assertThat(testData.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testData.getFile()).isEqualTo(UPDATED_FILE);
        assertThat(testData.getFileContentType()).isEqualTo(UPDATED_FILE_CONTENT_TYPE);
    }

    @Test
    void fullUpdateDataWithPatch() throws Exception {
        // Initialize the database
        dataRepository.save(data).block();

        int databaseSizeBeforeUpdate = dataRepository.findAll().collectList().block().size();

        // Update the data using partial update
        Data partialUpdatedData = new Data();
        partialUpdatedData.setId(data.getId());

        partialUpdatedData.name(UPDATED_NAME).file(UPDATED_FILE).fileContentType(UPDATED_FILE_CONTENT_TYPE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedData.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedData))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Data in the database
        List<Data> dataList = dataRepository.findAll().collectList().block();
        assertThat(dataList).hasSize(databaseSizeBeforeUpdate);
        Data testData = dataList.get(dataList.size() - 1);
        assertThat(testData.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testData.getFile()).isEqualTo(UPDATED_FILE);
        assertThat(testData.getFileContentType()).isEqualTo(UPDATED_FILE_CONTENT_TYPE);
    }

    @Test
    void patchNonExistingData() throws Exception {
        int databaseSizeBeforeUpdate = dataRepository.findAll().collectList().block().size();
        data.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, data.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(data))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Data in the database
        List<Data> dataList = dataRepository.findAll().collectList().block();
        assertThat(dataList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchData() throws Exception {
        int databaseSizeBeforeUpdate = dataRepository.findAll().collectList().block().size();
        data.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(data))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Data in the database
        List<Data> dataList = dataRepository.findAll().collectList().block();
        assertThat(dataList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamData() throws Exception {
        int databaseSizeBeforeUpdate = dataRepository.findAll().collectList().block().size();
        data.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(data))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Data in the database
        List<Data> dataList = dataRepository.findAll().collectList().block();
        assertThat(dataList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteData() {
        // Initialize the database
        dataRepository.save(data).block();

        int databaseSizeBeforeDelete = dataRepository.findAll().collectList().block().size();

        // Delete the data
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, data.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Data> dataList = dataRepository.findAll().collectList().block();
        assertThat(dataList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
