package ru.netology.test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;
import static ru.netology.data.DataGenerator.Registration.*;
import static ru.netology.data.DataGenerator.*;

public class AuthTest {

    @BeforeEach
    void setUp() {
        open("http://localhost:9999");
    }

    @Test
    @DisplayName("Should successfully login with registered user")
    void shouldLoginWithAccount() {

        var user = getRegisteredUser(generateLogin(),"active");

        $("[data-test-id='login'] input").sendKeys(user.getLogin());
        $("[data-test-id='password'] input").sendKeys(user.getPassword());
        $("[data-test-id='action-login']").click();
        $x("//*[contains(text(), 'Личный кабинет')]").shouldBe(visible);
    }

    @Test
    @DisplayName("Should rewrite userdata with the same login")
    void shouldRewriteWithTheSameLogin() {

        var user = getRegisteredUser(generateLogin(), "active");
        String oldPassword = user.getPassword();

        var newUser = getRegisteredUser(user.getLogin(), "active");

        $("[data-test-id='login'] input").sendKeys(user.getLogin());
        $("[data-test-id='password'] input").sendKeys(oldPassword);
        $("[data-test-id='action-login']").click();
        $("[data-test-id='error-notification']  [class='notification__content']").shouldHave(text("Неверно указан логин или пароль"));

        $("[data-test-id='password'] input").doubleClick().sendKeys(newUser.getPassword());
        $("[data-test-id='action-login']").click();
        $x("//*[contains(text(), 'Личный кабинет')]").shouldBe(visible);
    }

    @Test
    @DisplayName("Should get error when login with not registered user")
    void shouldNotLoginWithAccountBecauseNotReg() {

        var user = getUser(generateLogin(), "active");

        $("[data-test-id='login'] input").sendKeys(user.getLogin());
        $("[data-test-id='password'] input").sendKeys(user.getPassword());
        $("[data-test-id='action-login']").click();
        $("[data-test-id='error-notification']  [class='notification__content']").shouldHave(text("Неверно указан логин или пароль"));
    }

    @Test
    @DisplayName("Should get error when login blocked user")
    void shouldNotLoginWithAccountBecauseBlocked() {

        var user = getRegisteredUser(generateLogin(), "blocked");

        $("[data-test-id='login'] input").sendKeys(user.getLogin());
        $("[data-test-id='password'] input").sendKeys(user.getPassword());
        $("[data-test-id='action-login']").click();
        $("[data-test-id='error-notification']  [class='notification__content']").shouldHave(text("Пользователь заблокирован"));
    }

    @Test
    @DisplayName("Should get error when login with incorrect login")
    void shouldNotLoginWithAccountBecauseIncorrectLogin() {

        var user = getRegisteredUser(generateLogin(), "active");

        $("[data-test-id='login'] input").sendKeys(generateLogin());
        $("[data-test-id='password'] input").sendKeys(user.getPassword());
        $("[data-test-id='action-login']").click();
        $("[data-test-id='error-notification']  [class='notification__content']").shouldHave(text("Неверно указан логин или пароль"));
    }

    @Test
    @DisplayName("Should get error when login with incorrect password")
    void shouldNotLoginWithAccountBecauseIncorrectPassword() {

        var user = getRegisteredUser(generateLogin(), "active");

        $("[data-test-id='login'] input").sendKeys(user.getLogin());
        $("[data-test-id='password'] input").sendKeys(generatePassword());
        $("[data-test-id='action-login']").click();
        $("[data-test-id='error-notification']  [class='notification__content']").shouldHave(text("Неверно указан логин или пароль"));
    }

}
