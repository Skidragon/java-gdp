package com.lambdaschool.gdp;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
public class CountryController {
    private final CountryRepository countryRepo;
    private final RabbitTemplate rt;

    public CountryController(CountryRepository countryRepo, RabbitTemplate rt) {
        this.countryRepo = countryRepo;
        this.rt = rt;
    }
//    /names - return using the JSON format all of the countries alphabetized by name
    @GetMapping("/countries")
    public List<Country> all() {
        return countryRepo.findAll();
    }
    @PostMapping("/countries")
    public List<Country> newCountries(@RequestBody List<Country> newCountries) {
        return countryRepo.saveAll(newCountries);
    }
}
