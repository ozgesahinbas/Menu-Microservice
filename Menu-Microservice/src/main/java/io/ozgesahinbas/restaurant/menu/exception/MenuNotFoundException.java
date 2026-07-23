package io.ozgesahinbas.restaurant.menu.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class MenuNotFoundException extends RuntimeException {

    public MenuNotFoundException(String id) {
        super("Menu not found with id: " + id);
    }
}
