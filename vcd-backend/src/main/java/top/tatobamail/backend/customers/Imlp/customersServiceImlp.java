package top.tatobamail.backend.customers.Imlp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.tatobamail.backend.customers.customers;
import top.tatobamail.backend.customers.customersRepository;
import top.tatobamail.backend.customers.customersService;

import java.util.List;

@Service
public class customersServiceImlp implements customersService {

    @Autowired
    private customersRepository customersRepository;

    @Override
    public customers createCustomer(customers customer) {
        return customersRepository.save(customer);
    }

    @Override
    public customers updateCustomer(customers customer) {
        customers existing = customersRepository.findById(customer.getId())
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        existing.setName(customer.getName());
        existing.setPhoneNumber(customer.getPhoneNumber());
        return customersRepository.save(existing);
    }

    @Override
    public void deleteCustomer(Long id) {
        customersRepository.deleteById(id);
    }

    @Override
    public customers getCustomerById(Long id) {
        return customersRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
    }

    @Override
    public List<customers> getAllCustomers() {
        return customersRepository.findAll();
    }

    @Override
    public List<customers> searchCustomers(String keyword) {
        return customersRepository.findByNameContaining(keyword);
    }
}
