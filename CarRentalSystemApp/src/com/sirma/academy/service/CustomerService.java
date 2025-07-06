package com.sirma.academy.service;

import com.sirma.academy.model.Customer;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class CustomerService {
    private List<Customer> customers;
    private FileService fileService;

    private static final String FILE_PATH = "src/com/sirma/academy/data/customers.csv";
    private static final String CSV_HEADER = "id,name,phone,email";

    private final Function<String, Customer> customerParser = line -> {
        String[] parts = line.split(",");
        if (parts.length != 4) {
            System.err.println("Skipping malformed customer line (expected 4 parts): " + line);
            return null;
        }
        try {
            String id = parts[0];
            String name = parts[1];
            String phoneNumber = parts[2];
            String email = parts[3];
            return new Customer(id, name, phoneNumber, email);
        } catch (Exception e) {
            System.err.println("Error parsing customer line: " + line + ". " + e.getMessage());
            return null;
        }
    };

    private final Function<Customer, String> customerFormatter = customer ->
            customer.getId() + "," +
                    customer.getName() + "," +
                    customer.getPhoneNumber() + "," +
                    customer.getEmail();


    public CustomerService() {
        this.fileService = new FileService();

        this.customers = fileService.loadData(FILE_PATH, customerParser).stream()
                .filter(customer -> customer != null)
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);

        System.out.println("Loaded " + customers.size() + " customers from " + FILE_PATH);
    }

    public boolean addCustomer(Customer customer) {
        if (customer == null || customer.getId() == null || customer.getId().isEmpty()) {
            System.out.println("Invalid customer data provided. Cannot add/update customer.");
            return false;
        }

        Optional<Customer> existingCustomerOpt = customers.stream()
                .filter(c -> c.getId().equals(customer.getId()))
                .findFirst();

        if (existingCustomerOpt.isPresent()) {
            Customer existingCustomer = existingCustomerOpt.get();
            existingCustomer.setName(customer.getName());
            existingCustomer.setPhoneNumber(customer.getPhoneNumber());
            existingCustomer.setEmail(customer.getEmail());
            System.out.println("Customer with ID " + customer.getId() + " updated successfully in memory.");
        } else {
            this.customers.add(customer);
            System.out.println("Customer with ID " + customer.getId() + " added successfully in memory.");
        }
        return saveDataToCustomersFile();
    }

    public boolean deleteCustomer(String customerId) {
        boolean removed = customers.removeIf(customer -> customer.getId().equals(customerId));
        if (removed) {
            System.out.println("Customer with ID " + customerId + " removed from memory.");
            return saveDataToCustomersFile();
        } else {
            System.out.println("Customer with ID " + customerId + " not found to delete in memory.");
            return false;
        }
    }

    public boolean saveDataToCustomersFile() {
        boolean saveSuccessful = fileService.saveData(FILE_PATH, CSV_HEADER, customers, customerFormatter);
        if (!saveSuccessful) {
            System.err.println("Failed to save customer data to file.");
        }
        return saveSuccessful;
    }


    public Optional<Customer> findCustomerById(String customerId) {
        return customers.stream()
                .filter(customer -> customer.getId().equals(customerId))
                .findFirst();
    }

    public List<Customer> getCustomers() {
        return new ArrayList<>(customers);
    }
}