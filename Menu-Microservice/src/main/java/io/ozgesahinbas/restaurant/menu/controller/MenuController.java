package io.ozgesahinbas.restaurant.menu.controller;

import io.ozgesahinbas.restaurant.menu.dto.MenuCreateRequest;
import io.ozgesahinbas.restaurant.menu.dto.MenuItemCreateRequest;
import io.ozgesahinbas.restaurant.menu.dto.MenuUpdateRequest;
import io.ozgesahinbas.restaurant.menu.model.Menu;
import io.ozgesahinbas.restaurant.menu.service.MenuServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@RestController
@RequestMapping("/menu")
@RequiredArgsConstructor
public class MenuController {

    private final MenuServiceImpl menuService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void createMenu(@Valid @RequestBody MenuCreateRequest request) {
        menuService.createMenu(request);
    }
    @GetMapping
    public List<Menu> getAllMenus() {
        return menuService.getAllMenus();
    }
    @GetMapping("/{id}")
    public Menu getMenuById(@PathVariable String id) {
        return menuService.getMenuById(id);
    }
    @PutMapping("/{id}")
    public Menu updateMenu(@PathVariable String id,
                           @RequestBody @Valid MenuUpdateRequest request) {
        return menuService.updateMenu(id, request);
    }
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMenu(@PathVariable String id) {
        menuService.deleteMenu(id);
    }
    @GetMapping("/restaurants/{restaurantId}/menus")
    public List<Menu> getMenuByRestaurantId(@PathVariable String restaurantId) {
        return menuService.getMenuByRestaurantId(restaurantId);
    }
    @PostMapping("/{menuId}/items")
    public Menu createMenuItem(
            @PathVariable String menuId,
            @Valid @RequestBody MenuItemCreateRequest request) {

        return menuService.createMenuItem(menuId, request);
    }
}