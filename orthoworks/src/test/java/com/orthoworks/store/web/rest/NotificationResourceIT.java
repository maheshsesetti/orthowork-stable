package com.orthoworks.store.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.orthoworks.store.IntegrationTest;
import com.orthoworks.store.domain.Notification;
import com.orthoworks.store.domain.enumeration.NotificationType;
import com.orthoworks.store.repository.EntityManager;
import com.orthoworks.store.repository.NotificationRepository;
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
 * Integration tests for the {@link NotificationResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class NotificationResourceIT {

    private static final Instant DEFAULT_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_DETAILS = "AAAAAAAAAA";
    private static final String UPDATED_DETAILS = "BBBBBBBBBB";

    private static final Instant DEFAULT_SENT_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_SENT_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final NotificationType DEFAULT_FORMAT = NotificationType.EMAIL;
    private static final NotificationType UPDATED_FORMAT = NotificationType.SMS;

    private static final Long DEFAULT_USER_ID = 1L;
    private static final Long UPDATED_USER_ID = 2L;

    private static final Long DEFAULT_BRAND_ID = 1L;
    private static final Long UPDATED_BRAND_ID = 2L;

    private static final String ENTITY_API_URL = "/api/notifications";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Notification notification;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Notification createEntity(EntityManager em) {
        Notification notification = new Notification()
            .date(DEFAULT_DATE)
            .details(DEFAULT_DETAILS)
            .sentDate(DEFAULT_SENT_DATE)
            .format(DEFAULT_FORMAT)
            .userId(DEFAULT_USER_ID)
            .brandId(DEFAULT_BRAND_ID);
        return notification;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Notification createUpdatedEntity(EntityManager em) {
        Notification notification = new Notification()
            .date(UPDATED_DATE)
            .details(UPDATED_DETAILS)
            .sentDate(UPDATED_SENT_DATE)
            .format(UPDATED_FORMAT)
            .userId(UPDATED_USER_ID)
            .brandId(UPDATED_BRAND_ID);
        return notification;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Notification.class).block();
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
        notification = createEntity(em);
    }

    @Test
    void createNotification() throws Exception {
        int databaseSizeBeforeCreate = notificationRepository.findAll().collectList().block().size();
        // Create the Notification
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(notification))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Notification in the database
        List<Notification> notificationList = notificationRepository.findAll().collectList().block();
        assertThat(notificationList).hasSize(databaseSizeBeforeCreate + 1);
        Notification testNotification = notificationList.get(notificationList.size() - 1);
        assertThat(testNotification.getDate()).isEqualTo(DEFAULT_DATE);
        assertThat(testNotification.getDetails()).isEqualTo(DEFAULT_DETAILS);
        assertThat(testNotification.getSentDate()).isEqualTo(DEFAULT_SENT_DATE);
        assertThat(testNotification.getFormat()).isEqualTo(DEFAULT_FORMAT);
        assertThat(testNotification.getUserId()).isEqualTo(DEFAULT_USER_ID);
        assertThat(testNotification.getBrandId()).isEqualTo(DEFAULT_BRAND_ID);
    }

    @Test
    void createNotificationWithExistingId() throws Exception {
        // Create the Notification with an existing ID
        notification.setId(1L);

        int databaseSizeBeforeCreate = notificationRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(notification))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Notification in the database
        List<Notification> notificationList = notificationRepository.findAll().collectList().block();
        assertThat(notificationList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void checkDateIsRequired() throws Exception {
        int databaseSizeBeforeTest = notificationRepository.findAll().collectList().block().size();
        // set the field null
        notification.setDate(null);

        // Create the Notification, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(notification))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Notification> notificationList = notificationRepository.findAll().collectList().block();
        assertThat(notificationList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkSentDateIsRequired() throws Exception {
        int databaseSizeBeforeTest = notificationRepository.findAll().collectList().block().size();
        // set the field null
        notification.setSentDate(null);

        // Create the Notification, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(notification))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Notification> notificationList = notificationRepository.findAll().collectList().block();
        assertThat(notificationList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkFormatIsRequired() throws Exception {
        int databaseSizeBeforeTest = notificationRepository.findAll().collectList().block().size();
        // set the field null
        notification.setFormat(null);

        // Create the Notification, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(notification))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Notification> notificationList = notificationRepository.findAll().collectList().block();
        assertThat(notificationList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkUserIdIsRequired() throws Exception {
        int databaseSizeBeforeTest = notificationRepository.findAll().collectList().block().size();
        // set the field null
        notification.setUserId(null);

        // Create the Notification, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(notification))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Notification> notificationList = notificationRepository.findAll().collectList().block();
        assertThat(notificationList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkBrandIdIsRequired() throws Exception {
        int databaseSizeBeforeTest = notificationRepository.findAll().collectList().block().size();
        // set the field null
        notification.setBrandId(null);

        // Create the Notification, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(notification))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Notification> notificationList = notificationRepository.findAll().collectList().block();
        assertThat(notificationList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void getAllNotificationsAsStream() {
        // Initialize the database
        notificationRepository.save(notification).block();

        List<Notification> notificationList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(Notification.class)
            .getResponseBody()
            .filter(notification::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(notificationList).isNotNull();
        assertThat(notificationList).hasSize(1);
        Notification testNotification = notificationList.get(0);
        assertThat(testNotification.getDate()).isEqualTo(DEFAULT_DATE);
        assertThat(testNotification.getDetails()).isEqualTo(DEFAULT_DETAILS);
        assertThat(testNotification.getSentDate()).isEqualTo(DEFAULT_SENT_DATE);
        assertThat(testNotification.getFormat()).isEqualTo(DEFAULT_FORMAT);
        assertThat(testNotification.getUserId()).isEqualTo(DEFAULT_USER_ID);
        assertThat(testNotification.getBrandId()).isEqualTo(DEFAULT_BRAND_ID);
    }

    @Test
    void getAllNotifications() {
        // Initialize the database
        notificationRepository.save(notification).block();

        // Get all the notificationList
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
            .value(hasItem(notification.getId().intValue()))
            .jsonPath("$.[*].date")
            .value(hasItem(DEFAULT_DATE.toString()))
            .jsonPath("$.[*].details")
            .value(hasItem(DEFAULT_DETAILS))
            .jsonPath("$.[*].sentDate")
            .value(hasItem(DEFAULT_SENT_DATE.toString()))
            .jsonPath("$.[*].format")
            .value(hasItem(DEFAULT_FORMAT.toString()))
            .jsonPath("$.[*].userId")
            .value(hasItem(DEFAULT_USER_ID.intValue()))
            .jsonPath("$.[*].brandId")
            .value(hasItem(DEFAULT_BRAND_ID.intValue()));
    }

    @Test
    void getNotification() {
        // Initialize the database
        notificationRepository.save(notification).block();

        // Get the notification
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, notification.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(notification.getId().intValue()))
            .jsonPath("$.date")
            .value(is(DEFAULT_DATE.toString()))
            .jsonPath("$.details")
            .value(is(DEFAULT_DETAILS))
            .jsonPath("$.sentDate")
            .value(is(DEFAULT_SENT_DATE.toString()))
            .jsonPath("$.format")
            .value(is(DEFAULT_FORMAT.toString()))
            .jsonPath("$.userId")
            .value(is(DEFAULT_USER_ID.intValue()))
            .jsonPath("$.brandId")
            .value(is(DEFAULT_BRAND_ID.intValue()));
    }

    @Test
    void getNonExistingNotification() {
        // Get the notification
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewNotification() throws Exception {
        // Initialize the database
        notificationRepository.save(notification).block();

        int databaseSizeBeforeUpdate = notificationRepository.findAll().collectList().block().size();

        // Update the notification
        Notification updatedNotification = notificationRepository.findById(notification.getId()).block();
        updatedNotification
            .date(UPDATED_DATE)
            .details(UPDATED_DETAILS)
            .sentDate(UPDATED_SENT_DATE)
            .format(UPDATED_FORMAT)
            .userId(UPDATED_USER_ID)
            .brandId(UPDATED_BRAND_ID);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedNotification.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedNotification))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Notification in the database
        List<Notification> notificationList = notificationRepository.findAll().collectList().block();
        assertThat(notificationList).hasSize(databaseSizeBeforeUpdate);
        Notification testNotification = notificationList.get(notificationList.size() - 1);
        assertThat(testNotification.getDate()).isEqualTo(UPDATED_DATE);
        assertThat(testNotification.getDetails()).isEqualTo(UPDATED_DETAILS);
        assertThat(testNotification.getSentDate()).isEqualTo(UPDATED_SENT_DATE);
        assertThat(testNotification.getFormat()).isEqualTo(UPDATED_FORMAT);
        assertThat(testNotification.getUserId()).isEqualTo(UPDATED_USER_ID);
        assertThat(testNotification.getBrandId()).isEqualTo(UPDATED_BRAND_ID);
    }

    @Test
    void putNonExistingNotification() throws Exception {
        int databaseSizeBeforeUpdate = notificationRepository.findAll().collectList().block().size();
        notification.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, notification.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(notification))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Notification in the database
        List<Notification> notificationList = notificationRepository.findAll().collectList().block();
        assertThat(notificationList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchNotification() throws Exception {
        int databaseSizeBeforeUpdate = notificationRepository.findAll().collectList().block().size();
        notification.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(notification))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Notification in the database
        List<Notification> notificationList = notificationRepository.findAll().collectList().block();
        assertThat(notificationList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamNotification() throws Exception {
        int databaseSizeBeforeUpdate = notificationRepository.findAll().collectList().block().size();
        notification.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(notification))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Notification in the database
        List<Notification> notificationList = notificationRepository.findAll().collectList().block();
        assertThat(notificationList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateNotificationWithPatch() throws Exception {
        // Initialize the database
        notificationRepository.save(notification).block();

        int databaseSizeBeforeUpdate = notificationRepository.findAll().collectList().block().size();

        // Update the notification using partial update
        Notification partialUpdatedNotification = new Notification();
        partialUpdatedNotification.setId(notification.getId());

        partialUpdatedNotification.sentDate(UPDATED_SENT_DATE).format(UPDATED_FORMAT).userId(UPDATED_USER_ID).brandId(UPDATED_BRAND_ID);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedNotification.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedNotification))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Notification in the database
        List<Notification> notificationList = notificationRepository.findAll().collectList().block();
        assertThat(notificationList).hasSize(databaseSizeBeforeUpdate);
        Notification testNotification = notificationList.get(notificationList.size() - 1);
        assertThat(testNotification.getDate()).isEqualTo(DEFAULT_DATE);
        assertThat(testNotification.getDetails()).isEqualTo(DEFAULT_DETAILS);
        assertThat(testNotification.getSentDate()).isEqualTo(UPDATED_SENT_DATE);
        assertThat(testNotification.getFormat()).isEqualTo(UPDATED_FORMAT);
        assertThat(testNotification.getUserId()).isEqualTo(UPDATED_USER_ID);
        assertThat(testNotification.getBrandId()).isEqualTo(UPDATED_BRAND_ID);
    }

    @Test
    void fullUpdateNotificationWithPatch() throws Exception {
        // Initialize the database
        notificationRepository.save(notification).block();

        int databaseSizeBeforeUpdate = notificationRepository.findAll().collectList().block().size();

        // Update the notification using partial update
        Notification partialUpdatedNotification = new Notification();
        partialUpdatedNotification.setId(notification.getId());

        partialUpdatedNotification
            .date(UPDATED_DATE)
            .details(UPDATED_DETAILS)
            .sentDate(UPDATED_SENT_DATE)
            .format(UPDATED_FORMAT)
            .userId(UPDATED_USER_ID)
            .brandId(UPDATED_BRAND_ID);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedNotification.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedNotification))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Notification in the database
        List<Notification> notificationList = notificationRepository.findAll().collectList().block();
        assertThat(notificationList).hasSize(databaseSizeBeforeUpdate);
        Notification testNotification = notificationList.get(notificationList.size() - 1);
        assertThat(testNotification.getDate()).isEqualTo(UPDATED_DATE);
        assertThat(testNotification.getDetails()).isEqualTo(UPDATED_DETAILS);
        assertThat(testNotification.getSentDate()).isEqualTo(UPDATED_SENT_DATE);
        assertThat(testNotification.getFormat()).isEqualTo(UPDATED_FORMAT);
        assertThat(testNotification.getUserId()).isEqualTo(UPDATED_USER_ID);
        assertThat(testNotification.getBrandId()).isEqualTo(UPDATED_BRAND_ID);
    }

    @Test
    void patchNonExistingNotification() throws Exception {
        int databaseSizeBeforeUpdate = notificationRepository.findAll().collectList().block().size();
        notification.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, notification.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(notification))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Notification in the database
        List<Notification> notificationList = notificationRepository.findAll().collectList().block();
        assertThat(notificationList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchNotification() throws Exception {
        int databaseSizeBeforeUpdate = notificationRepository.findAll().collectList().block().size();
        notification.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(notification))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Notification in the database
        List<Notification> notificationList = notificationRepository.findAll().collectList().block();
        assertThat(notificationList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamNotification() throws Exception {
        int databaseSizeBeforeUpdate = notificationRepository.findAll().collectList().block().size();
        notification.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(notification))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Notification in the database
        List<Notification> notificationList = notificationRepository.findAll().collectList().block();
        assertThat(notificationList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteNotification() {
        // Initialize the database
        notificationRepository.save(notification).block();

        int databaseSizeBeforeDelete = notificationRepository.findAll().collectList().block().size();

        // Delete the notification
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, notification.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Notification> notificationList = notificationRepository.findAll().collectList().block();
        assertThat(notificationList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
