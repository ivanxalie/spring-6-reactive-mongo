package guru.springframework.reactivemongo.web.fn;

import guru.springframework.reactivemongo.domain.Customer;
import guru.springframework.reactivemongo.model.CustomerDTO;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static guru.springframework.reactivemongo.TestUtils.createTestCustomer;
import static guru.springframework.reactivemongo.TestUtils.createTestCustomerDTO;
import static guru.springframework.reactivemongo.web.fn.CustomerRouter.CUSTOMER_PATH;
import static guru.springframework.reactivemongo.web.fn.CustomerRouter.CUSTOMER_PATH_ID;
import static org.hamcrest.Matchers.greaterThan;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest
@AutoConfigureWebTestClient
public class CustomerEndpointTest {

    @Autowired
    WebTestClient webTestClient;

    @Test
    void testPathIdNotFound() {
        webTestClient.patch()
                .uri(CUSTOMER_PATH_ID, 999)
                .body(Mono.just(createTestCustomer()), CustomerDTO.class)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void testPatchIdFound() {
        CustomerDTO testCustomer = createAndSaveTestCustomer();

        webTestClient.patch()
                .uri(CUSTOMER_PATH_ID, testCustomer.getId())
                .body(Mono.just(testCustomer), CustomerDTO.class)
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    @Order(999)
    void testDeleteCustomer() {
        CustomerDTO testCustomer = createAndSaveTestCustomer();

        webTestClient.delete()
                .uri(CUSTOMER_PATH_ID, testCustomer.getId())
                .exchange()
                .expectStatus()
                .isNoContent();
    }

    @Test
    @Order(4)
    void testUpdateCustomerBadRequest() {
        CustomerDTO testCustomer = createAndSaveTestCustomer();
        testCustomer.setName("");

        webTestClient.put()
                .uri(CUSTOMER_PATH_ID, testCustomer.getId())
                .body(Mono.just(testCustomer), CustomerDTO.class)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void testUpdateCustomerNotFound() {
        webTestClient.put()
                .uri(CUSTOMER_PATH_ID, 999)
                .body(Mono.just(createAndSaveTestCustomer()), CustomerDTO.class)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    @Order(3)
    void testUpdateCustomer() {
        CustomerDTO testCustomer = createAndSaveTestCustomer();

        webTestClient.put()
                .uri(CUSTOMER_PATH_ID, testCustomer.getId())
                .body(Mono.just(testCustomer), CustomerDTO.class)
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    void testCreateCustomerBadData() {
        Customer Customer = createTestCustomer();
        Customer.setName("");

        webTestClient.post()
                .uri(CUSTOMER_PATH)
                .body(Mono.just(Customer), CustomerDTO.class)
                .header("Content-Type", "application/json")
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void testCreateCustomer() {
        webTestClient.post()
                .uri(CUSTOMER_PATH)
                .body(Mono.just(createAndSaveTestCustomer()), CustomerDTO.class)
                .header("Content-Type", "application/json")
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().exists("location");
    }

    @Test
    void testGetByIdNotFound() {
        webTestClient.get()
                .uri(CUSTOMER_PATH_ID, 999)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    @Order(1)
    void testGetById() {
        CustomerDTO testCustomer = createAndSaveTestCustomer();

        webTestClient.get()
                .uri(CUSTOMER_PATH_ID, testCustomer.getId())
                .exchange()
                .expectStatus().isOk()
                .expectHeader().valueEquals("Content-Type", "application/json")
                .expectBody(CustomerDTO.class);
    }

    @Test
    @Order(2)
    void testCustomers() {
        webTestClient.get()
                .uri(CUSTOMER_PATH)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().valueEquals("Content-Type", "application/json")
                .expectBody().jsonPath("$.size()").value(greaterThan(1));
    }

    CustomerDTO createAndSaveTestCustomer() {
        webTestClient
                .post()
                .uri(CUSTOMER_PATH)
                .body(Mono.just(createTestCustomerDTO()), CustomerDTO.class)
                .header("Content-Type", "application/json")
                .exchange()
                .returnResult(CustomerDTO.class);

        return webTestClient
                .get()
                .uri(CUSTOMER_PATH)
                .exchange()
                .returnResult(CustomerDTO.class)
                .getResponseBody()
                .blockFirst();
    }
}
