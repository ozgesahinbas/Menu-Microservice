package io.ozgesahinbas.restaurant.menu.exception;

/** @see MenuNotFoundException */
public class MenuItemNotFoundException extends RuntimeException {

    public MenuItemNotFoundException(String itemId) {
        super("Menu item not found with id: " + itemId);
    }
}
