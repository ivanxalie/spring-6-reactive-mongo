package guru.springframework.reactivemongo.services;

import guru.springframework.reactivemongo.model.CustomerDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CustomerService {
    Flux<CustomerDTO> customers();

    Mono<CustomerDTO> saveCustomer(CustomerDTO customerDTO);

    Mono<CustomerDTO> findById(String id);

    Mono<CustomerDTO> updateCustomer(String id, CustomerDTO customerDTO);

    Mono<CustomerDTO> patchCustomer(String id, CustomerDTO customerDTO);

    Mono<Void> deleteCustomer(String id);

    Mono<CustomerDTO> findFirstByName(String name);
}
