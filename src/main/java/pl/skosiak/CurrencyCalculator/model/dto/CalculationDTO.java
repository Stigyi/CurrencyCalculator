package pl.skosiak.CurrencyCalculator.model.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CalculationDTO {
    private String currency;
    private Double valueAfterConversion;
}
