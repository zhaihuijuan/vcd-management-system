package top.tatobamail.backend.customers;

import java.util.List;

public interface customersService {

    customers createCustomer(customers customer);

    customers updateCustomer(customers customer);

    void deleteCustomer(Long id);

    customers getCustomerById(Long id);

    List<customers> getAllCustomers();

    List<customers> searchCustomers(String keyword);
}
