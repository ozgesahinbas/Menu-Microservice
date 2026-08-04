package io.ozgesahinbas.restaurant.menu.exception;

import io.ozgesahinbas.restaurant.menu.dto.ErrorResponse;
import io.ozgesahinbas.restaurant.menu.dto.MenuCreateRequest;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void shouldMapMenuNotFoundToNotFound() {
        ResponseEntity<ErrorResponse> response = handler.handleNotFound(
                new MenuNotFoundException("menu::404"), request("/menus/menu::404"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

        ErrorResponse body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.getStatus()).isEqualTo(404);
        assertThat(body.getError()).isEqualTo("Not Found");
        assertThat(body.getMessage()).isEqualTo("Menu not found with id: menu::404");
        assertThat(body.getPath()).isEqualTo("/menus/menu::404");
        assertThat(body.getTimestamp()).isNotNull();
        assertThat(body.getValidationErrors()).isNull();
    }

    @Test
    void shouldMapMenuItemNotFoundToNotFound() {
        ResponseEntity<ErrorResponse> response = handler.handleNotFound(
                new MenuItemNotFoundException("menu-item::404"), request("/menus/menu::1/items"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getMessage())
                .isEqualTo("Menu item not found with id: menu-item::404");
    }

    @Test
    void shouldCollectFieldErrorsAndFallBackWhenMessageIsMissing() throws Exception {
        BeanPropertyBindingResult bindingResult =
                new BeanPropertyBindingResult(new MenuCreateRequest(), "menuCreateRequest");
        bindingResult.addError(
                new FieldError("menuCreateRequest", "name", "Menu name cannot be blank"));
        bindingResult.addError(new FieldError("menuCreateRequest", "menuType", null));

        MethodArgumentNotValidException exception =
                new MethodArgumentNotValidException(methodParameter(), bindingResult);

        ResponseEntity<ErrorResponse> response =
                handler.handleValidation(exception, request("/menus"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

        ErrorResponse body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.getMessage()).isEqualTo("Request validation failed");
        assertThat(body.getValidationErrors())
                .containsEntry("name", "Menu name cannot be blank")
                .containsEntry("menuType", "Invalid value");
    }

    @Test
    void shouldMapUnreadableBodyToBadRequest() {
        ResponseEntity<ErrorResponse> response = handler.handleUnreadableBody(
                new HttpMessageNotReadableException("broken json", null, null), request("/menus"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getMessage()).isEqualTo("Malformed request body");
    }

    @Test
    void shouldKeepStatusOfUnknownPath() {
        ResponseEntity<ErrorResponse> response = handler.handleUnexpected(
                new NoResourceFoundException(HttpMethod.GET, "/menu"), request("/menu"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getPath()).isEqualTo("/menu");
    }

    @Test
    void shouldKeepStatusOfUnsupportedHttpMethod() {
        ResponseEntity<ErrorResponse> response = handler.handleUnexpected(
                new HttpRequestMethodNotSupportedException("PATCH"), request("/menus/menu::1"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.METHOD_NOT_ALLOWED);
    }

    @Test
    void shouldKeepStatusOfUnsupportedMediaType() {
        ResponseEntity<ErrorResponse> response = handler.handleUnexpected(
                new HttpMediaTypeNotSupportedException("text/plain is not supported"),
                request("/menus"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNSUPPORTED_MEDIA_TYPE);
    }

    @Test
    void shouldMapUnexpectedExceptionToInternalServerError() {
        ResponseEntity<ErrorResponse> response = handler.handleUnexpected(
                new IllegalStateException("couchbase is unreachable"), request("/menus"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getMessage()).isEqualTo("An unexpected error occurred");
        assertThat(response.getBody().getStatus()).isEqualTo(500);
    }

    private MockHttpServletRequest request(String uri) {
        return new MockHttpServletRequest("POST", uri);
    }

    private MethodParameter methodParameter() throws NoSuchMethodException {
        Method method = GlobalExceptionHandlerTest.class
                .getDeclaredMethod("validatedEndpoint", MenuCreateRequest.class);

        return new MethodParameter(method, 0);
    }

    /** Only exists so a realistic {@link MethodParameter} can be built above. */
    @SuppressWarnings("unused")
    private void validatedEndpoint(MenuCreateRequest request) {
        // No behaviour needed.
    }
}
