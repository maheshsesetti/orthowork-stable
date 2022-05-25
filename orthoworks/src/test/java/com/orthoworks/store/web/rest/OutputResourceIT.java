package com.orthoworks.store.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.orthoworks.store.IntegrationTest;
import com.orthoworks.store.domain.Output;
import com.orthoworks.store.repository.EntityManager;
import com.orthoworks.store.repository.OutputRepository;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
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
 * Integration tests for the {@link OutputResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
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
    private WebTestClient webTestClient;

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

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Output.class).block();
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
        output = createEntity(em);
    }

    @Test
    void createOutput() throws Exception {
        int databaseSizeBeforeCreate = outputRepository.findAll().collectList().block().size();
        // Create the Output
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(output))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Output in the database
        List<Output> outputList = outputRepository.findAll().collectList().block();
        assertThat(outputList).hasSize(databaseSizeBeforeCreate + 1);
        Output testOutput = outputList.get(outputList.size() - 1);
        assertThat(testOutput.getDate()).isEqualTo(DEFAULT_DATE);
        assertThat(testOutput.getResult()).isEqualTo(DEFAULT_RESULT);
    }

    @Test
    void createOutputWithExistingId() throws Exception {
        // Create the Output with an existing ID
        output.setId(1L);

        int databaseSizeBeforeCreate = outputRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(output))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Output in the database
        List<Output> outputList = outputRepository.findAll().collectList().block();
        assertThat(outputList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void checkDateIsRequired() throws Exception {
        int databaseSizeBeforeTest = outputRepository.findAll().collectList().block().size();
        // set the field null
        output.setDate(null);

        // Create the Output, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(output))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Output> outputList = outputRepository.findAll().collectList().block();
        assertThat(outputList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void getAllOutputs() {
        // Initialize the database
        outputRepository.save(output).block();

        // Get all the outputList
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
            .value(hasItem(output.getId().intValue()))
            .jsonPath("$.[*].date")
            .value(hasItem(DEFAULT_DATE.toString()))
            .jsonPath("$.[*].result")
            .value(hasItem(DEFAULT_RESULT.toString()));
    }

    @Test
    void getOutput() {
        // Initialize the database
        outputRepository.save(output).block();

        // Get the output
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, output.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(output.getId().intValue()))
            .jsonPath("$.date")
            .value(is(DEFAULT_DATE.toString()))
            .jsonPath("$.result")
            .value(is(DEFAULT_RESULT.toString()));
    }

    @Test
    void getNonExistingOutput() {
        // Get the output
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewOutput() throws Exception {
        // Initialize the database
        outputRepository.save(output).block();

        int databaseSizeBeforeUpdate = outputRepository.findAll().collectList().block().size();

        // Update the output
        Output updatedOutput = outputRepository.findById(output.getId()).block();
        updatedOutput.date(UPDATED_DATE).result(UPDATED_RESULT);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedOutput.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedOutput))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Output in the database
        List<Output> outputList = outputRepository.findAll().collectList().block();
        assertThat(outputList).hasSize(databaseSizeBeforeUpdate);
        Output testOutput = outputList.get(outputList.size() - 1);
        assertThat(testOutput.getDate()).isEqualTo(UPDATED_DATE);
        assertThat(testOutput.getResult()).isEqualTo(UPDATED_RESULT);
    }

    @Test
    void putNonExistingOutput() throws Exception {
        int databaseSizeBeforeUpdate = outputRepository.findAll().collectList().block().size();
        output.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, output.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(output))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Output in the database
        List<Output> outputList = outputRepository.findAll().collectList().block();
        assertThat(outputList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchOutput() throws Exception {
        int databaseSizeBeforeUpdate = outputRepository.findAll().collectList().block().size();
        output.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(output))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Output in the database
        List<Output> outputList = outputRepository.findAll().collectList().block();
        assertThat(outputList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamOutput() throws Exception {
        int databaseSizeBeforeUpdate = outputRepository.findAll().collectList().block().size();
        output.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(output))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Output in the database
        List<Output> outputList = outputRepository.findAll().collectList().block();
        assertThat(outputList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateOutputWithPatch() throws Exception {
        // Initialize the database
        outputRepository.save(output).block();

        int databaseSizeBeforeUpdate = outputRepository.findAll().collectList().block().size();

        // Update the output using partial update
        Output partialUpdatedOutput = new Output();
        partialUpdatedOutput.setId(output.getId());

        partialUpdatedOutput.result(UPDATED_RESULT);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedOutput.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedOutput))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Output in the database
        List<Output> outputList = outputRepository.findAll().collectList().block();
        assertThat(outputList).hasSize(databaseSizeBeforeUpdate);
        Output testOutput = outputList.get(outputList.size() - 1);
        assertThat(testOutput.getDate()).isEqualTo(DEFAULT_DATE);
        assertThat(testOutput.getResult()).isEqualTo(UPDATED_RESULT);
    }

    @Test
    void fullUpdateOutputWithPatch() throws Exception {
        // Initialize the database
        outputRepository.save(output).block();

        int databaseSizeBeforeUpdate = outputRepository.findAll().collectList().block().size();

        // Update the output using partial update
        Output partialUpdatedOutput = new Output();
        partialUpdatedOutput.setId(output.getId());

        partialUpdatedOutput.date(UPDATED_DATE).result(UPDATED_RESULT);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedOutput.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedOutput))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Output in the database
        List<Output> outputList = outputRepository.findAll().collectList().block();
        assertThat(outputList).hasSize(databaseSizeBeforeUpdate);
        Output testOutput = outputList.get(outputList.size() - 1);
        assertThat(testOutput.getDate()).isEqualTo(UPDATED_DATE);
        assertThat(testOutput.getResult()).isEqualTo(UPDATED_RESULT);
    }

    @Test
    void patchNonExistingOutput() throws Exception {
        int databaseSizeBeforeUpdate = outputRepository.findAll().collectList().block().size();
        output.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, output.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(output))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Output in the database
        List<Output> outputList = outputRepository.findAll().collectList().block();
        assertThat(outputList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchOutput() throws Exception {
        int databaseSizeBeforeUpdate = outputRepository.findAll().collectList().block().size();
        output.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(output))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Output in the database
        List<Output> outputList = outputRepository.findAll().collectList().block();
        assertThat(outputList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamOutput() throws Exception {
        int databaseSizeBeforeUpdate = outputRepository.findAll().collectList().block().size();
        output.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(output))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Output in the database
        List<Output> outputList = outputRepository.findAll().collectList().block();
        assertThat(outputList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteOutput() {
        // Initialize the database
        outputRepository.save(output).block();

        int databaseSizeBeforeDelete = outputRepository.findAll().collectList().block().size();

        // Delete the output
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, output.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Output> outputList = outputRepository.findAll().collectList().block();
        assertThat(outputList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
