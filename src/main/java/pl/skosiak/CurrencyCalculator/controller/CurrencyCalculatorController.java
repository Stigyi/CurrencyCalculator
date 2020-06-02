package pl.skosiak.CurrencyCalculator.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import pl.skosiak.CurrencyCalculator.model.ErrorMessage;
import pl.skosiak.CurrencyCalculator.model.dto.CalculationDTO;
import pl.skosiak.CurrencyCalculator.model.exception.NoCurrencyException;
import pl.skosiak.CurrencyCalculator.model.request.CalculationRequest;
import pl.skosiak.CurrencyCalculator.service.CalculationService;
import pl.skosiak.CurrencyCalculator.model.CustomValidator;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@AllArgsConstructor
public class CurrencyCalculatorController {

    private CalculationService calculationService;

    @GetMapping("/calculate")
    private CalculationDTO calculateRate(@RequestParam(value = "value") Double value,
                                         @RequestParam(value = "currency") String currency,
                                         @RequestParam(value = "targetCurrency") String targetCurrency){

        CalculationRequest request = CalculationRequest.builder()
                .value(value)
                .currency(currency)
                .targetCurrency(targetCurrency)
                .build();
        CustomValidator.validate(request);

        return calculationService.calculate(request);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    @ExceptionHandler(NoCurrencyException.class)
    public ErrorMessage handleNoCurrencyException(NoCurrencyException e) {
        return  ErrorMessage.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .error(HttpStatus.BAD_REQUEST.name())
                .message(e.getMessage())
                .build();
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    @ExceptionHandler(ConstraintViolationException.class)
    public ErrorMessage handleValidation(ConstraintViolationException e) {
        return  ErrorMessage.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .error(HttpStatus.BAD_REQUEST.name())
                .message(e.getConstraintViolations().stream()
                        .map(ConstraintViolation::getMessage)
                        .collect(Collectors.joining(", ")))
                .build();
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    @ExceptionHandler(Exception.class)
    public ErrorMessage handleException(Exception e) {
        return  ErrorMessage.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error(HttpStatus.INTERNAL_SERVER_ERROR.name())
                .message("Something bad happened :(")
                .build();
    }

}
