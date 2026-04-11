package top.tatobamail.backend.customers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
public class customersController {

    @Autowired
    private customersService customersService;

    @GetMapping
    public ResponseEntity<List<customers>> getAllCustomers() {
        return ResponseEntity.ok(customersService.getAllCustomers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<customers> getCustomerById(@PathVariable Long id) {
        return ResponseEntity.ok(customersService.getCustomerById(id));
    }

    @PostMapping
    public ResponseEntity<customers> createCustomer(@RequestBody customers customer) {
        return ResponseEntity.ok(customersService.createCustomer(customer));
    }

    @PutMapping("/{id}")
    public ResponseEntity<customers> updateCustomer(@PathVariable Long id, @RequestBody customers customer) {
        customer.setId(id);
        return ResponseEntity.ok(customersService.updateCustomer(customer));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable Long id) {
        customersService.deleteCustomer(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<customers>> searchCustomers(@RequestParam String keyword) {
        return ResponseEntity.ok(customersService.searchCustomers(keyword));
    }
}
