package guru.springframework.reactivemongo.web.fn;

import guru.springframework.reactivemongo.model.BeerDTO;
import guru.springframework.reactivemongo.services.BeerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.Optional;

import static guru.springframework.reactivemongo.web.fn.BeerRouter.BEER_PATH_ID;
import static org.springframework.web.reactive.function.server.ServerResponse.*;
import static org.springframework.web.util.UriComponentsBuilder.fromHttpUrl;

@Component
@RequiredArgsConstructor
public class BeerHandler {
    private final BeerService service;

    public Mono<ServerResponse> beers(ServerRequest request) {
        Optional<String> beerStyle = request.queryParam("beerStyle");
        return beerStyle.map(s -> ok().body(service.findByStyle(s), BeerDTO.class))
                .orElseGet(() -> ok().body(service.beers(), BeerDTO.class));
    }

    public Mono<ServerResponse> findById(ServerRequest request) {
        return ok().body(service.findById(request.pathVariable("beerId")), BeerDTO.class);
    }

    public Mono<ServerResponse> createBeer(ServerRequest request) {
        return request
                .bodyToMono(BeerDTO.class)
                .flatMap(service::saveBeer)
                .flatMap(beerDTO -> {
                    UriComponentsBuilder builder =
                            fromHttpUrl("http://localhost:8080" + BEER_PATH_ID);
                    return created(builder.build(Map.of("beerId", beerDTO.getId()))).build();
                });
    }

    public Mono<ServerResponse> updateById(ServerRequest request) {
        return request
                .bodyToMono(BeerDTO.class)
                .flatMap(beerDTO ->
                        service.updateBeer(request.pathVariable("beerId"), beerDTO))
                .then(noContent().build());
    }

    public Mono<ServerResponse> patchById(ServerRequest request) {
        return request
                .bodyToMono(BeerDTO.class)
                .flatMap(beerDTO ->
                        service.patchBeer(request.pathVariable("beerId"), beerDTO))
                .flatMap(beerDTO -> noContent().build());
    }

    public Mono<ServerResponse> deleteById(ServerRequest request) {
        return service
                .findById(request.pathVariable("beerId"))
                .flatMap(beerDTO -> service.deleteBeer(beerDTO.getId()))
                .then(noContent().build());
    }
}
