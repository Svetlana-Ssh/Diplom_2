package praktikum.order;

import io.qameta.allure.junit4.DisplayName;
import org.junit.After;
import org.junit.Test;
import praktikum.user.User;
import praktikum.user.UserClient;
import static org.hamcrest.Matchers.equalTo;

public class TestOrderCreation {
    private final OrderGenerator generator = new OrderGenerator();
    private final OrderClient orderClient = new OrderClient();
    private final UserClient userClient = new UserClient();
    private String userAccessToken;

    @After
    public void deleteUser() {
        if (userAccessToken != null) {
            userClient.delete(userAccessToken);
        }
    }

    @Test
    @DisplayName("Успешное создание заказа без авторизации, с ингридиентами, status code and body.")
    public void createUnauthorizedOrder() {
        Order order = generator.simple();
        orderClient.createUnauthorized(order).assertThat().statusCode(200).and().body("success", equalTo(true));
    }

    @Test
    @DisplayName("Успешное создание заказа с авторизацией, с ингридиентами, status code and body.")
    public void createAutorizedOrder() {
        Order order = generator.simple();
        User user = new User("ssh-authOrder@yandex.ru", "123qweASD", "ssh-authOrder");
        userAccessToken = userClient.create(user).extract().body().path("accessToken");
        orderClient.createAuthorized(order, userAccessToken).assertThat().statusCode(200).and().body("success",equalTo(true));
    }

    @Test
    @DisplayName("Создание заказа без ингридиентов, без авторизации.")
    public void createUnauthorizedOrderNoIngredients() {
        Order order = generator.noIngredients();
        orderClient.createUnauthorized(order).assertThat().statusCode(400).and().body("success", equalTo(false)).body("message", equalTo("Ingredient ids must be provided"));
    }

    @Test
    @DisplayName("Создание заказа без ингридиентов, с авторизацией.")
    public void createAutorizedOrderNoIngredients() {
        Order order = generator.noIngredients();
        User user = new User("ssh-authOrder@yandex.ru", "123qweASD", "ssh-authOrder");
        userAccessToken = userClient.create(user).extract().body().path("accessToken");
        orderClient.createAuthorized(order, userAccessToken).assertThat().statusCode(400).and().body("success",equalTo(false)).body("message", equalTo("Ingredient ids must be provided"));
    }

    @Test
    @DisplayName("Попытка создать заказ с несуществующим в базе хешем ингредиента.")
    public void createUnauthorizedOrderNotExistingHashIngredients() {
        Order order = generator.notExistingHashIngredients();
        orderClient.createUnauthorized(order).assertThat().statusCode(400).and().body("success", equalTo(false)).body("message", equalTo("One or more ids provided are incorrect"));
    }
}
