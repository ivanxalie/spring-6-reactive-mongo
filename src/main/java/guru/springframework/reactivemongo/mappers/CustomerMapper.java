package guru.springframework.reactivemongo.mappers;

import guru.springframework.reactivemongo.domain.Customer;
import guru.springframework.reactivemongo.model.CustomerDTO;
import org.mapstruct.Mapper;

@Mapper
public interface CustomerMapper {
    CustomerDTO toCustomerDTO(Customer customer);

    Customer toCustomer(CustomerDTO customerDTO);
}