package pl.skosiak.CurrencyCalculator.model.request;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Builder
public class CalculationRequest {
    @NotNull(message = "value cannot be blank")
    private Double value;
    @NotBlank(message = "currency cannot be blank")
    private String currency;
    @NotBlank(message = "targetCurrency cannot be blank")
    private String targetCurrency;
}
