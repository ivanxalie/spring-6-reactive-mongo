package guru.springframework.reactivemongo.mappers;

import guru.springframework.reactivemongo.domain.Beer;
import guru.springframework.reactivemongo.model.BeerDTO;
import org.mapstruct.Mapper;

@Mapper
public interface BeerMapper {
    BeerDTO toBeerDto(Beer beer);

    Beer toBeer(BeerDTO beerDTO);
}
