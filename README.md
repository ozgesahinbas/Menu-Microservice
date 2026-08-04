# Menu Microservice

Standalone Spring Boot microservice that manages restaurant menus, the items on
those menus, and the photo/video URLs attached to each item. Every record is
stored as its own document in Couchbase.

## Tech stack

| | |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot 3.5.6 (Web, Validation, AOP) |
| Database | Couchbase (Capella or local) via Spring Data Couchbase |
| Build | Gradle |
| Tests | JUnit 5, Mockito, AssertJ, MockMvc |
| Coverage | JaCoCo — 100%, enforced by the build |

## Project layout

```
Menu-Microservice/src/main/java/io/ozgesahinbas/restaurant/menu/
├── aspect/       LoggingAspect            — AOP: request/response, timing, exceptions
├── controller/   MenuController           — /menus
│                 MenuItemController       — /menus/{menuId}/items
│                 RestaurantMenuController — /restaurants/{restaurantId}/menus
├── dto/          Create/Update requests + ErrorResponse
├── entity/       Menu, MenuItem           — @Document, stored separately
├── enums/        MenuType, MenuStatus, MenuItemStatus
├── exception/    Domain exceptions + GlobalExceptionHandler
├── repository/   CouchbaseRepository interfaces
└── service/      MenuService / MenuItemService (+ implementations)
```

Menu and menu item logic are separated into two services: menu items are always
scoped to a menu, so `MenuItemService` delegates the menu lookup to
`MenuService` instead of duplicating the "does this menu exist" rule.

## Data model

Menus and items are **separate documents**. Document keys are prefixed so both
types can live in the same collection and stay readable in the Capella UI:

```
menu::0f1c9d24-...          menu-item::7b3a5e10-...
```

**Menu**: `id`, `restaurantId`, `name`, `description`, `menuType`, `status`,
`createdAt`, `updatedAt`

**MenuItem**: `id`, `menuId`, `restaurantId`, `name`, `description`, `category`,
`price`, `currency`, `photoUrls`, `videoUrls`, `allergens`, `ingredients`,
`status`, `createdAt`, `updatedAt`

`menuType`: `DAY`, `NIGHT`, `FOOD`, `BEVERAGE`, `WINE`, `DESSERT`
`status`: `ACTIVE`, `INACTIVE`

Deleting a menu also deletes its items — Couchbase has no cascading delete, so
`MenuServiceImpl.deleteMenu` removes them explicitly.

## Couchbase connection

Configuration lives in
[`application.yml`](Menu-Microservice/src/main/resources/application.yml) and is
driven entirely by environment variables, so no credential is committed:

| Variable | Default | Notes |
|---|---|---|
| `COUCHBASE_CONNECTION_STRING` | `couchbase://localhost` | Capella needs `couchbases://` (TLS) |
| `COUCHBASE_USERNAME` | `Administrator` | Capella **database access** user, not the login e-mail |
| `COUCHBASE_PASSWORD` | `password` | |
| `COUCHBASE_BUCKET` | `menu-service` | |
| `COUCHBASE_SCOPE` | `_default` | |
| `COUCHBASE_SSL_ENABLED` | `true` | set to `false` for a plain local cluster |

### TLS

Two things are needed for Capella, and neither happens on its own:

- `spring.couchbase.env.ssl.enabled` must be `true`. Spring Boot pre-builds the
  `ClusterEnvironment`, so the `couchbases://` scheme is not enough by itself —
  without the flag the SDK refuses to connect with *"Connection string scheme
  indicates a secure connection, but the pre-built ClusterEnvironment was not
  configured for TLS"*.
- Capella serves a certificate signed by Couchbase's own root CA, which is not
  in the JDK trust store. It ships here as
  [`couchbase-capella-root.pem`](Menu-Microservice/src/main/resources/couchbase-capella-root.pem)
  and is wired in as an SSL bundle; without it the handshake fails with
  *"PKIX path building failed"*. The same certificate can be downloaded from the
  Capella UI.

### Setting up Capella

1. **Bucket** — create a bucket named `menu-service`. Leave scope and collection
   at `_default`; the entities carry no `@Collection` annotation, so both
   document types land in the default collection.
2. **Database Access** — *Settings → Database Access* → create a user with
   read/write on `menu-service`. These credentials are what the service uses.
3. **Allowed IPs** — *Settings → Allowed IP Addresses* → add your own IP.
   Without this the connection times out with no useful error.
4. **Connection string** — *Connect* tab, copy the
   `couchbases://cb.<id>.cloud.couchbase.com` value.
5. **Primary index** — `findAll()` and `findByRestaurantId()` are N1QL queries,
   so the collection needs an index. If it is missing, create it in the Capella
   query editor:

   ```sql
   CREATE PRIMARY INDEX ON `menu-service`.`_default`.`_default`;
   ```

### Running

```bash
export COUCHBASE_CONNECTION_STRING="couchbases://cb.xxxxx.cloud.couchbase.com"
export COUCHBASE_USERNAME="menu_service_user"
export COUCHBASE_PASSWORD="********"

cd Menu-Microservice
./gradlew bootRun
```

The service listens on **8082**. In IntelliJ the same variables go into
*Run → Edit Configurations → Environment variables*.

For a local cluster instead of Capella, run Couchbase in Docker and keep the
defaults (`couchbase://localhost`).

## API

Base URL: `http://localhost:8082`

### Menus

| Method | Path | Response |
|---|---|---|
| POST | `/menus` | 201 + created menu |
| GET | `/menus` | 200 + all menus |
| GET | `/menus/{id}` | 200 / 404 |
| GET | `/restaurants/{restaurantId}/menus` | 200 + that restaurant's menus |
| PUT | `/menus/{id}` | 200 / 404 |
| DELETE | `/menus/{id}` | 204 / 404 |

