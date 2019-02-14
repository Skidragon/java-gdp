package com.lambdaschool.gdp;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
public class CountryController {
    private final CountryRepository countryRepo;
    private final RabbitTemplate rt;

    public CountryController(CountryRepository countryRepo, RabbitTemplate rt) {
        this.countryRepo = countryRepo;
        this.rt = rt;
    }
    @GetMapping("/countries")
    public List<Country> all() {
        return countryRepo.findAll();
    }
    //    /names - return using the JSON format all of the countries alphabetized by name

    @GetMapping("/countries/names")
    public List<Country> alphabetizeNames() {
        return countryRepo.findAll()
                .stream()
                .sorted((c1,c2) -> c1.getCountry().compareToIgnoreCase(c2.getCountry()))
                .collect(Collectors.toList());
    }
    @GetMapping("/countries/economy")
    public List<Country> descendingGDP() {
        return countryRepo.findAll()
                .stream()
                .sorted((c1,c2) -> (int) (c2.getGdp() - c1.getGdp()))
                .collect(Collectors.toList());
    }

    @GetMapping("/countries/total")
    public ObjectNode allCountriesGdp() {
        Long total = 0L;
        List<Country> countries = countryRepo.findAll();

        for (Country country: countries) {
            total += country.getGdp();
        }

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode totalGdp = mapper.createObjectNode();
        totalGdp.put("id", 0);
        totalGdp.put("country", "total");
        totalGdp.put("totalGdp", total);
        return totalGdp;
    }

    @GetMapping("/countries/{name}")
    public Country getCountryByName(@PathVariable String name) {
        List<Country> country = countryRepo.findAll()
                .stream()
                .filter(c -> c.getCountry().equalsIgnoreCase(name))
                .collect(Collectors.toList());
        CountryLog message = new CountryLog("Checked country: ");
        rt.convertAndSend(GdpApplication.QUEUE_NAME, message.toString());

        return country.get(0);
    }

    @PostMapping("/countries/gdp")
    public List<Country> newCountries(@RequestBody List<Country> newCountries) {
        return countryRepo.saveAll(newCountries);
    }
}
