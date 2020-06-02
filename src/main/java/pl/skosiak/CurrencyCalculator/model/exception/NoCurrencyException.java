package pl.skosiak.CurrencyCalculator.model.exception;

public class NoCurrencyException extends RuntimeException{
    public NoCurrencyException(String message) {
        super(message);
    }
}
