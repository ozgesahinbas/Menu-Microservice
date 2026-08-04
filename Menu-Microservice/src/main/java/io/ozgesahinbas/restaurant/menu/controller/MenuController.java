package io.ozgesahinbas.restaurant.menu.controller;

import io.ozgesahinbas.restaurant.menu.dto.MenuCreateRequest;
import io.ozgesahinbas.restaurant.menu.dto.MenuUpdateRequest;
import io.ozgesahinbas.restaurant.menu.entity.Menu;
import io.ozgesahinbas.restaurant.menu.service.MenuService;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/menus")
@RequiredArgsConstructor
public class MenuController {

    private final MenuService menuService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Menu createMenu(@Valid @RequestBody MenuCreateRequest request) {
        return menuService.createMenu(request);
    }

    @GetMapping
    public List<Menu> getAllMenus() {
        return menuService.getAllMenus();
    }

    // Filtering the collection rather than nesting under /restaurants keeps this
    // service's URLs rooted at the resource it actually owns.
    @GetMapping(params = "restaurantId")
    public List<Menu> getMenusByRestaurantId(@RequestParam String restaurantId) {
        return menuService.getMenusByRestaurantId(restaurantId);
    }

    @GetMapping("/{id}")
    public Menu getMenuById(@PathVariable String id) {
        return menuService.getMenuById(id);
    }

    @PutMapping("/{id}")
    public Menu updateMenu(@PathVariable String id,
                           @Valid @RequestBody MenuUpdateRequest request) {

        return menuService.updateMenu(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMenu(@PathVariable String id) {
        menuService.deleteMenu(id);
    }
}
