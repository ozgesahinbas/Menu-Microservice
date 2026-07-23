package io.ozgesahinbas.restaurant.menu.controller;

import io.ozgesahinbas.restaurant.menu.dto.MenuCreateRequest;
import io.ozgesahinbas.restaurant.menu.service.MenuServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

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
}