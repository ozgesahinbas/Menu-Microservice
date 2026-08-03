package io.ozgesahinbas.restaurant.menu.controller;

import io.ozgesahinbas.restaurant.menu.dto.MenuItemCreateRequest;
import io.ozgesahinbas.restaurant.menu.dto.MenuItemUpdateRequest;
import io.ozgesahinbas.restaurant.menu.entity.MenuItem;
import io.ozgesahinbas.restaurant.menu.service.MenuItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/menus/{menuId}/items")
@RequiredArgsConstructor
public class MenuItemController {

    private final MenuItemService menuItemService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MenuItem createMenuItem(@PathVariable String menuId,
                                   @Valid @RequestBody MenuItemCreateRequest request) {

        return menuItemService.createMenuItem(menuId, request);
    }

    @GetMapping
    public List<MenuItem> getMenuItems(@PathVariable String menuId) {
        return menuItemService.getMenuItems(menuId);
    }

    @GetMapping("/{itemId}")
    public MenuItem getMenuItemById(@PathVariable String menuId,
                                    @PathVariable String itemId) {

        return menuItemService.getMenuItemById(menuId, itemId);
    }

    @PutMapping("/{itemId}")
    public MenuItem updateMenuItem(@PathVariable String menuId,
                                   @PathVariable String itemId,
                                   @Valid @RequestBody MenuItemUpdateRequest request) {

        return menuItemService.updateMenuItem(menuId, itemId, request);
    }

    @DeleteMapping("/{itemId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMenuItem(@PathVariable String menuId,
                               @PathVariable String itemId) {

        menuItemService.deleteMenuItem(menuId, itemId);
    }
}
