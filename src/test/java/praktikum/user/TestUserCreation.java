package praktikum.user;

import io.qameta.allure.junit4.DisplayName;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import static org.hamcrest.Matchers.equalTo;

public class TestUserCreation {
    private User user;
    private final UserGenerator userGenerator = new UserGenerator();
    private String userAccessToken;
    private final UserClient userClient = new UserClient();

    @Before
    public void setUp() {
        user = userGenerator.random();
    }

    @After
    public void deleteUser() {
        userAccessToken = userClient.getAccessTokenOnLogin(user);
        System.out.println("Удаляем пользователя с userAccessToken, если он не null: " + userAccessToken);
        if (userAccessToken != null) {
            userClient.delete(userAccessToken);
        }
    }

    @Test
    @DisplayName("Успешное создание уникального пользователя, status code and body.")
    public void createNewUniqueUser(){
        userClient.create(user).assertThat().statusCode(200).and().body("success", equalTo(true));
    }

    @Test
    @DisplayName("Нельзя еще раз создать пользователя, который уже зарегистрирован.")
    public void createDuplicateUserFails(){
        userClient.create(user);
        userClient.create(user).assertThat().statusCode(403).and().body("success", equalTo(false));
    }

    @Test
    @DisplayName("Нельзя создать пользователя, не заполнив обязательное поле email.")
    public void createUserWithoutEmailFails(){
        user.setEmail("");
        userClient.create(user).assertThat().statusCode(403).and().body("success", equalTo(false)).and().body("message", equalTo("Email, password and name are required fields"));
    }
}
