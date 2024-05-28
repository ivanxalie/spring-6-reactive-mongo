package guru.springframework.reactivemongo.bootstrap;

import guru.springframework.reactivemongo.domain.Beer;
import guru.springframework.reactivemongo.domain.Customer;
import guru.springframework.reactivemongo.repositories.BeerRepository;
import guru.springframework.reactivemongo.repositories.CustomerRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.sql.init.dependency.DependsOnDatabaseInitialization;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

import static java.time.LocalDateTime.now;

@Component
@RequiredArgsConstructor
@DependsOnDatabaseInitialization
public class BootstrapDB {
    private final BeerRepository beerRepository;
    private final CustomerRepository customerRepository;

    @PostConstruct
    public void init() {
        beerRepository.deleteAll().doOnSuccess(success -> initBeers()).subscribe();
        customerRepository.deleteAll().doOnSuccess(success -> initCustomers()).subscribe();
    }

    private void initBeers() {
        createAndSaveBeers();
        beerRepository.findAll().subscribe(System.out::println);
    }

    private void createAndSaveBeers() {
        Beer beerDTO1 = Beer.builder()
                .name("Galaxy Cat")
                .style("PALE_ALE")
                .upc("123456")
                .price(BigDecimal.valueOf(12.99))
                .quantityOnHand(122)
                .createdDate(now())
                .lastModifiedDate(now())
                .build();
        Beer beerDTO2 = Beer.builder()
                .name("Crank")
                .style("PALE_ALE")
                .upc("123456890")
                .price(BigDecimal.valueOf(11.99))
                .quantityOnHand(392)
                .createdDate(now())
                .lastModifiedDate(now())
                .build();
        Beer beerDTO3 = Beer.builder()
                .name("Sunshine City")
                .style("IPA")
                .upc("1234")
                .price(BigDecimal.valueOf(13.99))
                .quantityOnHand(144)
                .createdDate(now())
                .lastModifiedDate(now())
                .build();
        beerRepository.saveAll(
                List.of(
                        beerDTO1,
                        beerDTO2,
                        beerDTO3
                )
        ).subscribe();
    }

    private void initCustomers() {
        Customer customerDTO1 = createCustomer("Alex");
        Customer customerDTO2 = createCustomer("Alice");
        Customer customerDTO3 = createCustomer("Roberto");

        customerRepository.saveAll(
                List.of(
                        customerDTO1,
                        customerDTO2,
                        customerDTO3
                )
        ).subscribe();
        customerRepository.findAll().subscribe(System.out::println);
    }

    private Customer createCustomer(String name) {
        return Customer.builder()
                .name(name)
                .createdDate(now())
                .lastModifiedDate(now())
                .build();
    }
}
