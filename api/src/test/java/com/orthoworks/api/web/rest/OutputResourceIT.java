package com.orthoworks.api.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.orthoworks.api.IntegrationTest;
import com.orthoworks.api.domain.Output;
import com.orthoworks.api.repository.OutputRepository;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
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
 * Integration tests for the {@link OutputResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class OutputResourceIT {

    private static final Instant DEFAULT_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_RESULT = "AAAAAAAAAA";
    private static final String UPDATED_RESULT = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/outputs";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private OutputRepository outputRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restOutputMockMvc;

    private Output output;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Output createEntity(EntityManager em) {
        Output output = new Output().date(DEFAULT_DATE).result(DEFAULT_RESULT);
        return output;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Output createUpdatedEntity(EntityManager em) {
        Output output = new Output().date(UPDATED_DATE).result(UPDATED_RESULT);
        return output;
    }

    @BeforeEach
    public void initTest() {
        output = createEntity(em);
    }

    @Test
    @Transactional
    void createOutput() throws Exception {
        int databaseSizeBeforeCreate = outputRepository.findAll().size();
        // Create the Output
        restOutputMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(output)))
            .andExpect(status().isCreated());

        // Validate the Output in the database
        List<Output> outputList = outputRepository.findAll();
        assertThat(outputList).hasSize(databaseSizeBeforeCreate + 1);
        Output testOutput = outputList.get(outputList.size() - 1);
        assertThat(testOutput.getDate()).isEqualTo(DEFAULT_DATE);
        assertThat(testOutput.getResult()).isEqualTo(DEFAULT_RESULT);
    }

    @Test
    @Transactional
    void createOutputWithExistingId() throws Exception {
        // Create the Output with an existing ID
        output.setId(1L);

        int databaseSizeBeforeCreate = outputRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restOutputMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(output)))
            .andExpect(status().isBadRequest());

        // Validate the Output in the database
        List<Output> outputList = outputRepository.findAll();
        assertThat(outputList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkDateIsRequired() throws Exception {
        int databaseSizeBeforeTest = outputRepository.findAll().size();
        // set the field null
        output.setDate(null);

        // Create the Output, which fails.

        restOutputMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(output)))
            .andExpect(status().isBadRequest());

        List<Output> outputList = outputRepository.findAll();
        assertThat(outputList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllOutputs() throws Exception {
        // Initialize the database
        outputRepository.saveAndFlush(output);

        // Get all the outputList
        restOutputMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(output.getId().intValue())))
            .andExpect(jsonPath("$.[*].date").value(hasItem(DEFAULT_DATE.toString())))
            .andExpect(jsonPath("$.[*].result").value(hasItem(DEFAULT_RESULT.toString())));
    }

    @Test
    @Transactional
    void getOutput() throws Exception {
        // Initialize the database
        outputRepository.saveAndFlush(output);

        // Get the output
        restOutputMockMvc
            .perform(get(ENTITY_API_URL_ID, output.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(output.getId().intValue()))
            .andExpect(jsonPath("$.date").value(DEFAULT_DATE.toString()))
            .andExpect(jsonPath("$.result").value(DEFAULT_RESULT.toString()));
    }

    @Test
    @Transactional
    void getNonExistingOutput() throws Exception {
        // Get the output
        restOutputMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewOutput() throws Exception {
        // Initialize the database
        outputRepository.saveAndFlush(output);

        int databaseSizeBeforeUpdate = outputRepository.findAll().size();

        // Update the output
        Output updatedOutput = outputRepository.findById(output.getId()).get();
        // Disconnect from session so that the updates on updatedOutput are not directly saved in db
        em.detach(updatedOutput);
        updatedOutput.date(UPDATED_DATE).result(UPDATED_RESULT);

        restOutputMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedOutput.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedOutput))
            )
            .andExpect(status().isOk());

        // Validate the Output in the database
        List<Output> outputList = outputRepository.findAll();
        assertThat(outputList).hasSize(databaseSizeBeforeUpdate);
        Output testOutput = outputList.get(outputList.size() - 1);
        assertThat(testOutput.getDate()).isEqualTo(UPDATED_DATE);
        assertThat(testOutput.getResult()).isEqualTo(UPDATED_RESULT);
    }

    @Test
    @Transactional
    void putNonExistingOutput() throws Exception {
        int databaseSizeBeforeUpdate = outputRepository.findAll().size();
        output.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restOutputMockMvc
            .perform(
                put(ENTITY_API_URL_ID, output.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(output))
            )
            .andExpect(status().isBadRequest());

        // Validate the Output in the database
        List<Output> outputList = outputRepository.findAll();
        assertThat(outputList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchOutput() throws Exception {
        int databaseSizeBeforeUpdate = outputRepository.findAll().size();
        output.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restOutputMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(output))
            )
            .andExpect(status().isBadRequest());

        // Validate the Output in the database
        List<Output> outputList = outputRepository.findAll();
        assertThat(outputList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamOutput() throws Exception {
        int databaseSizeBeforeUpdate = outputRepository.findAll().size();
        output.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restOutputMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(output)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Output in the database
        List<Output> outputList = outputRepository.findAll();
        assertThat(outputList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateOutputWithPatch() throws Exception {
        // Initialize the database
        outputRepository.saveAndFlush(output);

        int databaseSizeBeforeUpdate = outputRepository.findAll().size();

        // Update the output using partial update
        Output partialUpdatedOutput = new Output();
        partialUpdatedOutput.setId(output.getId());

        partialUpdatedOutput.result(UPDATED_RESULT);

        restOutputMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedOutput.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedOutput))
            )
            .andExpect(status().isOk());

        // Validate the Output in the database
        List<Output> outputList = outputRepository.findAll();
        assertThat(outputList).hasSize(databaseSizeBeforeUpdate);
        Output testOutput = outputList.get(outputList.size() - 1);
        assertThat(testOutput.getDate()).isEqualTo(DEFAULT_DATE);
        assertThat(testOutput.getResult()).isEqualTo(UPDATED_RESULT);
    }

    @Test
    @Transactional
    void fullUpdateOutputWithPatch() throws Exception {
        // Initialize the database
        outputRepository.saveAndFlush(output);

        int databaseSizeBeforeUpdate = outputRepository.findAll().size();

        // Update the output using partial update
        Output partialUpdatedOutput = new Output();
        partialUpdatedOutput.setId(output.getId());

        partialUpdatedOutput.date(UPDATED_DATE).result(UPDATED_RESULT);

        restOutputMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedOutput.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedOutput))
            )
            .andExpect(status().isOk());

        // Validate the Output in the database
        List<Output> outputList = outputRepository.findAll();
        assertThat(outputList).hasSize(databaseSizeBeforeUpdate);
        Output testOutput = outputList.get(outputList.size() - 1);
        assertThat(testOutput.getDate()).isEqualTo(UPDATED_DATE);
        assertThat(testOutput.getResult()).isEqualTo(UPDATED_RESULT);
    }

    @Test
    @Transactional
    void patchNonExistingOutput() throws Exception {
        int databaseSizeBeforeUpdate = outputRepository.findAll().size();
        output.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restOutputMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, output.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(output))
            )
            .andExpect(status().isBadRequest());

        // Validate the Output in the database
        List<Output> outputList = outputRepository.findAll();
        assertThat(outputList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchOutput() throws Exception {
        int databaseSizeBeforeUpdate = outputRepository.findAll().size();
        output.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restOutputMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(output))
            )
            .andExpect(status().isBadRequest());

        // Validate the Output in the database
        List<Output> outputList = outputRepository.findAll();
        assertThat(outputList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamOutput() throws Exception {
        int databaseSizeBeforeUpdate = outputRepository.findAll().size();
        output.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restOutputMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(output)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Output in the database
        List<Output> outputList = outputRepository.findAll();
        assertThat(outputList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteOutput() throws Exception {
        // Initialize the database
        outputRepository.saveAndFlush(output);

        int databaseSizeBeforeDelete = outputRepository.findAll().size();

        // Delete the output
        restOutputMockMvc
            .perform(delete(ENTITY_API_URL_ID, output.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Output> outputList = outputRepository.findAll();
        assertThat(outputList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
