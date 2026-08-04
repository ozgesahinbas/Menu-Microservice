package io.ozgesahinbas.restaurant.menu.entity;

import io.ozgesahinbas.restaurant.menu.enums.MenuItemStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.couchbase.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document
public class MenuItem {

    /** Keeps item documents distinguishable from menus in the same bucket. */
    public static final String ID_PREFIX = "menu-item::";

    @Id
    private String id;
    private String menuId;
    private String restaurantId;
    private String name;
    private String description;
    private String category;
    private BigDecimal price;
    private String currency;
    private List<String> photoUrls;
    private List<String> videoUrls;
    private List<String> allergens;
    private List<String> ingredients;
    private MenuItemStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static String newId() {
        return ID_PREFIX + UUID.randomUUID();
    }
}
