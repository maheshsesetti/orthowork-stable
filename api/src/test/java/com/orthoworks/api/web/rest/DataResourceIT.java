package com.orthoworks.api.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.orthoworks.api.IntegrationTest;
import com.orthoworks.api.domain.Data;
import com.orthoworks.api.repository.DataRepository;
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
import org.springframework.util.Base64Utils;

/**
 * Integration tests for the {@link DataResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
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
    private MockMvc restDataMockMvc;

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

    @BeforeEach
    public void initTest() {
        data = createEntity(em);
    }

    @Test
    @Transactional
    void createData() throws Exception {
        int databaseSizeBeforeCreate = dataRepository.findAll().size();
        // Create the Data
        restDataMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(data)))
            .andExpect(status().isCreated());

        // Validate the Data in the database
        List<Data> dataList = dataRepository.findAll();
        assertThat(dataList).hasSize(databaseSizeBeforeCreate + 1);
        Data testData = dataList.get(dataList.size() - 1);
        assertThat(testData.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testData.getFile()).isEqualTo(DEFAULT_FILE);
        assertThat(testData.getFileContentType()).isEqualTo(DEFAULT_FILE_CONTENT_TYPE);
    }

    @Test
    @Transactional
    void createDataWithExistingId() throws Exception {
        // Create the Data with an existing ID
        data.setId(1L);

        int databaseSizeBeforeCreate = dataRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restDataMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(data)))
            .andExpect(status().isBadRequest());

        // Validate the Data in the database
        List<Data> dataList = dataRepository.findAll();
        assertThat(dataList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = dataRepository.findAll().size();
        // set the field null
        data.setName(null);

        // Create the Data, which fails.

        restDataMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(data)))
            .andExpect(status().isBadRequest());

        List<Data> dataList = dataRepository.findAll();
        assertThat(dataList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllData() throws Exception {
        // Initialize the database
        dataRepository.saveAndFlush(data);

        // Get all the dataList
        restDataMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(data.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].fileContentType").value(hasItem(DEFAULT_FILE_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].file").value(hasItem(Base64Utils.encodeToString(DEFAULT_FILE))));
    }

    @Test
    @Transactional
    void getData() throws Exception {
        // Initialize the database
        dataRepository.saveAndFlush(data);

        // Get the data
        restDataMockMvc
            .perform(get(ENTITY_API_URL_ID, data.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(data.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.fileContentType").value(DEFAULT_FILE_CONTENT_TYPE))
            .andExpect(jsonPath("$.file").value(Base64Utils.encodeToString(DEFAULT_FILE)));
    }

    @Test
    @Transactional
    void getNonExistingData() throws Exception {
        // Get the data
        restDataMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewData() throws Exception {
        // Initialize the database
        dataRepository.saveAndFlush(data);

        int databaseSizeBeforeUpdate = dataRepository.findAll().size();

        // Update the data
        Data updatedData = dataRepository.findById(data.getId()).get();
        // Disconnect from session so that the updates on updatedData are not directly saved in db
        em.detach(updatedData);
        updatedData.name(UPDATED_NAME).file(UPDATED_FILE).fileContentType(UPDATED_FILE_CONTENT_TYPE);

        restDataMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedData.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedData))
            )
            .andExpect(status().isOk());

        // Validate the Data in the database
        List<Data> dataList = dataRepository.findAll();
        assertThat(dataList).hasSize(databaseSizeBeforeUpdate);
        Data testData = dataList.get(dataList.size() - 1);
        assertThat(testData.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testData.getFile()).isEqualTo(UPDATED_FILE);
        assertThat(testData.getFileContentType()).isEqualTo(UPDATED_FILE_CONTENT_TYPE);
    }

    @Test
    @Transactional
    void putNonExistingData() throws Exception {
        int databaseSizeBeforeUpdate = dataRepository.findAll().size();
        data.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restDataMockMvc
            .perform(
                put(ENTITY_API_URL_ID, data.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(data))
            )
            .andExpect(status().isBadRequest());

        // Validate the Data in the database
        List<Data> dataList = dataRepository.findAll();
        assertThat(dataList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchData() throws Exception {
        int databaseSizeBeforeUpdate = dataRepository.findAll().size();
        data.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDataMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(data))
            )
            .andExpect(status().isBadRequest());

        // Validate the Data in the database
        List<Data> dataList = dataRepository.findAll();
        assertThat(dataList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamData() throws Exception {
        int databaseSizeBeforeUpdate = dataRepository.findAll().size();
        data.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDataMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(data)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Data in the database
        List<Data> dataList = dataRepository.findAll();
        assertThat(dataList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateDataWithPatch() throws Exception {
        // Initialize the database
        dataRepository.saveAndFlush(data);

        int databaseSizeBeforeUpdate = dataRepository.findAll().size();

        // Update the data using partial update
        Data partialUpdatedData = new Data();
        partialUpdatedData.setId(data.getId());

        partialUpdatedData.name(UPDATED_NAME).file(UPDATED_FILE).fileContentType(UPDATED_FILE_CONTENT_TYPE);

        restDataMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedData.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedData))
            )
            .andExpect(status().isOk());

        // Validate the Data in the database
        List<Data> dataList = dataRepository.findAll();
        assertThat(dataList).hasSize(databaseSizeBeforeUpdate);
        Data testData = dataList.get(dataList.size() - 1);
        assertThat(testData.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testData.getFile()).isEqualTo(UPDATED_FILE);
        assertThat(testData.getFileContentType()).isEqualTo(UPDATED_FILE_CONTENT_TYPE);
    }

    @Test
    @Transactional
    void fullUpdateDataWithPatch() throws Exception {
        // Initialize the database
        dataRepository.saveAndFlush(data);

        int databaseSizeBeforeUpdate = dataRepository.findAll().size();

        // Update the data using partial update
        Data partialUpdatedData = new Data();
        partialUpdatedData.setId(data.getId());

        partialUpdatedData.name(UPDATED_NAME).file(UPDATED_FILE).fileContentType(UPDATED_FILE_CONTENT_TYPE);

        restDataMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedData.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedData))
            )
            .andExpect(status().isOk());

        // Validate the Data in the database
        List<Data> dataList = dataRepository.findAll();
        assertThat(dataList).hasSize(databaseSizeBeforeUpdate);
        Data testData = dataList.get(dataList.size() - 1);
        assertThat(testData.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testData.getFile()).isEqualTo(UPDATED_FILE);
        assertThat(testData.getFileContentType()).isEqualTo(UPDATED_FILE_CONTENT_TYPE);
    }

    @Test
    @Transactional
    void patchNonExistingData() throws Exception {
        int databaseSizeBeforeUpdate = dataRepository.findAll().size();
        data.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restDataMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, data.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(data))
            )
            .andExpect(status().isBadRequest());

        // Validate the Data in the database
        List<Data> dataList = dataRepository.findAll();
        assertThat(dataList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchData() throws Exception {
        int databaseSizeBeforeUpdate = dataRepository.findAll().size();
        data.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDataMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(data))
            )
            .andExpect(status().isBadRequest());

        // Validate the Data in the database
        List<Data> dataList = dataRepository.findAll();
        assertThat(dataList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamData() throws Exception {
        int databaseSizeBeforeUpdate = dataRepository.findAll().size();
        data.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDataMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(data)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Data in the database
        List<Data> dataList = dataRepository.findAll();
        assertThat(dataList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteData() throws Exception {
        // Initialize the database
        dataRepository.saveAndFlush(data);

        int databaseSizeBeforeDelete = dataRepository.findAll().size();

        // Delete the data
        restDataMockMvc
            .perform(delete(ENTITY_API_URL_ID, data.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Data> dataList = dataRepository.findAll();
        assertThat(dataList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
