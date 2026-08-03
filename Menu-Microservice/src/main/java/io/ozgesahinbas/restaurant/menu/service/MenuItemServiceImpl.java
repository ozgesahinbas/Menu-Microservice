package io.ozgesahinbas.restaurant.menu.service;

import io.ozgesahinbas.restaurant.menu.dto.MenuItemCreateRequest;
import io.ozgesahinbas.restaurant.menu.dto.MenuItemUpdateRequest;
import io.ozgesahinbas.restaurant.menu.entity.Menu;
import io.ozgesahinbas.restaurant.menu.entity.MenuItem;
import io.ozgesahinbas.restaurant.menu.exception.MenuItemNotFoundException;
import io.ozgesahinbas.restaurant.menu.repository.MenuItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MenuItemServiceImpl implements MenuItemService {

    private final MenuItemRepository menuItemRepository;
    private final MenuService menuService;

    @Override
    public MenuItem createMenuItem(String menuId, MenuItemCreateRequest request) {
        Menu menu = menuService.getMenuById(menuId);

        return menuItemRepository.save(request.toEntity(menuId, menu.getRestaurantId()));
    }

    @Override
    public List<MenuItem> getMenuItems(String menuId) {
        menuService.getMenuById(menuId);

        return menuItemRepository.findByMenuId(menuId);
    }

    @Override
    public MenuItem getMenuItemById(String menuId, String itemId) {
        menuService.getMenuById(menuId);

        MenuItem menuItem = menuItemRepository.findById(itemId)
                .orElseThrow(() -> new MenuItemNotFoundException(itemId));

        // An item that exists but hangs off another menu must not leak through
        // this menu's URL, so it is reported as missing rather than forbidden.
        if (!menuItem.getMenuId().equals(menuId)) {
            throw new MenuItemNotFoundException(itemId);
        }

        return menuItem;
    }

    @Override
    public MenuItem updateMenuItem(String menuId,
                                   String itemId,
                                   MenuItemUpdateRequest request) {

        MenuItem menuItem = getMenuItemById(menuId, itemId);
        request.updateEntity(menuItem);

        return menuItemRepository.save(menuItem);
    }

    @Override
    public void deleteMenuItem(String menuId, String itemId) {
        MenuItem menuItem = getMenuItemById(menuId, itemId);

        menuItemRepository.delete(menuItem);
    }
}
