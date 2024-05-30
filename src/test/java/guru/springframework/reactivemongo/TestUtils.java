package guru.springframework.reactivemongo;

import guru.springframework.reactivemongo.domain.Beer;
import guru.springframework.reactivemongo.domain.Customer;
import guru.springframework.reactivemongo.mappers.BeerMapper;
import guru.springframework.reactivemongo.mappers.BeerMapperImpl;
import guru.springframework.reactivemongo.mappers.CustomerMapper;
import guru.springframework.reactivemongo.mappers.CustomerMapperImpl;
import guru.springframework.reactivemongo.model.BeerDTO;
import guru.springframework.reactivemongo.model.CustomerDTO;
import guru.springframework.reactivemongo.services.BeerService;

import java.math.BigDecimal;

public class TestUtils {
    private static final BeerMapper BEER_MAPPER = new BeerMapperImpl();
    private static final CustomerMapper CUSTOMER_MAPPER = new CustomerMapperImpl();

    public static BeerDTO saveAndGetBeer(BeerService service, BeerMapper mapper) {
        return service
                .saveBeer(mapper.toBeerDto(createTestBeer()))
                .block();
    }

    public static BeerDTO createTestBeerDTO() {
        return BEER_MAPPER.toBeerDto(createTestBeer());
    }

    public static Beer createTestBeer() {
        return Beer.builder()
                .name("Space Dust")
                .style("IPA")
                .price(BigDecimal.TEN)
                .quantityOnHand(12)
                .upc("12323232")
                .build();
    }

    public static CustomerDTO createTestCustomerDTO() {
        return CUSTOMER_MAPPER.toCustomerDTO(createTestCustomer());
    }

    public static Customer createTestCustomer() {
        return Customer.builder()
                .name("Andrew")
                .build();
    }
}
