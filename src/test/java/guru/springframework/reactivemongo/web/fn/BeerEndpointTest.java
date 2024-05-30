package guru.springframework.reactivemongo.web.fn;

import guru.springframework.reactivemongo.domain.Beer;
import guru.springframework.reactivemongo.model.BeerDTO;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import static guru.springframework.reactivemongo.TestUtils.createTestBeer;
import static guru.springframework.reactivemongo.TestUtils.createTestBeerDTO;
import static guru.springframework.reactivemongo.web.fn.BeerRouter.BEER_PATH;
import static guru.springframework.reactivemongo.web.fn.BeerRouter.BEER_PATH_ID;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest
@AutoConfigureWebTestClient
public class BeerEndpointTest {

    @Autowired
    WebTestClient webTestClient;

    @Test
    void testPathIdNotFound() {
        webTestClient.patch()
                .uri(BEER_PATH_ID, 999)
                .body(Mono.just(createTestBeer()), BeerDTO.class)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void testPatchIdFound() {
        BeerDTO testBeer = createAndSaveTestBeer();

        webTestClient.patch()
                .uri(BEER_PATH_ID, testBeer.getId())
                .body(Mono.just(testBeer), BeerDTO.class)
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    @Order(999)
    void testDeleteBeer() {
        BeerDTO testBeer = createAndSaveTestBeer();

        webTestClient.delete()
                .uri(BEER_PATH_ID, testBeer.getId())
                .exchange()
                .expectStatus()
                .isNoContent();
    }

    @Test
    @Order(4)
    void testUpdateBeerBadRequest() {
        BeerDTO testBeer = createAndSaveTestBeer();
        testBeer.setStyle("");

        webTestClient.put()
                .uri(BEER_PATH_ID, testBeer.getId())
                .body(Mono.just(testBeer), BeerDTO.class)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void testUpdateBeerNotFound() {
        webTestClient.put()
                .uri(BEER_PATH_ID, 999)
                .body(Mono.just(createAndSaveTestBeer()), BeerDTO.class)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    @Order(3)
    void testUpdateBeer() {
        BeerDTO testBeer = createAndSaveTestBeer();

        webTestClient.put()
                .uri(BEER_PATH_ID, testBeer.getId())
                .body(Mono.just(testBeer), BeerDTO.class)
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    void testCreateBeerBadData() {
        Beer beer = createTestBeer();
        beer.setName("");

        webTestClient.post()
                .uri(BEER_PATH)
                .body(Mono.just(beer), BeerDTO.class)
                .header("Content-Type", "application/json")
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void testCreateBeer() {
        webTestClient.post()
                .uri(BEER_PATH)
                .body(Mono.just(createAndSaveTestBeer()), BeerDTO.class)
                .header("Content-Type", "application/json")
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().exists("location");
    }

    @Test
    void testGetByIdNotFound() {
        webTestClient.get()
                .uri(BEER_PATH_ID, 999)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    @Order(1)
    void testGetById() {
        BeerDTO testBeer = createAndSaveTestBeer();

        webTestClient.get()
                .uri(BEER_PATH_ID, testBeer.getId())
                .exchange()
                .expectStatus().isOk()
                .expectHeader().valueEquals("Content-Type", "application/json")
                .expectBody(BeerDTO.class);
    }

    @Test
    @Order(2)
    void testBeers() {
        webTestClient.get()
                .uri(BEER_PATH)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().valueEquals("Content-Type", "application/json")
                .expectBody().jsonPath("$.size()").value(greaterThan(1));
    }

    @Test
    @Order(2)
    void testListBeersByStyle() {
        final String BEER_STYLE = "TEST";
        BeerDTO testDto = createTestBeerDTO();
        testDto.setStyle(BEER_STYLE);

        //create test data
        webTestClient.post().uri(BeerRouter.BEER_PATH)
                .body(Mono.just(testDto), BeerDTO.class)
                .header("Content-Type", "application/json")
                .exchange();

        webTestClient.get().uri(UriComponentsBuilder
                        .fromPath(BeerRouter.BEER_PATH)
                        .queryParam("beerStyle", BEER_STYLE).build().toUri())
                .exchange()
                .expectStatus().isOk()
                .expectHeader().valueEquals("Content-type", "application/json")
                .expectBody().jsonPath("$.size()").value(equalTo(1));
    }

    BeerDTO createAndSaveTestBeer() {
        webTestClient
                .post()
                .uri(BEER_PATH)
                .body(Mono.just(createTestBeerDTO()), BeerDTO.class)
                .header("Content-Type", "application/json")
                .exchange()
                .returnResult(BeerDTO.class);

        return webTestClient
                .get()
                .uri(BEER_PATH)
                .exchange()
                .returnResult(BeerDTO.class)
                .getResponseBody()
                .blockFirst();
    }
}
