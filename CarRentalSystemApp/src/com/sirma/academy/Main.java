package com.sirma.academy;

import com.sirma.academy.model.Car;
import com.sirma.academy.model.CarStatus;
import com.sirma.academy.model.Customer;
import com.sirma.academy.service.CarService;
import com.sirma.academy.service.CustomerService;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Main {

    private static CarService carService;
    private static CustomerService customerService;
    private static Scanner scanner;

    public static void main(String[] args) {
        System.out.println("Hello and welcome to Car Rental System!");

        carService = new CarService();
        customerService = new CustomerService();

        scanner = new Scanner(System.in);

        runMenu();

        scanner.close();
        System.out.println("Exiting Car Rental System. Goodbye!");
    }

    private static void runMenu() {
        int choice;
        do {
            displayMenu();
            System.out.print("Enter your choice: ");
            try {
                choice = scanner.nextInt();
                scanner.nextLine();

                switch (choice) {
                    case 1:
                        listAllCars();
                        break;
                    case 2:
                        listAllCustomers();
                        break;
                    case 3:
                        addCar();
                        break;
                    case 4:
                        addCustomer();
                        break;
                    case 5:
                        rentCar();
                        break;
                    case 6:
                        returnCar();
                        break;
                    case 7:
                        listAllRentedCars();
                        break;
                    case 8:
                        updateCarsMenu();
                        break;
                    case 9:
                        updateCustomersMenu();
                        break;
                    case 0:
                        System.out.println("Exiting application...");
                        break;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a number.");
                scanner.nextLine();
                choice = -1;
            } catch (Exception e) {
                System.out.println("An unexpected error occurred: " + e.getMessage());
                e.printStackTrace();
                choice = -1;
            }
            System.out.println();
        } while (choice != 0);
    }

    private static void displayMenu() {
        System.out.println("--- Car Rental System Menu ---");
        System.out.println("1. List all cars (Available and Rented)");
        System.out.println("2. List all customers");
        System.out.println("3. Add a new Car");
        System.out.println("4. Add a new Customer");
        System.out.println("5. Rent a Car");
        System.out.println("6. Return a Car");
        System.out.println("7. List all Rented Cars");
        System.out.println("8. Update Cars");
        System.out.println("   1. Change info car");
        System.out.println("   2. Delete car (Mark as REMOVED_FROM_AUTO_PARK)");
        System.out.println("9. Update Customers");
        System.out.println("   1. Change info customer");
        System.out.println("   2. Delete customer");
        System.out.println("0. Exit");
        System.out.println("------------------------------");
    }

    private static void addCar() {
        System.out.println("\n--- Add New Car ---");
        System.out.print("Enter Car ID: ");
        String id = scanner.nextLine();

        if (carService.findCarById(id).isPresent()) {
            System.out.println("Error: Car with this ID already exists.");
            return;
        }

        System.out.print("Enter Mark: ");
        String make = scanner.nextLine();
        System.out.print("Enter Model: ");
        String model = scanner.nextLine();
        System.out.print("Enter Year: ");
        int year = 0;
        try {
            year = scanner.nextInt();
            scanner.nextLine();
        } catch (InputMismatchException e) {
            System.out.println("Invalid input for Year. Please enter a number.");
            scanner.nextLine();
            return;
        }

        System.out.print("Enter Type (e.g.,Hatchback, Sedan, SUV, Cabrio, Coupe): ");
        String type = scanner.nextLine();

        Car newCar = new Car(id, make, model, year, type, CarStatus.AVAILABLE,
                null, null, null, null, null, null, 0.0);
        if (carService.addCar(newCar)) {
            System.out.println("Car " + make + " " + model + " added successfully.");
        } else {
            System.out.println("Failed to add car. It might already exist or there was a file error.");
        }
    }

    private static void listAllCars() {
        System.out.println("\n--- All Available and Rented Cars ---");
        List<Car> cars = carService.getAllCars();

        List<Car> displayableCars = cars.stream()
                .filter(car -> car.getStatus() != CarStatus.REMOVE_FROM_AUTO_PARK)
                .collect(Collectors.toList());

        if (displayableCars.isEmpty()) {
            System.out.println("No cars available or currently rented.");
        } else {
            displayableCars.forEach(car -> {
                System.out.println(car.toString());
            });
        }
    }

    private static void updateCarsMenu() {
        int choice;
        do {
            System.out.println("\n--- Update Cars Menu ---");
            System.out.println("1. Change info car (by ID)");
            System.out.println("2. Delete car (by ID)");
            System.out.println("0. Back to Main Menu");
            System.out.print("Enter your choice: ");
            try {
                choice = scanner.nextInt();
                scanner.nextLine();

                switch (choice) {
                    case 1:
                        changeCarInfo();
                        break;
                    case 2:
                        deleteCar();
                        break;
                    case 0:
                        break;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a number.");
                scanner.nextLine();
                choice = -1;
            }
            System.out.println();
        } while (choice != 0);
    }

    private static void changeCarInfo() {
        System.out.println("\n--- Change Car Information ---");
        System.out.print("Enter Car ID to update: ");
        String carId = scanner.nextLine();

        Optional<Car> carOptional = carService.findCarById(carId);
        if (!carOptional.isPresent()) {
            System.out.println("Error: Car with ID " + carId + " not found.");
            return;
        }
        Car carToUpdate = carOptional.get();

        System.out.println("Current Car Info: " + carToUpdate);

        if (carToUpdate.getStatus() == CarStatus.RENTED) {
            System.out.println("Cannot change all information for a rented car. Only return it first.");
            System.out.print("Enter new Daily Rate (current: " + carToUpdate.getDailyRate() + "): ");
            double newDailyRate = 0.0;
            try {
                newDailyRate = scanner.nextDouble();
                scanner.nextLine();
            } catch (InputMismatchException e) {
                System.out.println("Invalid input for Daily Rate. Keeping old value.");
                scanner.nextLine();
                newDailyRate = carToUpdate.getDailyRate();
            }
            if (newDailyRate <= 0) {
                System.out.println("Daily rate must be a positive number. Keeping old value.");
                newDailyRate = carToUpdate.getDailyRate();
            }
            carToUpdate.setDailyRate(newDailyRate);

            boolean success = carService.updateCarStatusAndRentalInfo(
                    carToUpdate.getId(),
                    carToUpdate.getStatus(),
                    carToUpdate.getCurrentRenterId(),
                    carToUpdate.getCurrentRenterName(),
                    carToUpdate.getCurrentRenterPhone(),
                    carToUpdate.getCurrentRenterEmail(),
                    carToUpdate.getRentalStartDate(),
                    carToUpdate.getExpectedReturnDate(),
                    carToUpdate.getDailyRate()
            );
            if (success) {
                System.out.println("Car " + carToUpdate.getId() + " daily rate updated successfully.");
            } else {
                System.out.println("Failed to update car daily rate.");
            }
            return;
        }

        System.out.print("Enter new Make (current: " + carToUpdate.getMake() + "): ");
        String newMake = scanner.nextLine();
        if (!newMake.isEmpty()) {
            carToUpdate.setMake(newMake);
        }

        System.out.print("Enter new Model (current: " + carToUpdate.getModel() + "): ");
        String newModel = scanner.nextLine();
        if (!newModel.isEmpty()) {
            carToUpdate.setModel(newModel);
        }

        System.out.print("Enter new Year (current: " + carToUpdate.getYear() + "): ");
        String newYearStr = scanner.nextLine();
        if (!newYearStr.isEmpty()) {
            try {
                int newYear = Integer.parseInt(newYearStr);
                carToUpdate.setYear(newYear);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input for Year. Keeping old value.");
            }
        }

        System.out.print("Enter new Type (current: " + carToUpdate.getType() + "): ");
        String newType = scanner.nextLine();
        if (!newType.isEmpty()) {
            carToUpdate.setType(newType);
        }

        System.out.print("Enter new Daily Rate (current: " + carToUpdate.getDailyRate() + "): ");
        String newDailyRateStr = scanner.nextLine();
        if (!newDailyRateStr.isEmpty()) {
            try {
                double newDailyRate = Double.parseDouble(newDailyRateStr);
                if (newDailyRate > 0) {
                    carToUpdate.setDailyRate(newDailyRate);
                } else {
                    System.out.println("Daily rate must be a positive number. Keeping old value.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input for Daily Rate. Keeping old value.");
            }
        }

        boolean success = carService.updateCarStatusAndRentalInfo(
                carToUpdate.getId(),
                carToUpdate.getStatus(),
                carToUpdate.getCurrentRenterId(),
                carToUpdate.getCurrentRenterName(),
                carToUpdate.getCurrentRenterPhone(),
                carToUpdate.getCurrentRenterEmail(),
                carToUpdate.getRentalStartDate(),
                carToUpdate.getExpectedReturnDate(),
                carToUpdate.getDailyRate()
        );

        if (success) {
            System.out.println("Car information updated successfully for ID: " + carToUpdate.getId());
        } else {
            System.out.println("Failed to update car information for ID: " + carToUpdate.getId());
        }
    }


    private static void deleteCar() {
        System.out.println("\n--- Delete Car ---");
        System.out.print("Enter Car ID to delete: ");
        String carId = scanner.nextLine();

        Optional<Car> carOptional = carService.findCarById(carId);
        if (!carOptional.isPresent()) {
            System.out.println("Error: Car with ID " + carId + " not found.");
            return;
        }
        Car carToDelete = carOptional.get();

        if (carToDelete.getStatus() == CarStatus.RENTED) {
            System.out.println("Error: Cannot delete a car that is currently rented. Please return it first.");
            return;
        }

        boolean success = carService.updateCarStatusAndRentalInfo(
                carId,
                CarStatus.REMOVE_FROM_AUTO_PARK,
                null, null, null, null, null, null, 0.0
        );

        if (success) {
            System.out.println("Car " + carId + " marked as REMOVED_FROM_AUTO_PARK successfully.");
        } else {
            System.out.println("Failed to mark car " + carId + " as REMOVED_FROM_AUTO_PARK.");
        }
    }


    private static void addCustomer() {
        System.out.println("\n--- Add New Customer ---");
        System.out.print("Enter Customer ID: ");
        String id = scanner.nextLine();

        if (customerService.findCustomerById(id).isPresent()) {
            System.out.println("Error: Customer with this ID already exists.");
            return;
        }

        System.out.print("Enter Customer Name: ");
        String name = scanner.nextLine();
        System.out.print("Enter Phone Number: ");
        String phone = scanner.nextLine();
        System.out.print("Enter Email: ");
        String email = scanner.nextLine();

        Customer newCustomer = new Customer(id, name, phone, email);
        if (customerService.addCustomer(newCustomer)) {
            System.out.println("Customer " + name + " added successfully.");
        } else {
            System.out.println("Failed to add customer. They might already exist or there was a file error.");
        }
    }

    private static void listAllCustomers() {
        System.out.println("\n--- All Customers ---");
        List<Customer> customers = customerService.getCustomers();
        if (customers.isEmpty()) {
            System.out.println("No customers available.");
        } else {
            customers.forEach(customer -> {
                System.out.println(customer.toString());
            });
        }
    }

    private static void updateCustomersMenu() {
        int choice;
        do {
            System.out.println("\n--- Update Customers Menu ---");
            System.out.println("1. Change info customer (by ID)");
            System.out.println("2. Delete customer (by ID)");
            System.out.println("0. Back to Main Menu");
            System.out.print("Enter your choice: ");
            try {
                choice = scanner.nextInt();
                scanner.nextLine();

                switch (choice) {
                    case 1:
                        changeCustomerInfo();
                        break;
                    case 2:
                        deleteCustomer();
                        break;
                    case 0:
                        break;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a number.");
                scanner.nextLine();
                choice = -1;
            }
            System.out.println();
        } while (choice != 0);
    }

    private static void changeCustomerInfo() {
        System.out.println("\n--- Change Customer Information ---");
        System.out.print("Enter Customer ID to update: ");
        String customerId = scanner.nextLine();

        Optional<Customer> customerOptional = customerService.findCustomerById(customerId);
        if (!customerOptional.isPresent()) {
            System.out.println("Error: Customer with ID " + customerId + " not found.");
            return;
        }
        Customer customerToUpdate = customerOptional.get();

        System.out.println("Current Customer Info: " + customerToUpdate);

        System.out.print("Enter new Name (current: " + customerToUpdate.getName() + "): ");
        String newName = scanner.nextLine();
        if (!newName.isEmpty()) {
            customerToUpdate.setName(newName);
        }

        System.out.print("Enter new Phone Number (current: " + customerToUpdate.getPhoneNumber() + "): ");
        String newPhone = scanner.nextLine();
        if (!newPhone.isEmpty()) {
            customerToUpdate.setPhoneNumber(newPhone);
        }

        System.out.print("Enter new Email (current: " + customerToUpdate.getEmail() + "): ");
        String newEmail = scanner.nextLine();
        if (!newEmail.isEmpty()) {
            customerToUpdate.setEmail(newEmail);
        }

        if (customerService.addCustomer(customerToUpdate)) {
            System.out.println("Customer information updated successfully for ID: " + customerToUpdate.getId());
        } else {
            System.out.println("Failed to update customer information for ID: " + customerToUpdate.getId() + " (might not exist or file error).");
        }
        customerService.saveDataToCustomersFile();
    }

    private static void deleteCustomer() {
        System.out.println("\n--- Delete Customer ---");
        System.out.print("Enter Customer ID to delete: ");
        String customerId = scanner.nextLine();

        List<Car> rentedCarsByCustomer = carService.getAllCars().stream()
                .filter(car -> car.getStatus() == CarStatus.RENTED &&
                        car.getCurrentRenterId() != null &&
                        car.getCurrentRenterId().equals(customerId))
                .collect(Collectors.toList());

        if (!rentedCarsByCustomer.isEmpty()) {
            System.out.println("Error: Cannot delete customer with ID " + customerId + " because they are currently renting the following cars:");
            rentedCarsByCustomer.forEach(car -> System.out.println("- Car ID: " + car.getId() + ", Make: " + car.getMake() + ", Model: " + car.getModel()));
            System.out.println("Please ensure all cars rented by this customer are returned before deleting the customer.");
            return;
        }

        if (customerService.deleteCustomer(customerId)) {
            System.out.println("Customer with ID " + customerId + " deleted successfully.");
        } else {
            System.out.println("Failed to delete customer with ID " + customerId + " (might not exist or file error).");
        }
    }


    private static void rentCar() {
        System.out.println("\n--- Rent a Car ---");
        System.out.print("Enter Car ID to rent: ");
        String carId = scanner.nextLine();

        Optional<Car> carOptional = carService.findCarById(carId);
        if (!carOptional.isPresent()) {
            System.out.println("Error: Car with ID " + carId + " not found.");
            return;
        }
        Car carToRent = carOptional.get();

        if (!carToRent.isAvailable()) {
            System.out.println("Error: Car " + carToRent.getMake() + " " + carToRent.getModel() +
                    " is not available for rent. Current status: " + carToRent.getStatus());
            return;
        }

        System.out.print("Enter Customer ID: ");
        String customerId = scanner.nextLine();
        Optional<Customer> customerOptional = customerService.findCustomerById(customerId);
        if (!customerOptional.isPresent()) {
            System.out.println("Error: Customer with ID " + customerId + " not found.");
            return;
        }
        Customer customer = customerOptional.get();

        System.out.print("Enter Rental Start Date (YYYY-MM-DD): ");
        LocalDate rentalStartDate;
        try {
            rentalStartDate = LocalDate.parse(scanner.nextLine());
        } catch (DateTimeParseException e) {
            System.out.println("Invalid date format. Please use YYYY-MM-DD.");
            return;
        }

        System.out.print("Enter Expected Return Date (YYYY-MM-DD): ");
        LocalDate expectedReturnDate;
        try {
            expectedReturnDate = LocalDate.parse(scanner.nextLine());
        } catch (DateTimeParseException e) {
            System.out.println("Invalid date format. Please use YYYY-MM-DD.");
            return;
        }

        if (rentalStartDate.isAfter(expectedReturnDate)) {
            System.out.println("Error: Rental start date cannot be after expected return date.");
            return;
        }
        if (rentalStartDate.isBefore(LocalDate.now()) && !rentalStartDate.isEqual(LocalDate.now())) {
            System.out.println("Error: Rental start date cannot be in the past.");
            return;
        }

        System.out.print("Enter fee per day: ");
        double dailyRate = 0.0;
        try {
            dailyRate = scanner.nextDouble();
            scanner.nextLine();
        } catch (InputMismatchException e) {
            System.out.println("Invalid input for Daily Rate. Please enter a number.");
            scanner.nextLine();
            return;
        }
        if (dailyRate <= 0) {
            System.out.println("Daily rate must be a positive number.");
            return;
        }

        long numberOfDays = ChronoUnit.DAYS.between(rentalStartDate, expectedReturnDate) + 1;
        double totalCost = numberOfDays * dailyRate;

        boolean success = carService.updateCarStatusAndRentalInfo(
                carId,
                CarStatus.RENTED,
                customer.getId(),
                customer.getName(),
                customer.getPhoneNumber(),
                customer.getEmail(),
                rentalStartDate,
                expectedReturnDate,
                dailyRate
        );

        if (success) {
            System.out.println("Car " + carToRent.getMake() + " " + carToRent.getModel() +
                    " rented successfully to " + customer.getName() +
                    " for " + numberOfDays + " days. Total cost: $" + String.format("%.2f", totalCost));
        } else {
            System.out.println("Failed to rent car due to an internal error or file saving issue.");
        }
    }

    private static void returnCar() {
        System.out.println("\n--- Return a Car ---");
        System.out.print("Enter Car ID to return: ");
        String carId = scanner.nextLine();

        Optional<Car> carOptional = carService.findCarById(carId);
        if (!carOptional.isPresent()) {
            System.out.println("Error: Car with ID " + carId + " not found.");
            return;
        }
        Car carToReturn = carOptional.get();

        if (carToReturn.getStatus() != CarStatus.RENTED) {
            System.out.println("Error: Car " + carToReturn.getId() + " is not currently rented. Current status: " + carToReturn.getStatus());
            return;
        }

        boolean success = carService.updateCarStatusAndRentalInfo(
                carId,
                CarStatus.AVAILABLE,
                null, null, null, null, null, null, 0.0
        );

        if (success) {
            System.out.println("Car " + carToReturn.getMake() + " " + carToReturn.getModel() + " returned successfully.");
        } else {
            System.out.println("Failed to return car due to an internal error or file saving issue.");
        }
    }

    private static void listAllRentedCars() {
        System.out.println("\n--- All Rented Cars ---");
        List<Car> rentedCars = carService.getAllCars().stream()
                .filter(car -> car.getStatus() == CarStatus.RENTED)
                .collect(Collectors.toList());

        if (rentedCars.isEmpty()) {
            System.out.println("No cars are currently rented.");
        } else {
            rentedCars.forEach(System.out::println);
        }
    }
}