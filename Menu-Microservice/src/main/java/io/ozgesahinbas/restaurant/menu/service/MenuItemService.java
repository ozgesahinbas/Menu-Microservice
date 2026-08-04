package io.ozgesahinbas.restaurant.menu.service;

import io.ozgesahinbas.restaurant.menu.dto.MenuItemCreateRequest;
import io.ozgesahinbas.restaurant.menu.dto.MenuItemUpdateRequest;
import io.ozgesahinbas.restaurant.menu.entity.MenuItem;

import java.util.List;

/**
 * Menu item operations. Every method is scoped to a menu: the menu must exist
 * and the item must belong to it, otherwise the item is reported as not found.
 */
public interface MenuItemService {

    MenuItem createMenuItem(String menuId, MenuItemCreateRequest request);

    List<MenuItem> getMenuItems(String menuId);

    MenuItem getMenuItemById(String menuId, String itemId);

    MenuItem updateMenuItem(String menuId, String itemId, MenuItemUpdateRequest request);

    void deleteMenuItem(String menuId, String itemId);
}
