package com.example.menumicroservice.controller;

import com.example.menumicroservice.dto.MenuCreateRequest;
import com.example.menumicroservice.service.MenuServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/menus")
@RequiredArgsConstructor
public class MenuController {
    private final MenuServiceImpl menuService;
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void createMenu(@Valid @RequestBody MenuCreateRequest request) {
        menuService.createMenu(request);
    }
}