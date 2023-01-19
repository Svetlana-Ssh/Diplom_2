package praktikum.user;

import org.apache.commons.lang3.RandomStringUtils;

public class UserGenerator {

    public User generic() {
        return new User("Ssh_Test_User@yandex.ru", "123qweASD", "Ssh_Test_User");
    }

    public User random() {
        return new User(
                RandomStringUtils.randomAlphanumeric(10) + "@yandex.ru",
                RandomStringUtils.randomAlphanumeric(10),
                RandomStringUtils.randomAlphanumeric(10));
    }
}
