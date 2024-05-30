package guru.springframework.reactivemongo.services;

import guru.springframework.reactivemongo.domain.Beer;
import guru.springframework.reactivemongo.mappers.BeerMapper;
import guru.springframework.reactivemongo.model.BeerDTO;
import guru.springframework.reactivemongo.repositories.BeerRepository;
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
public class BeerServiceImpl implements BeerService {
    private final BeerRepository repository;
    private final BeerMapper mapper;
    private final Validator validator;

    @Override
    public Flux<BeerDTO> beers() {
        return repository
                .findAll()
                .map(mapper::toBeerDto);
    }

    @Override
    public Mono<BeerDTO> saveBeer(BeerDTO beerDTO) {
        if (!isValid(beerDTO))
            return Mono.error(new ResponseStatusException(BAD_REQUEST));
        return repository
                .save(mapper.toBeer(beerDTO))
                .map(mapper::toBeerDto);
    }

    private boolean isValid(BeerDTO beerDTO) {
        return !validator.validateObject(beerDTO).hasErrors();
    }

    @Override
    public Mono<BeerDTO> findById(String id) {
        return repository
                .findById(id)
                .switchIfEmpty(Mono.error(new ResponseStatusException(NOT_FOUND)))
                .map(mapper::toBeerDto);
    }

    @Override
    public Mono<BeerDTO> updateBeer(String id, BeerDTO beerDTO) {
        if (!isValid(beerDTO))
            return Mono.error(new ResponseStatusException(BAD_REQUEST));
        return repository
                .findById(id)
                .switchIfEmpty(Mono.error(new ResponseStatusException(NOT_FOUND)))
                .map(saved -> {
                    updateBeer(beerDTO, saved);
                    return saved;
                })
                .flatMap(repository::save)
                .map(mapper::toBeerDto);
    }

    private void updateBeer(BeerDTO beerDTO, Beer saved) {
        saved.setName(beerDTO.getName());
        saved.setStyle(beerDTO.getStyle());
        saved.setPrice(beerDTO.getPrice());
        saved.setUpc(beerDTO.getUpc());
        saved.setQuantityOnHand(beerDTO.getQuantityOnHand());
    }

    @Override
    public Mono<BeerDTO> patchBeer(String id, BeerDTO beerDTO) {
        if (!isValid(beerDTO))
            return Mono.error(new ResponseStatusException(BAD_REQUEST));
        return repository
                .findById(id)
                .switchIfEmpty(Mono.error(new ResponseStatusException(NOT_FOUND)))
                .map(saved -> {
                    patchBeer(beerDTO, saved);
                    return saved;
                })
                .flatMap(repository::save)
                .map(mapper::toBeerDto);
    }

    private void patchBeer(BeerDTO beerDTO, Beer saved) {
        if (StringUtils.hasText(beerDTO.getName()))
            saved.setName(beerDTO.getName());
        if (StringUtils.hasText(beerDTO.getStyle()))
            saved.setStyle(beerDTO.getStyle());
        if (beerDTO.getPrice() != null)
            saved.setPrice(beerDTO.getPrice());
        if (StringUtils.hasText(beerDTO.getUpc()))
            saved.setUpc(beerDTO.getUpc());
        if (beerDTO.getQuantityOnHand() != null)
            saved.setQuantityOnHand(beerDTO.getQuantityOnHand());
    }

    @Override
    public Mono<Void> deleteBeer(String id) {
        return repository.deleteById(id);
    }

    @Override
    public Mono<BeerDTO> findFirstByName(String name) {
        return repository
                .findFirstByName(name)
                .switchIfEmpty(Mono.error(new ResponseStatusException(NOT_FOUND)))
                .map(mapper::toBeerDto);
    }

    @Override
    public Flux<BeerDTO> findByStyle(String style) {
        return repository
                .findByStyle(style)
                .map(mapper::toBeerDto);
    }
}
