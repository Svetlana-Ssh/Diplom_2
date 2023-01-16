package praktikum.user;

import io.qameta.allure.junit4.DisplayName;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.hamcrest.Matchers.equalTo;

public class TestUserLogin {
    private User user;
    private String userAccessToken;
    private final UserClient userClient = new UserClient();

    @Before
    public void setUp() {
        user = new User("ssh-test@yandex.ru", "123qweASD", "ssh-test");
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
    @DisplayName("Успешный логин под существующим пользователем.")
    public void loginWithExistingUser(){
        userClient.create(user);
        userClient.login(user).assertThat().statusCode(200).and().body("success", equalTo(true));
    }

    @Test
    @DisplayName("Не проходит логин пользователя с неверным логином и паролем.")
    public void loginWithWrongLoginAndPasswordFails(){
        userClient.create(user);
        User corruptedUser = new User("Wrong" + user.getEmail(), "Wrong" + user.getPassword(),user.getName());
        userClient.login(corruptedUser).assertThat().statusCode(401).and().body("success", equalTo(false)).and().body("message", equalTo("email or password are incorrect"));
    }
}
