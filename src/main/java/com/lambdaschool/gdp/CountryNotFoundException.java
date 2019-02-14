package com.lambdaschool.gdp;


public class CountryNotFoundException extends RuntimeException {

    public CountryNotFoundException(Long id) {
        super("Could not find country by the id " + id);
    }
}
