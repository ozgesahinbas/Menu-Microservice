package io.ozgesahinbas.restaurant.menu.validation;

import io.ozgesahinbas.restaurant.menu.dto.MenuCreateRequest;
import io.ozgesahinbas.restaurant.menu.dto.MenuItemCreateRequest;
import io.ozgesahinbas.restaurant.menu.dto.MenuItemUpdateRequest;
import io.ozgesahinbas.restaurant.menu.dto.MenuUpdateRequest;
import io.ozgesahinbas.restaurant.menu.enums.MenuStatus;
import io.ozgesahinbas.restaurant.menu.enums.MenuType;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
class RequestValidationTest {

    private static ValidatorFactory validatorFactory;
    private static Validator validator;

    @BeforeAll
    static void startValidator() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @AfterAll
    static void closeValidator() {
        validatorFactory.close();
    }

    @Test
    void shouldAcceptValidMenuCreateRequest() {

        assertThat(validator.validate(validMenuCreateRequest())).isEmpty();
    }

    @Test
    void shouldRejectBlankRestaurantId() {
        MenuCreateRequest request = validMenuCreateRequest();
        request.setRestaurantId("  ");

        assertThat(messagesFor(validator.validate(request)))
                .containsExactly("Restaurant id cannot be blank");
    }

    @Test
    void shouldRejectBlankMenuName() {
        MenuCreateRequest request = validMenuCreateRequest();
        request.setName("");

        assertThat(messagesFor(validator.validate(request)))
                .containsExactly("Menu name cannot be blank");
    }

    @Test
    void shouldRejectTooLongMenuNameAndDescription() {
        MenuCreateRequest request = validMenuCreateRequest();
        request.setName("n".repeat(101));
        request.setDescription("d".repeat(501));

        assertThat(messagesFor(validator.validate(request)))
                .containsExactlyInAnyOrder(
                        "Menu name cannot exceed 100 characters",
                        "Description cannot exceed 500 characters");
    }

    @Test
    void shouldRejectMissingMenuType() {
        MenuCreateRequest request = validMenuCreateRequest();
        request.setMenuType(null);

        assertThat(messagesFor(validator.validate(request)))
                .containsExactly("Menu type cannot be null");
    }

    @Test
    void shouldRequireStatusOnMenuUpdate() {
        MenuUpdateRequest request = MenuUpdateRequest.builder()
                .name("Night Menu")
                .menuType(MenuType.NIGHT)
                .build();

        assertThat(messagesFor(validator.validate(request)))
                .containsExactly("Status cannot be null");
    }

    @Test
    void shouldAcceptValidMenuItemCreateRequest() {
        assertThat(validator.validate(validMenuItemCreateRequest())).isEmpty();
    }

    @Test
    void shouldRejectBlankMenuItemName() {
        MenuItemCreateRequest request = validMenuItemCreateRequest();
        request.setName(" ");

        assertThat(messagesFor(validator.validate(request)))
                .containsExactly("Menu item name cannot be blank");
    }

    @Test
    void shouldRejectMissingPrice() {
        MenuItemCreateRequest request = validMenuItemCreateRequest();
        request.setPrice(null);

        assertThat(messagesFor(validator.validate(request)))
                .containsExactly("Price cannot be null");
    }

    @Test
    void shouldRejectZeroPrice() {
        MenuItemCreateRequest request = validMenuItemCreateRequest();
        request.setPrice(BigDecimal.ZERO);

        assertThat(messagesFor(validator.validate(request)))
                .containsExactly("Price must be greater than zero");
    }

    @Test
    void shouldRejectCurrencyThatIsNotThreeLetters() {
        MenuItemCreateRequest request = validMenuItemCreateRequest();
        request.setCurrency("TR");

        assertThat(messagesFor(validator.validate(request)))
                .containsExactly("Currency must be a 3 letter ISO code");
    }

    @Test
    void shouldAllowMenuItemWithoutCurrency() {
        MenuItemCreateRequest request = validMenuItemCreateRequest();
        request.setCurrency(null);

        assertThat(validator.validate(request)).isEmpty();
    }

    @Test
    void shouldRequireStatusOnMenuItemUpdate() {
        MenuItemUpdateRequest request = MenuItemUpdateRequest.builder()
                .name("Margherita")
                .price(BigDecimal.valueOf(250))
                .build();

        assertThat(messagesFor(validator.validate(request)))
                .containsExactly("Status cannot be null");
    }

    private MenuCreateRequest validMenuCreateRequest() {
        return MenuCreateRequest.builder()
                .restaurantId("restaurant-1")
                .name("Night Menu")
                .description("Served after 20:00")
                .menuType(MenuType.NIGHT)
                .status(MenuStatus.ACTIVE)
                .build();
    }

    private MenuItemCreateRequest validMenuItemCreateRequest() {
        return MenuItemCreateRequest.builder()
                .name("Margherita")
                .description("Tomato, mozzarella, basil")
                .category("Pizza")
                .price(BigDecimal.valueOf(250))
                .currency("TRY")
                .build();
    }

    private <T> List<String> messagesFor(Set<ConstraintViolation<T>> violations) {
        return violations.stream()
                .map(ConstraintViolation::getMessage)
                .toList();
    }
}
