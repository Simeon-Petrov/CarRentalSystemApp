package com.sirma.academy.model;

import java.time.LocalDate;

public class Car implements Rentable, Searchable {
    private String id;
    private String make;
    private String model;
    private int year;
    private String type;
    private CarStatus status;
    private String currentRenterId;
    private String currentRenterName;
    private String currentRenterPhone;
    private String currentRenterEmail;
    private LocalDate rentalStartDate;
    private LocalDate expectedReturnDate;
    private double dailyRate;

    public Car() {
    }

    public Car(String id, String make, String model, int year, String type, CarStatus status,
               String currentRenterId, String currentRenterName, String currentRenterPhone,
               String currentRenterEmail, LocalDate rentalStartDate, LocalDate expectedReturnDate,
               double dailyRate) {
        this.id = id;
        this.make = make;
        this.model = model;
        this.year = year;
        this.type = type;
        this.status = status;
        this.currentRenterId = currentRenterId;
        this.currentRenterName = currentRenterName;
        this.currentRenterPhone = currentRenterPhone;
        this.currentRenterEmail = currentRenterEmail;
        this.rentalStartDate = rentalStartDate;
        this.expectedReturnDate = expectedReturnDate;
        this.dailyRate = dailyRate;
    }

    public String getId() {
        return id;
    }

    public String getMake() {
        return make;
    }

    public String getModel() {
        return model;
    }

    public int getYear() {
        return year;
    }

    public String getType() {
        return type;
    }

    public CarStatus getStatus() {
        return status;
    }

    public String getCurrentRenterId() {
        return currentRenterId;
    }

    public String getCurrentRenterName() {
        return currentRenterName;
    }

    public String getCurrentRenterPhone() {
        return currentRenterPhone;
    }

    public String getCurrentRenterEmail() {
        return currentRenterEmail;
    }

    public LocalDate getRentalStartDate() {
        return rentalStartDate;
    }

    public LocalDate getExpectedReturnDate() {
        return expectedReturnDate;
    }

    public double getDailyRate() {
        return dailyRate;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setStatus(CarStatus status) {
        this.status = status;
    }

    public void setCurrentRenterId(String currentRenterId) {
        this.currentRenterId = currentRenterId;
    }

    public void setCurrentRenterName(String currentRenterName) {
        this.currentRenterName = currentRenterName;
    }

    public void setCurrentRenterPhone(String currentRenterPhone) {
        this.currentRenterPhone = currentRenterPhone;
    }

    public void setCurrentRenterEmail(String currentRenterEmail) {
        this.currentRenterEmail = currentRenterEmail;
    }

    public void setRentalStartDate(LocalDate rentalStartDate) {
        this.rentalStartDate = rentalStartDate;
    }

    public void setExpectedReturnDate(LocalDate expectedReturnDate) {
        this.expectedReturnDate = expectedReturnDate;
    }

    public void setDailyRate(double dailyRate) {
        this.dailyRate = dailyRate;
    }

    @Override
    public boolean matches(String criteria) {
        String lowerCaseCriteria = criteria.toLowerCase();
        return this.id.toLowerCase().contains(lowerCaseCriteria) ||
                this.make.toLowerCase().contains(lowerCaseCriteria) ||
                this.model.toLowerCase().contains(lowerCaseCriteria) ||
                this.type.toLowerCase().contains(lowerCaseCriteria) ||
                (this.currentRenterId != null && this.currentRenterId.toLowerCase().contains(lowerCaseCriteria)) ||
                (this.currentRenterName != null && this.currentRenterName.toLowerCase().contains(lowerCaseCriteria)) ||
                (this.currentRenterPhone != null && this.currentRenterPhone.toLowerCase().contains(lowerCaseCriteria)) ||
                (this.currentRenterEmail != null && this.currentRenterEmail.toLowerCase().contains(lowerCaseCriteria)) ||
                (this.rentalStartDate != null && this.rentalStartDate.toString().toLowerCase().contains(lowerCaseCriteria)) ||
                (this.expectedReturnDate != null && this.expectedReturnDate.toString().toLowerCase().contains(lowerCaseCriteria));
    }

    @Override
    public boolean isAvailable() {
        return this.status == CarStatus.AVAILABLE;
    }

    @Override
    public void markAsRented() {
        if (this.status == CarStatus.AVAILABLE) {
            this.status = CarStatus.RENTED;
        } else {
            System.out.println("Car " + id + " cannot be rented as it's not Available. Current status: " + this.status);
        }
    }

    @Override
    public void markAsReturned() {
        if (this.status == CarStatus.RENTED) {
            this.status = CarStatus.AVAILABLE;
            this.currentRenterId = null;
            this.currentRenterName = null;
            this.currentRenterPhone = null;
            this.currentRenterEmail = null;
            this.rentalStartDate = null;
            this.expectedReturnDate = null;
            this.dailyRate = 0.0;
        } else {
            System.out.println("Car " + id + " cannot be returned as it's not Rented. Current status: " + this.status);
        }
    }

    @Override
    public void markAsRemovedFromAutoPark() {
        this.status = CarStatus.REMOVE_FROM_AUTO_PARK;
        this.currentRenterId = null;
        this.currentRenterName = null;
        this.currentRenterPhone = null;
        this.currentRenterEmail = null;
        this.rentalStartDate = null;
        this.expectedReturnDate = null;
        this.dailyRate = 0.0;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Car ID: ").append(id)
                .append(" | Make: ").append(make)
                .append(" | Model: ").append(model)
                .append(" | Year: ").append(year)
                .append(" | Type: ").append(type)
                .append(" | Status: ").append(status);

        if (status == CarStatus.RENTED && currentRenterId != null) {
            sb.append(" | Rented by: ").append(currentRenterName)
                    .append(" (ID: ").append(currentRenterId)
                    .append(", Phone: ").append(currentRenterPhone)
                    .append(", Email: ").append(currentRenterEmail)
                    .append(")")
                    .append(" | From: ").append(rentalStartDate)
                    .append(" | To: ").append(expectedReturnDate)
                    .append(" | Daily Rate: $").append(String.format("%.2f", dailyRate));
        } else if (status == CarStatus.REMOVE_FROM_AUTO_PARK) {
            sb.append(" | Removed from auto park.");
        }
        return sb.toString();
    }
}