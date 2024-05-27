package guru.springframework.reactivemongo.mappers;

import guru.springframework.reactivemongo.domain.Customer;
import guru.springframework.reactivemongo.model.CustomerDTO;
import org.mapstruct.Mapper;

@Mapper
public interface CustomerMapper {
    Customer toCustomerDTO(CustomerDTO customerDTO);

    CustomerDTO toCustomer(Customer customer);
}