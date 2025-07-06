package com.sirma.academy.service;

import com.sirma.academy.model.Car;
import com.sirma.academy.model.CarStatus;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class CarService {
    private List<Car> cars;
    private FileService fileService;
    private static final String FILE_PATH = "src/com/sirma/academy/data/cars.csv";
    private static final String CSV_HEADER = "id,make,model,year,type,status,customerId,name,phone,email,rentalStartDate,expectedReturnDate,priceForRent";


    private final Function<String, Car> carParser = line -> {
        String[] parts = line.split(",");
        if (parts.length != 13) {
            System.err.println("Skipping malformed car line (expected 13 parts): " + line);
            return null;
        }
        try {
            String id = parts[0];
            String make = parts[1];
            String model = parts[2];
            int year = Integer.parseInt(parts[3]);
            String type = parts[4];
            CarStatus status = CarStatus.valueOf(parts[5]);

            String currentRenterId = parts[6].isEmpty() ? null : parts[6];
            String currentRenterName = parts[7].isEmpty() ? null : parts[7];
            String currentRenterPhone = parts[8].isEmpty() ? null : parts[8];
            String currentRenterEmail = parts[9].isEmpty() ? null : parts[9];
            LocalDate rentalStartDate = parts[10].isEmpty() ? null : LocalDate.parse(parts[10]);
            LocalDate expectedReturnDate = parts[11].isEmpty() ? null : LocalDate.parse(parts[11]);
            double dailyRate = parts[12].isEmpty() ? 0.0 : Double.parseDouble(parts[12]);

            return new Car(id, make, model, year, type, status,
                    currentRenterId, currentRenterName, currentRenterPhone,
                    currentRenterEmail, rentalStartDate, expectedReturnDate, dailyRate);
        } catch (NumberFormatException e) {
            System.err.println("Error parsing number (year or priceForRent) in car line: " + line + ". " + e.getMessage());
            return null;
        } catch (IllegalArgumentException e) {
            System.err.println("Error parsing CarStatus enum in car line: " + line + ". " + e.getMessage());
            return null;
        } catch (DateTimeParseException e) {
            System.err.println("Error parsing date in car line: " + line + ". " + e.getMessage());
            return null;
        } catch (Exception e) {
            System.err.println("Unexpected error parsing car line: " + line + ". " + e.getMessage());
            return null;
        }
    };

    private final Function<Car, String> carFormatter = car ->
            car.getId() + "," +
                    car.getMake() + "," +
                    car.getModel() + "," +
                    car.getYear() + "," +
                    car.getType() + "," +
                    car.getStatus().name() + "," +
                    (car.getCurrentRenterId() != null ? car.getCurrentRenterId() : "") + "," +
                    (car.getCurrentRenterName() != null ? car.getCurrentRenterName() : "") + "," +
                    (car.getCurrentRenterPhone() != null ? car.getCurrentRenterPhone() : "") + "," +
                    (car.getCurrentRenterEmail() != null ? car.getCurrentRenterEmail() : "") + "," +
                    (car.getRentalStartDate() != null ? car.getRentalStartDate().toString() : "") + "," +
                    (car.getExpectedReturnDate() != null ? car.getExpectedReturnDate().toString() : "") + "," +
                    (car.getDailyRate() != 0.0 ? String.format("%.2f", car.getDailyRate()) : "0.00");


    public CarService() {
        this.fileService = new FileService();

        this.cars = fileService.loadData(FILE_PATH, carParser).stream()
                .filter(car -> car != null)
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);

        System.out.println("Loaded " + cars.size() + " cars from " + FILE_PATH);
    }

    public boolean addCar(Car car) {
        if (car == null || car.getId() == null || car.getId().isEmpty()) {
            System.out.println("Invalid car data provided. Cannot add car.");
            return false;
        }

        boolean exists = cars.stream().anyMatch(c -> c.getId().equals(car.getId()));
        if (exists) {
            System.out.println("Car with ID " + car.getId() + " already exists.");
            return false;
        }

        this.cars.add(car);
        boolean saveSuccessful = fileService.saveData(FILE_PATH, CSV_HEADER, cars, carFormatter);
        if (saveSuccessful) {
            System.out.println("Car with ID " + car.getId() + " added successfully.");
        } else {
            System.err.println("Failed to save car with ID " + car.getId() + " to file. It might be added only in memory.");
        }
        return saveSuccessful;
    }

    public Optional<Car> findCarById(String carId) {
        return cars.stream()
                .filter(car -> car.getId().equals(carId))
                .findFirst();
    }

    public List<Car> searchCars(String criteria) {
        List<Car> foundCars = new ArrayList<>();
        for (Car car : cars) {
            if (car.matches(criteria)) {
                foundCars.add(car);
            }
        }
        return foundCars;
    }

    public List<Car> getAllCars() {
        return new ArrayList<>(cars);
    }


    public boolean updateCarStatusAndRentalInfo(String carId, CarStatus newStatus,
                                                String renterId, String renterName, String renterPhone, String renterEmail,
                                                LocalDate startDate, LocalDate expectedDate, double rate) {
        Optional<Car> carOptional = findCarById(carId);
        if (carOptional.isPresent()) {
            Car car = carOptional.get();

            switch (newStatus) {
                case AVAILABLE:
                    car.markAsReturned();
                    break;
                case RENTED:
                    if (car.getStatus() == CarStatus.AVAILABLE) {
                        car.setStatus(CarStatus.RENTED);
                        car.setCurrentRenterId(renterId);
                        car.setCurrentRenterName(renterName);
                        car.setCurrentRenterPhone(renterPhone);
                        car.setCurrentRenterEmail(renterEmail);
                        car.setRentalStartDate(startDate);
                        car.setExpectedReturnDate(expectedDate);
                        car.setDailyRate(rate);
                    } else {
                        System.out.println("Car " + car.getId() + " cannot be rented as it's not Available. Current status: " + car.getStatus());
                        return false;
                    }
                    break;
                case REMOVE_FROM_AUTO_PARK:
                    car.markAsRemovedFromAutoPark();
                    break;
            }

            boolean saveSuccessful = fileService.saveData(FILE_PATH, CSV_HEADER, cars, carFormatter);
            if (saveSuccessful) {
                System.out.println("Status of car " + car.getId() + " updated to " + car.getStatus() + (newStatus == CarStatus.RENTED ? " and rental info set." : "."));
            } else {
                System.err.println("Failed to save car status update for car ID " + car.getId() + " to file.");
            }
            return saveSuccessful;
        } else {
            System.out.println("Car with ID " + carId + " not found. Cannot update status or rental info.");
            return false;
        }
    }
}