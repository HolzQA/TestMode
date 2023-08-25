package ru.netology.test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.netology.data.DataGenerator;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;
import static io.restassured.RestAssured.given;
import static ru.netology.data.DataGenerator.Registration.*;
import static ru.netology.data.DataGenerator.*;
//import static ru.netology.data.DataGenerator.requestSpec;

public class AuthTest {

    @BeforeEach
    void setUp() {
        open("http://localhost:9999");
    }

    @Test
    @DisplayName("Should successfully login with registered user")
    void shouldLoginWithAccount() {

        var user = getRegisteredUser("active");

        $("[data-test-id='login'] input").sendKeys(user.getLogin());
        $("[data-test-id='password'] input").sendKeys(user.getPassword());
        $("[data-test-id='action-login']").click();
        $x("//*[contains(text(), 'Личный кабинет')]").shouldBe(visible);
    }

    @Test
    @DisplayName("Should rewrite userdata with the same login")
    void shouldRewriteWithTheSameLogin() {

        var user = getRegisteredUser("active");
        String newPassword = generatePassword();

        given()
                .spec(requestSpec)
                .body(new DataGenerator.RegistrationDto(user.getLogin(), newPassword, "active"))
                .when()
                .post("/api/system/users")
                .then()
                .statusCode(200);

        $("[data-test-id='login'] input").sendKeys(user.getLogin());
        $("[data-test-id='password'] input").sendKeys(user.getPassword());
        $("[data-test-id='action-login']").click();
        $("[data-test-id='error-notification']  [class='notification__content']").shouldHave(text("Неверно указан логин или пароль"));

        $("[data-test-id='password'] input").doubleClick().sendKeys(newPassword);
        $("[data-test-id='action-login']").click();
        $x("//*[contains(text(), 'Личный кабинет')]").shouldBe(visible);
    }

    @Test
    @DisplayName("Should get error when login with not registered user")
    void shouldNotLoginWithAccountBecauseNotReg() {

        var user = getUser("active");

        $("[data-test-id='login'] input").sendKeys(user.getLogin());
        $("[data-test-id='password'] input").sendKeys(user.getPassword());
        $("[data-test-id='action-login']").click();
        $("[data-test-id='error-notification']  [class='notification__content']").shouldHave(text("Неверно указан логин или пароль"));
    }

    @Test
    @DisplayName("Should get error when login blocked user")
    void shouldNotLoginWithAccountBecauseBlocked() {

        var user = getRegisteredUser("blocked");

        $("[data-test-id='login'] input").sendKeys(user.getLogin());
        $("[data-test-id='password'] input").sendKeys(user.getPassword());
        $("[data-test-id='action-login']").click();
        $("[data-test-id='error-notification']  [class='notification__content']").shouldHave(text("Пользователь заблокирован"));
    }

    @Test
    @DisplayName("Should get error when login with incorrect login")
    void shouldNotLoginWithAccountBecauseIncorrectLogin() {

        var user = getRegisteredUser("active");

        $("[data-test-id='login'] input").sendKeys(user.getLogin() + "m");
        $("[data-test-id='password'] input").sendKeys(user.getPassword());
        $("[data-test-id='action-login']").click();
        $("[data-test-id='error-notification']  [class='notification__content']").shouldHave(text("Неверно указан логин или пароль"));
    }

    @Test
    @DisplayName("Should get error when login with incorrect password")
    void shouldNotLoginWithAccountBecauseIncorrectPassword() {

        var user = getRegisteredUser("active");

        $("[data-test-id='login'] input").sendKeys(user.getLogin());
        $("[data-test-id='password'] input").sendKeys(user.getPassword() + "m");
        $("[data-test-id='action-login']").click();
        $("[data-test-id='error-notification']  [class='notification__content']").shouldHave(text("Неверно указан логин или пароль"));
    }

}
