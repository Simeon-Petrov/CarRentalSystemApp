package com.sirma.academy.model;


public interface Rentable {

    boolean isAvailable();

    void markAsRented();

    void markAsReturned();

    void markAsRemovedFromAutoPark();
}