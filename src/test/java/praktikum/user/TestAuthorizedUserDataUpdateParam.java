package praktikum.user;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import praktikum.constants.UserPropertiesState;
import static praktikum.constants.UserPropertiesState.*;
import static org.hamcrest.Matchers.equalTo;


@RunWith(Parameterized.class)
public class TestAuthorizedUserDataUpdateParam {
    private final UserPropertiesState email;
    private final UserPropertiesState password;
    private final UserPropertiesState name;
    private final int expectedStatusCode;
    private final boolean expectedSuccessState;

    private User user;
    private final UserGenerator userGenerator = new UserGenerator();
    private final UserClient userClient = new UserClient();
    private String userAccessToken;

    public TestAuthorizedUserDataUpdateParam(UserPropertiesState email, UserPropertiesState password, UserPropertiesState name, int expectedStatusCode, boolean expectedSuccessState) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.expectedStatusCode = expectedStatusCode;
        this.expectedSuccessState = expectedSuccessState;
    }

    @Before
    public void setUp() {
        user = userGenerator.random();
    }

    @After
    public void deleteUser() {
        System.out.println("Удаляем пользователя с userAccessToken, если он не null: " + userAccessToken);
        if (userAccessToken != null) {
            userClient.delete(userAccessToken);
        }
    }

    @Parameterized.Parameters(name = "email: {0}, password: {1}, name: {2}")
    //Набор тестовых данных. Проверяются разные комбинации изменения email, password и name.
    public static Object[][] checkUpdate() {
        return new Object[][]{
                {NEW_VALID_VALUE, NOT_CHANGED, NOT_CHANGED, 200, true},
                {NOT_CHANGED, NEW_VALID_VALUE, NOT_CHANGED, 200, true},
                {NOT_CHANGED, NOT_CHANGED, NEW_VALID_VALUE, 200, true},
                {NEW_VALID_VALUE, NEW_VALID_VALUE, NEW_VALID_VALUE, 200, true},
                {NEW_VALID_VALUE, NEW_VALID_VALUE, NOT_CHANGED, 200, true},
                {NEW_VALID_VALUE, NOT_CHANGED, NEW_VALID_VALUE, 200, true},
                {NOT_CHANGED, NEW_VALID_VALUE, NEW_VALID_VALUE, 200, true},
        };
    }

    @Test
    @Description("Проверка обновления данных пользователя при разных комбинациях допустимых значений email, password, name. (NEW_VALID_VALUE, NOT_CHANGED)")
    public void UpdateUserData() {

        //Создаем пользователя, свойства которого будем менять в тесте.
        userClient.create(user);
        userAccessToken = userClient.getAccessTokenOnLogin(user);
        System.out.println("UserAccessToken для исходного пользователя: " + userAccessToken);

        //Преобразование существующих свойств пользователя в зависимости от параметров теста.
        switch(email) {
            case NEW_VALID_VALUE:
                user.setEmail("New" + user.getEmail());
                break;
            case NOT_CHANGED:
                break;
        }
        switch(password) {
            case NEW_VALID_VALUE:
                user.setPassword("New" + user.getPassword());
                break;
            case NOT_CHANGED:
                break;
        }
        switch(name) {
            case NEW_VALID_VALUE:
                user.setName("New" + user.getName());
                break;
            case NOT_CHANGED:
                break;
        }

        System.out.println("Отправляем запрос на изменение свойств пользователя со значениями: " + user);
        userClient.updateData(user, userAccessToken).assertThat().statusCode(expectedStatusCode).and().body("success", equalTo(expectedSuccessState));
    }
}
