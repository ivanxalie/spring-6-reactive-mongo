package guru.springframework.reactivemongo.services;

import guru.springframework.reactivemongo.model.BeerDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface BeerService {
    Flux<BeerDTO> beers();

    Mono<BeerDTO> saveBeer(BeerDTO beerDTO);

    Mono<BeerDTO> findById(String id);

    Mono<BeerDTO> updateBeer(String id, BeerDTO beerDTO);

    Mono<BeerDTO> patchBeer(String id, BeerDTO beerDTO);

    Mono<Void> deleteBeer(String id);

    Mono<BeerDTO> findFirstByName(String name);

    Flux<BeerDTO> findByStyle(String style);
}
