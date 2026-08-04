package io.ozgesahinbas.restaurant.menu.dto;

import io.ozgesahinbas.restaurant.menu.model.MenuItem;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MenuItemCreateRequest {
    @NotBlank
    private String name;
    private String description;

    @NotNull
    private BigDecimal price;
    private String imageUrl;

    public MenuItem toEntity() {
        return MenuItem.builder()
                .name(name)
                .description(description)
                .price(price)
                .imageUrl(imageUrl)
                .build();
    }
}
