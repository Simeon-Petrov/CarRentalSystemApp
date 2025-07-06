package com.sirma.academy.model;

public class Customer implements Searchable {
    private String id;
    private String name;
    private String phoneNumber;
    private String email;

    public Customer() {
    }

    public Customer(String id, String name, String phoneNumber, String email) {
        this.id = id;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public boolean matches(String criteria) {
        String lowerCaseCriteria = criteria.toLowerCase();
        return this.id.toLowerCase().contains(lowerCaseCriteria) ||
                this.name.toLowerCase().contains(lowerCaseCriteria) ||
                this.phoneNumber.toLowerCase().contains(lowerCaseCriteria) ||
                (this.email != null && this.email.toLowerCase().contains(lowerCaseCriteria));
    }

    @Override
    public String toString() {
        return "Customer ID: " + id +
                " | Name: " + name +
                " | Phone: " + phoneNumber +
                " | Email: " + (email != null ? email : "N/A");
    }
}