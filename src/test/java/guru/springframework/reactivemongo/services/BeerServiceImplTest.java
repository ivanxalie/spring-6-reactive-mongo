package guru.springframework.reactivemongo.services;

import guru.springframework.reactivemongo.TestUtils;
import guru.springframework.reactivemongo.domain.Beer;
import guru.springframework.reactivemongo.mappers.BeerMapper;
import guru.springframework.reactivemongo.model.BeerDTO;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static guru.springframework.reactivemongo.TestUtils.createTestBeer;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Slf4j
class BeerServiceImplTest {

    @Autowired
    BeerService service;

    @Autowired
    BeerMapper mapper;

    BeerDTO beerDTO;

    @BeforeEach
    void setUp() {
        beerDTO = mapper.toBeerDto(createTestBeer());
    }

    @Test
    @DisplayName("Test Save Beer Using Subscriber")
    void saveBeer() {
        AtomicBoolean result = new AtomicBoolean();
        AtomicReference<BeerDTO> atomicReference = new AtomicReference<>();

        Mono<BeerDTO> savedDTO = service.saveBeer(beerDTO);

        savedDTO.subscribe(saved -> {
            log.info("{}", saved);
            result.set(true);
            atomicReference.set(saved);
        });

        await().untilTrue(result);
        assertThat(atomicReference).doesNotHaveNullValue();
        assertThat(atomicReference.get().getId()).isNotNull();
    }

    @Test
    @DisplayName("Test Save Beer Using Block")
    void saveBeerBlocking() {
        BeerDTO saved = service.saveBeer(mapper.toBeerDto(createTestBeer())).block();

        assertThat(saved).isNotNull();
        assertThat(saved.getId()).isNotNull();
    }

    @Test
    @DisplayName("Test Update Using Block")
    void testUpdateBlocking() {
        String newName = "New Beer Name";
        BeerDTO testBeer = saveAndGetBeer();
        testBeer.setName(newName);

        BeerDTO updated = service.saveBeer(testBeer).block();

        assertThat(updated).isNotNull();

        BeerDTO fetched = service.findById(updated.getId()).block();

        assertThat(fetched).isNotNull();
        assertThat(fetched.getName()).isEqualTo(newName);
    }

    private BeerDTO saveAndGetBeer() {
        return TestUtils.saveAndGetBeer(service, mapper);
    }

    @Test
    @DisplayName("Test Update Using Reactive Streams")
    void testUpdateStreaming() {
        String newName = "New Beer Name";
        AtomicReference<BeerDTO> atomicReference = new AtomicReference<>();

        service
                .saveBeer(mapper.toBeerDto(createTestBeer()))
                .map(saved -> {
                    saved.setName(newName);
                    return saved;
                })
                .flatMap(service::saveBeer)
                .flatMap(updated -> service.findById(updated.getId()))
                .subscribe(atomicReference::set);

        await().until(() -> atomicReference.get() != null);
        assertThat(atomicReference.get().getName()).isEqualTo(newName);
    }

    @Test
    @DisplayName("Test find after delete throws 404")
    void testDelete() {
        BeerDTO beerToDelete = saveAndGetBeer();

        service.deleteBeer(beerToDelete.getId()).block();

        assertThrows(ResponseStatusException.class, () -> service.findById(beerToDelete.getId()).block());
    }

    @Test
    @DisplayName("Test find by First Name")
    void testFindFirstByName() {
        Beer test = createTestBeer();
        AtomicReference<BeerDTO> reference = new AtomicReference<>();

        service
                .findFirstByName(test.getName())
                .subscribe(reference::set);

        await().until(() -> reference.get() != null);
        assertThat(reference.get().getName()).isEqualTo(test.getName());
    }

    @Test
    @DisplayName("Find all beers by style")
    void testFindByStyle() {
        AtomicBoolean result = new AtomicBoolean();
        Beer saved = createTestBeer();

        service.findByStyle(saved.getStyle()).subscribe(dto -> {
            result.set(true);
            log.info("{}", dto);
        });

        await().untilTrue(result);
    }
}