### Menu items

| Method | Path | Response |
|---|---|---|
| POST | `/menus/{menuId}/items` | 201 + created item |
| GET | `/menus/{menuId}/items` | 200 / 404 |
| GET | `/menus/{menuId}/items/{itemId}` | 200 / 404 |
| PUT | `/menus/{menuId}/items/{itemId}` | 200 / 404 |
| DELETE | `/menus/{menuId}/items/{itemId}` | 204 / 404 |

Ready-to-run requests: [`docs/api-examples.http`](Menu-Microservice/docs/api-examples.http)

### Examples

**Create a menu**

```bash
curl -X POST http://localhost:8082/menus \
  -H 'Content-Type: application/json' \
  -d '{
        "restaurantId": "restaurant-1",
        "name": "Night Menu",
        "description": "Served after 20:00",
        "menuType": "NIGHT",
        "status": "ACTIVE"
      }'
```

```json
{
  "id": "menu::0f1c9d24-6b1e-4f3a-9c2d-1a2b3c4d5e6f",
  "restaurantId": "restaurant-1",
  "name": "Night Menu",
  "description": "Served after 20:00",
  "menuType": "NIGHT",
  "status": "ACTIVE",
  "createdAt": "2026-08-03T16:40:11.204",
  "updatedAt": "2026-08-03T16:40:11.204"
}
```

**Add an item with photo and video URLs**

```bash
curl -X POST 'http://localhost:8082/menus/menu::0f1c9d24-6b1e-4f3a-9c2d-1a2b3c4d5e6f/items' \
  -H 'Content-Type: application/json' \
  -d '{
        "name": "Margherita",
        "description": "Tomato, mozzarella, basil",
        "category": "Pizza",
        "price": 250.00,
        "currency": "TRY",
        "photoUrls": ["https://cdn.example.com/image-1.jpg"],
        "videoUrls": ["https://cdn.example.com/video-1.mp4"],
        "allergens": ["gluten", "lactose"],
        "ingredients": ["tomato", "mozzarella", "basil"]
      }'
```

**Error responses** are uniform, produced by `GlobalExceptionHandler`:

```json
{
  "timestamp": "2026-08-03T16:41:02.881",
  "status": 404,
  "error": "Not Found",
  "message": "Menu not found with id: menu::404",
  "path": "/menus/menu::404"
}
```

```json
{
  "timestamp": "2026-08-03T16:41:44.130",
  "status": 400,
  "error": "Bad Request",
  "message": "Request validation failed",
  "path": "/menus",
  "validationErrors": {
    "restaurantId": "Restaurant id cannot be blank",
    "menuType": "Menu type cannot be null"
  }
}
```

The same body is used for every failure, including the ones Spring itself
raises — unknown path (404), unsupported method (405) and unsupported media
type (415) all keep their own status instead of collapsing into a 500.

### Validation rules

| Field | Rule |
|---|---|
| `restaurantId` | required, not blank |
| `name` | required, not blank, ≤ 100 characters |
| `description` | ≤ 500 characters |
| `menuType` | required, one of the six menu types |
| `status` | required on update, defaults to `ACTIVE` on create |
| `price` | required, greater than zero |
| `currency` | optional, exactly 3 characters |

## AOP

[`LoggingAspect`](Menu-Microservice/src/main/java/io/ozgesahinbas/restaurant/menu/aspect/LoggingAspect.java)
covers three of the four purposes listed in the case:

- **Request/response logging** — `@Around` on the controller package
- **Execution time logging** — `@Around` on the service package, reported in ms
- **Exception logging** — `@AfterThrowing` on both packages

```
--> MenuController.createMenu(..) arguments=[MenuCreateRequest(restaurantId=restaurant-1, ...)]
MenuServiceImpl.createMenu(..) executed in 42 ms
<-- MenuController.createMenu(..) response=Menu(id=menu::0f1c9d24-..., ...)
MenuServiceImpl.getMenuById(..) failed with MenuNotFoundException: Menu not found with id: menu::404
```

## Tests

```bash
cd Menu-Microservice
./gradlew test          # 78 tests
./gradlew check         # tests + fails the build under 100% coverage
```

| Test class | Covers |
|---|---|
| `MenuServiceImplTest` | menu CRUD, cascade delete, not-found paths |
| `MenuItemServiceImplTest` | item CRUD, menu scoping, item-on-another-menu |
| `MenuControllerTest` | `/menus` endpoints, status codes, validation |
| `MenuItemControllerTest` | item endpoints, media URLs, validation |
| `RestaurantMenuControllerTest` | `/restaurants/{id}/menus` |
| `GlobalExceptionHandlerTest` | 404 / 400 / 500 mapping and error body |
| `RequestValidationTest` | every bean validation constraint |
| `MenuRequestMappingTest`, `MenuItemRequestMappingTest` | DTO → entity mapping, defaults |
| `LoggingAspectTest` | all three advices, success and failure paths |
| `MenuMicroserviceApplicationTests` | Spring context wiring |

Tests never touch a real cluster — repositories are mocked and the Couchbase
auto-configuration is excluded in `application-test.yml`.

### Coverage report

```bash
./gradlew test
open build/reports/jacoco/test/html/index.html
```

| Counter | Coverage |
|---|---|
| Instruction | 100% (636/636) |
| Branch | 100% (6/6) |
| Line | 100% (148/148) |
| Method | 100% (48/48) |
| Class | 100% (18/18) |

`jacocoTestCoverageVerification` fails the build below 100% on instructions and
branches. Two things are kept out of the measurement: `MenuMicroserviceApplication`
(only calls `SpringApplication.run`, excluded in `build.gradle`) and
Lombok-generated methods (`lombok.config` marks them `@Generated`, which JaCoCo
filters).
