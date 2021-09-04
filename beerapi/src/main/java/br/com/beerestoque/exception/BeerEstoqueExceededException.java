package br.com.beerestoque.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BeerEstoqueExceededException extends Exception {

    public BeerEstoqueExceededException(Long id, int quantityToIncrement) {
        super(String.format("Beers with %s ID to increment informed exceeds the max estoque capacity: %s", id, quantityToIncrement));
    }
}
