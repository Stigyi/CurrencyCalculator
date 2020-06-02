package pl.skosiak.CurrencyCalculator.service;

import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import pl.skosiak.CurrencyCalculator.model.dto.NbpCurrencyDTO;
import pl.skosiak.CurrencyCalculator.model.dto.NbpCurrencyRateDTO;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@AllArgsConstructor
public class NbpService {

    private static final String NBP_ENDPOINT_URL = "http://api.nbp.pl/api/exchangerates/tables/";
    private static final long DICTIONARY_RELOAD_TIME_IN_MILLIS = 86400000L;

    private List<NbpCurrencyRateDTO> currencies;
    private RestTemplate restTemplate;

    @PostConstruct
    private void initializeCurencies(){
        loadCurrencyData();
    }

    @Scheduled(fixedDelay = DICTIONARY_RELOAD_TIME_IN_MILLIS)
    public void loadCurrencyData(){
       currencies.clear();
       NbpCurrencyDTO[] tableA = restTemplate.getForObject(NBP_ENDPOINT_URL.concat("A/"), NbpCurrencyDTO[].class);
       NbpCurrencyDTO[] tableB = restTemplate.getForObject(NBP_ENDPOINT_URL.concat("B/"), NbpCurrencyDTO[].class);
       List<NbpCurrencyRateDTO> loadedCurrencies =
               Stream.concat(tableA[0].getRates().stream(),tableB[0].getRates().stream()).collect(Collectors.toList());
       this.currencies = loadedCurrencies;
    }

    public List<NbpCurrencyRateDTO> getCurrencies() {
        return currencies;
    }
}
