package guru.springframework.reactivemongo.services;

import guru.springframework.reactivemongo.domain.Customer;
import guru.springframework.reactivemongo.mappers.CustomerMapper;
import guru.springframework.reactivemongo.model.CustomerDTO;
import guru.springframework.reactivemongo.repositories.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.validation.Validator;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {
    private final CustomerRepository repository;
    private final CustomerMapper mapper;
    private final Validator validator;

    @Override
    public Flux<CustomerDTO> customers() {
        return repository
                .findAll()
                .map(mapper::toCustomerDTO);
    }

    @Override
    public Mono<CustomerDTO> saveCustomer(CustomerDTO customerDTO) {
        if (isInvalid(customerDTO))
            return Mono.error(new ResponseStatusException(BAD_REQUEST));
        return repository
                .save(mapper.toCustomer(customerDTO))
                .map(mapper::toCustomerDTO);
    }

    private boolean isInvalid(CustomerDTO customerDTO) {
        return validator.validateObject(customerDTO).hasErrors();
    }

    @Override
    public Mono<CustomerDTO> findById(String id) {
        return repository
                .findById(id)
                .switchIfEmpty(Mono.error(new ResponseStatusException(NOT_FOUND)))
                .map(mapper::toCustomerDTO);
    }

    @Override
    public Mono<CustomerDTO> updateCustomer(String id, CustomerDTO customerDTO) {
        if (isInvalid(customerDTO))
            return Mono.error(new ResponseStatusException(BAD_REQUEST));
        return repository
                .findById(id)
                .switchIfEmpty(Mono.error(new ResponseStatusException(NOT_FOUND)))
                .map(saved -> {
                    updateCustomer(customerDTO, saved);
                    return saved;
                })
                .flatMap(repository::save)
                .map(mapper::toCustomerDTO);
    }

    private void updateCustomer(CustomerDTO customerDTO, Customer saved) {
        saved.setName(customerDTO.getName());
    }

    @Override
    public Mono<CustomerDTO> patchCustomer(String id, CustomerDTO customerDTO) {
        if (isInvalid(customerDTO))
            return Mono.error(new ResponseStatusException(BAD_REQUEST));
        return repository
                .findById(id)
                .switchIfEmpty(Mono.error(new ResponseStatusException(NOT_FOUND)))
                .map(saved -> {
                    patchCustomer(customerDTO, saved);
                    return saved;
                })
                .flatMap(repository::save)
                .map(mapper::toCustomerDTO);
    }

    private void patchCustomer(CustomerDTO customerDTO, Customer saved) {
        if (StringUtils.hasText(customerDTO.getName()))
            saved.setName(customerDTO.getName());
    }

    @Override
    public Mono<Void> deleteCustomer(String id) {
        return repository.deleteById(id);
    }

    @Override
    public Mono<CustomerDTO> findFirstByName(String name) {
        return repository
                .findFirstByName(name)
                .switchIfEmpty(Mono.error(new ResponseStatusException(NOT_FOUND)))
                .map(mapper::toCustomerDTO);
    }
}
