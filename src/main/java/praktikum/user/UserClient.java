package praktikum.user;

import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import praktikum.Client;

public class UserClient extends Client {
    public static final String ROOT_REGISTER = "/auth/register";
    public static final String ROOT_LOGIN = "/auth/login";
    public static final String ROOT_USER = "/auth/user";

    @Step("Создать пользователя")
    public ValidatableResponse create(User user) {
        return spec()
                .body(user)
                .when()
                .post(ROOT_REGISTER)
                .then().log().all();
    }

    @Step("Залогиниться существующим пользователем")
    public ValidatableResponse login(User user) {
        return spec()
                .body(user)
                .when()
                .post(ROOT_LOGIN)
                .then().log().all();
    }

    @Step("Получить accessToken пользователя")
    public String getAccessTokenOnLogin(User user) {
        return spec()
                .body(user)
                .when()
                .post(ROOT_LOGIN)
                .then().log().all()
                .extract()
                .path("accessToken");
    }

    @Step("Удалить пользователя по accessToken")
    public ValidatableResponse delete(String accessToken) {
        return spec()
                .auth().oauth2(accessToken.replace("Bearer ", ""))
                .when()
                .delete(ROOT_USER)
                .then().log().all();
    }

    @Step("Обновить данные пользователя")
    public ValidatableResponse updateData(User user, String userAccessToken) {
        return spec()
                .auth().oauth2(userAccessToken.replace("Bearer ", ""))
                .body(user)
                .when()
                .patch(ROOT_USER)
                .then().log().all();
    }

    @Step("Неавторизованная попытка обновить данные пользователя")
    public ValidatableResponse updateDataUnauthorized(User user) {
        return spec()
                .body(user)
                .when()
                .patch(ROOT_USER)
                .then().log().all();
    }
}
