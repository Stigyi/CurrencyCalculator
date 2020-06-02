package pl.skosiak.CurrencyCalculator.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.skosiak.CurrencyCalculator.model.dto.CalculationDTO;
import pl.skosiak.CurrencyCalculator.model.dto.NbpCurrencyRateDTO;
import pl.skosiak.CurrencyCalculator.model.exception.NoCurrencyException;
import pl.skosiak.CurrencyCalculator.model.request.CalculationRequest;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@AllArgsConstructor
public class CalculationService {

    private NbpService nbpService;


    public CalculationDTO calculate(CalculationRequest calculationRequest){
        if(calculationRequest.getCurrency().equals(NbpCurrencyRateDTO.PLN_CODE)){
            return calculateFromPLN(calculationRequest.getValue(), calculationRequest.getTargetCurrency());
        }
        return calculateWithDoubleCurrencyConversion(calculationRequest.getValue(),
                calculationRequest.getCurrency(), calculationRequest.getTargetCurrency());
    }

    public CalculationDTO calculateFromPLN(Double amount, String currency){
        NbpCurrencyRateDTO currencyRateDTO = getCurrencyFromDictionary(currency);

        Double convertedAmount = BigDecimal.valueOf(amount)
                .multiply(BigDecimal.valueOf(currencyRateDTO.getMid()))
                .setScale(2, RoundingMode.HALF_UP)
                .doubleValue();

        return CalculationDTO.builder()
                .currency(currency)
                .valueAfterConversion(convertedAmount)
                .build();
    }

    public CalculationDTO calculateWithDoubleCurrencyConversion(Double amount,
                                                                String sourceCurrency,
                                                                String targetCurrency){
        NbpCurrencyRateDTO sourceCurrencyRateDTO = getCurrencyFromDictionary(sourceCurrency);
        NbpCurrencyRateDTO targetCurrencyRateDTO = getCurrencyFromDictionary(targetCurrency);

        Double convertedAmount = BigDecimal.valueOf(amount)
                .multiply(BigDecimal.valueOf(sourceCurrencyRateDTO.getMid()))
                .multiply(BigDecimal.valueOf(targetCurrencyRateDTO.getMid()))
                .setScale(2, RoundingMode.HALF_UP)
                .doubleValue();

        return CalculationDTO.builder()
                .currency(targetCurrency)
                .valueAfterConversion(convertedAmount)
                .build();
    }

    private NbpCurrencyRateDTO getCurrencyFromDictionary(String currencyName){
       return nbpService.getCurrencies()
                .stream()
                .filter(p -> p.getCode().equals(currencyName)).findFirst()
                .orElseThrow(() -> new NoCurrencyException(String.format("currency %s not found", currencyName)));
    }

}
