package guru.springframework.reactivemongo.web.fn;

import guru.springframework.reactivemongo.model.BeerDTO;
import guru.springframework.reactivemongo.model.CustomerDTO;
import guru.springframework.reactivemongo.services.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.util.Map;

import static guru.springframework.reactivemongo.web.fn.CustomerRouter.CUSTOMER_PATH_ID;
import static org.springframework.web.reactive.function.server.ServerResponse.*;
import static org.springframework.web.util.UriComponentsBuilder.fromHttpUrl;

@Component
@RequiredArgsConstructor
public class CustomerHandler {
    private final CustomerService service;

    public Mono<ServerResponse> customers(ServerRequest request) {
        return ok().body(service.customers(), BeerDTO.class);
    }

    public Mono<ServerResponse> findById(ServerRequest request) {
        return ok().body(service.findById(request.pathVariable("customerId")), CustomerDTO.class);
    }

    public Mono<ServerResponse> createCustomer(ServerRequest request) {
        return request
                .bodyToMono(CustomerDTO.class)
                .flatMap(service::saveCustomer)
                .flatMap(CustomerDTO -> {
                    UriComponentsBuilder builder =
                            fromHttpUrl("http://localhost:8080" + CUSTOMER_PATH_ID);
                    return created(builder.build(Map.of("customerId", CustomerDTO.getId()))).build();
                });
    }

    public Mono<ServerResponse> updateById(ServerRequest request) {
        return request
                .bodyToMono(CustomerDTO.class)
                .flatMap(CustomerDTO ->
                        service.updateCustomer(request.pathVariable("customerId"), CustomerDTO))
                .then(noContent().build());
    }

    public Mono<ServerResponse> patchById(ServerRequest request) {
        return request
                .bodyToMono(CustomerDTO.class)
                .flatMap(CustomerDTO ->
                        service.patchCustomer(request.pathVariable("customerId"), CustomerDTO))
                .flatMap(CustomerDTO -> noContent().build());
    }

    public Mono<ServerResponse> deleteById(ServerRequest request) {
        return service
                .findById(request.pathVariable("customerId"))
                .flatMap(CustomerDTO -> service.deleteCustomer(CustomerDTO.getId()))
                .then(noContent().build());
    }
}
