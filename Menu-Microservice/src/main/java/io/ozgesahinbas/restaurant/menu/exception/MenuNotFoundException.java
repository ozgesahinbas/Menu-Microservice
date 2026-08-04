package io.ozgesahinbas.restaurant.menu.exception;

/**
 * The HTTP status mapping lives in {@link GlobalExceptionHandler} so that every
 * error leaves the service with the same response body.
 */
public class MenuNotFoundException extends RuntimeException {

    public MenuNotFoundException(String id) {
        super("Menu not found with id: " + id);
    }
}
