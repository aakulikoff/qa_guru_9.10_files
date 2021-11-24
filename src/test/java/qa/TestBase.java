package qa;

import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.BeforeAll;

public class TestBase {

    @BeforeAll
    void beforeAll() {
        SelenideLogger.addListener("allure", new AllureSelenide());
    }
}
