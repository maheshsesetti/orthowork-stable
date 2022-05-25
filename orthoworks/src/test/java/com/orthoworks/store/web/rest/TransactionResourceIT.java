package com.orthoworks.store.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.orthoworks.store.IntegrationTest;
import com.orthoworks.store.domain.Transaction;
import com.orthoworks.store.domain.enumeration.TransactionStatus;
import com.orthoworks.store.repository.EntityManager;
import com.orthoworks.store.repository.TransactionRepository;
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

/**
 * Integration tests for the {@link TransactionResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class TransactionResourceIT {

    private static final String DEFAULT_TITLE = "AAAAAAAAAA";
    private static final String UPDATED_TITLE = "BBBBBBBBBB";

    private static final TransactionStatus DEFAULT_STATUS = TransactionStatus.DRAFT;
    private static final TransactionStatus UPDATED_STATUS = TransactionStatus.SUBMITTED;

    private static final Instant DEFAULT_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/transactions";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Transaction transaction;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Transaction createEntity(EntityManager em) {
        Transaction transaction = new Transaction().title(DEFAULT_TITLE).status(DEFAULT_STATUS).date(DEFAULT_DATE);
        return transaction;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Transaction createUpdatedEntity(EntityManager em) {
        Transaction transaction = new Transaction().title(UPDATED_TITLE).status(UPDATED_STATUS).date(UPDATED_DATE);
        return transaction;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Transaction.class).block();
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
        transaction = createEntity(em);
    }

    @Test
    void createTransaction() throws Exception {
        int databaseSizeBeforeCreate = transactionRepository.findAll().collectList().block().size();
        // Create the Transaction
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(transaction))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Transaction in the database
        List<Transaction> transactionList = transactionRepository.findAll().collectList().block();
        assertThat(transactionList).hasSize(databaseSizeBeforeCreate + 1);
        Transaction testTransaction = transactionList.get(transactionList.size() - 1);
        assertThat(testTransaction.getTitle()).isEqualTo(DEFAULT_TITLE);
        assertThat(testTransaction.getStatus()).isEqualTo(DEFAULT_STATUS);
        assertThat(testTransaction.getDate()).isEqualTo(DEFAULT_DATE);
    }

    @Test
    void createTransactionWithExistingId() throws Exception {
        // Create the Transaction with an existing ID
        transaction.setId(1L);

        int databaseSizeBeforeCreate = transactionRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(transaction))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Transaction in the database
        List<Transaction> transactionList = transactionRepository.findAll().collectList().block();
        assertThat(transactionList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void checkTitleIsRequired() throws Exception {
        int databaseSizeBeforeTest = transactionRepository.findAll().collectList().block().size();
        // set the field null
        transaction.setTitle(null);

        // Create the Transaction, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(transaction))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Transaction> transactionList = transactionRepository.findAll().collectList().block();
        assertThat(transactionList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkDateIsRequired() throws Exception {
        int databaseSizeBeforeTest = transactionRepository.findAll().collectList().block().size();
        // set the field null
        transaction.setDate(null);

        // Create the Transaction, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(transaction))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Transaction> transactionList = transactionRepository.findAll().collectList().block();
        assertThat(transactionList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void getAllTransactions() {
        // Initialize the database
        transactionRepository.save(transaction).block();

        // Get all the transactionList
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
            .value(hasItem(transaction.getId().intValue()))
            .jsonPath("$.[*].title")
            .value(hasItem(DEFAULT_TITLE))
            .jsonPath("$.[*].status")
            .value(hasItem(DEFAULT_STATUS.toString()))
            .jsonPath("$.[*].date")
            .value(hasItem(DEFAULT_DATE.toString()));
    }

    @Test
    void getTransaction() {
        // Initialize the database
        transactionRepository.save(transaction).block();

        // Get the transaction
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, transaction.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(transaction.getId().intValue()))
            .jsonPath("$.title")
            .value(is(DEFAULT_TITLE))
            .jsonPath("$.status")
            .value(is(DEFAULT_STATUS.toString()))
            .jsonPath("$.date")
            .value(is(DEFAULT_DATE.toString()));
    }

    @Test
    void getNonExistingTransaction() {
        // Get the transaction
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewTransaction() throws Exception {
        // Initialize the database
        transactionRepository.save(transaction).block();

        int databaseSizeBeforeUpdate = transactionRepository.findAll().collectList().block().size();

        // Update the transaction
        Transaction updatedTransaction = transactionRepository.findById(transaction.getId()).block();
        updatedTransaction.title(UPDATED_TITLE).status(UPDATED_STATUS).date(UPDATED_DATE);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedTransaction.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedTransaction))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Transaction in the database
        List<Transaction> transactionList = transactionRepository.findAll().collectList().block();
        assertThat(transactionList).hasSize(databaseSizeBeforeUpdate);
        Transaction testTransaction = transactionList.get(transactionList.size() - 1);
        assertThat(testTransaction.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testTransaction.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testTransaction.getDate()).isEqualTo(UPDATED_DATE);
    }

    @Test
    void putNonExistingTransaction() throws Exception {
        int databaseSizeBeforeUpdate = transactionRepository.findAll().collectList().block().size();
        transaction.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, transaction.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(transaction))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Transaction in the database
        List<Transaction> transactionList = transactionRepository.findAll().collectList().block();
        assertThat(transactionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchTransaction() throws Exception {
        int databaseSizeBeforeUpdate = transactionRepository.findAll().collectList().block().size();
        transaction.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(transaction))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Transaction in the database
        List<Transaction> transactionList = transactionRepository.findAll().collectList().block();
        assertThat(transactionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamTransaction() throws Exception {
        int databaseSizeBeforeUpdate = transactionRepository.findAll().collectList().block().size();
        transaction.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(transaction))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Transaction in the database
        List<Transaction> transactionList = transactionRepository.findAll().collectList().block();
        assertThat(transactionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateTransactionWithPatch() throws Exception {
        // Initialize the database
        transactionRepository.save(transaction).block();

        int databaseSizeBeforeUpdate = transactionRepository.findAll().collectList().block().size();

        // Update the transaction using partial update
        Transaction partialUpdatedTransaction = new Transaction();
        partialUpdatedTransaction.setId(transaction.getId());

        partialUpdatedTransaction.status(UPDATED_STATUS);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedTransaction.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedTransaction))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Transaction in the database
        List<Transaction> transactionList = transactionRepository.findAll().collectList().block();
        assertThat(transactionList).hasSize(databaseSizeBeforeUpdate);
        Transaction testTransaction = transactionList.get(transactionList.size() - 1);
        assertThat(testTransaction.getTitle()).isEqualTo(DEFAULT_TITLE);
        assertThat(testTransaction.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testTransaction.getDate()).isEqualTo(DEFAULT_DATE);
    }

    @Test
    void fullUpdateTransactionWithPatch() throws Exception {
        // Initialize the database
        transactionRepository.save(transaction).block();

        int databaseSizeBeforeUpdate = transactionRepository.findAll().collectList().block().size();

        // Update the transaction using partial update
        Transaction partialUpdatedTransaction = new Transaction();
        partialUpdatedTransaction.setId(transaction.getId());

        partialUpdatedTransaction.title(UPDATED_TITLE).status(UPDATED_STATUS).date(UPDATED_DATE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedTransaction.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedTransaction))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Transaction in the database
        List<Transaction> transactionList = transactionRepository.findAll().collectList().block();
        assertThat(transactionList).hasSize(databaseSizeBeforeUpdate);
        Transaction testTransaction = transactionList.get(transactionList.size() - 1);
        assertThat(testTransaction.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testTransaction.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testTransaction.getDate()).isEqualTo(UPDATED_DATE);
    }

    @Test
    void patchNonExistingTransaction() throws Exception {
        int databaseSizeBeforeUpdate = transactionRepository.findAll().collectList().block().size();
        transaction.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, transaction.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(transaction))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Transaction in the database
        List<Transaction> transactionList = transactionRepository.findAll().collectList().block();
        assertThat(transactionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchTransaction() throws Exception {
        int databaseSizeBeforeUpdate = transactionRepository.findAll().collectList().block().size();
        transaction.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(transaction))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Transaction in the database
        List<Transaction> transactionList = transactionRepository.findAll().collectList().block();
        assertThat(transactionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamTransaction() throws Exception {
        int databaseSizeBeforeUpdate = transactionRepository.findAll().collectList().block().size();
        transaction.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(transaction))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Transaction in the database
        List<Transaction> transactionList = transactionRepository.findAll().collectList().block();
        assertThat(transactionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteTransaction() {
        // Initialize the database
        transactionRepository.save(transaction).block();

        int databaseSizeBeforeDelete = transactionRepository.findAll().collectList().block().size();

        // Delete the transaction
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, transaction.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Transaction> transactionList = transactionRepository.findAll().collectList().block();
        assertThat(transactionList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
