package tests;

import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;
import lombok.SneakyThrows;
import org.junit.jupiter.api.*;
import ru.netology.data.DBUtils;
import ru.netology.data.Status;
import ru.netology.page.CreditPage;
import ru.netology.page.MainPage;

import static com.codeborne.selenide.Selenide.open;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FormCreditPayment {
    private static String appURL = System.getProperty("app.url");
    private static String appPORT = System.getProperty("app.port");

    MainPage mainPage = new MainPage();
    CreditPage creditPage = new CreditPage();

    @BeforeEach
    void setUpPage() {
        open(appURL + ":" + appPORT);
        creditPage = mainPage.buttonCreditClick();
    }

    @BeforeAll
    static void setUpAll() {
        SelenideLogger.addListener("allure", new AllureSelenide());
    }

    @AfterEach
    @SneakyThrows
    void clearAll() {
        DBUtils.clearAllData();
    }

    @AfterAll
    static void tearDownAll() {
        SelenideLogger.removeListener("allure");
    }

    // Позитивные тесты
    @Test
    @DisplayName("Оплата картой со статусом APPROVED, покупка в кредит, введение валидных данных")
    @SneakyThrows
    void shouldPayByApprovedCardInCredit() {
        creditPage.setCardNumber("4444444444444441");
        creditPage.setCardMonth("10");
        creditPage.setCardYear("22");
        creditPage.setCardOwner("Антон Попов");
        creditPage.setCardCVV("999");
        creditPage.pushСontinueButton();
        creditPage.checkMessageSuccess();
    }

    @Test
    @DisplayName("Оплата картой со статусом APPROVED, граничные значения в поле месяц, введение валидных данных")
    @SneakyThrows
    void shouldPayByApprovedCardInCreditMonth() {
        creditPage.setCardNumber("4444444444444441");
        creditPage.setCardMonth("11");
        creditPage.setCardYear("22");
        creditPage.setCardOwner("Антон Попов");
        creditPage.setCardCVV("999");
        creditPage.pushСontinueButton();
        creditPage.checkMessageSuccess();
    }

    @Test
    @DisplayName("Оплата картой со статусом APPROVED, граничные значения в поле год, введение валидных данных")
    @SneakyThrows
    void shouldPayByApprovedCardInCreditYear() {
        creditPage.setCardNumber("4444444444444441");
        creditPage.setCardMonth("10");
        creditPage.setCardYear("23");
        creditPage.setCardOwner("Антон Попов");
        creditPage.setCardCVV("999");
        creditPage.pushСontinueButton();
        creditPage.checkMessageSuccess();
    }

    @Test   // Баг № 9, оплата проходит
    @DisplayName("Оплата картой со статусом DECLINED, покупка в кредит, введение валидных данных")
    @SneakyThrows
    void shouldNoPayByDeclinedCardInCredit() {
        creditPage.setCardNumber("4444444444444442");
        creditPage.setCardMonth("10");
        creditPage.setCardYear("22");
        creditPage.setCardOwner("Антон Попов");
        creditPage.setCardCVV("999");
        creditPage.pushСontinueButton();
        creditPage.checkMessageError();
    }

    // Негативные тесты
    @Test   // Баг № 10, появляется два уведомления: "Ошибка", а затем "Успешно"
    @DisplayName("Оплата неизвестной картой, покупка в кредит, введение валидных данных, за исключением номера карты")
    @SneakyThrows
    void shouldNoPayByUnknownCardInCredit() {
        creditPage.setCardNumber("4444444444444443");
        creditPage.setCardMonth("10");
        creditPage.setCardYear("22");
        creditPage.setCardOwner("Антон Попов");
        creditPage.setCardCVV("999");
        creditPage.pushСontinueButton();
        creditPage.checkMessageError();
    }

    @Test // Баг № 11, неверное описание текста ошибки в некоторых полях, должно быть "Поле обязательно для заполнения"
    @DisplayName("Оплата картой со статусом APPROVED, покупка в кредит, пустые данные во всех полях")
    @SneakyThrows
    void shouldPayByApprovedCardInCreditEmpty() {
        creditPage.setCardNumber("");
        creditPage.setCardMonth("");
        creditPage.setCardYear("");
        creditPage.setCardOwner("");
        creditPage.setCardCVV("");
        creditPage.pushСontinueButton();
        creditPage.checkMessageRequiredField();
    }

    @Test
    @DisplayName("Оплата картой со статусом APPROVED, покупка в кредит, введение невалидных данных в поле Номер карты с использованием цифр длиной от 1 до 15")
    @SneakyThrows
    void shouldPayByApprovedCardInCreditNumberCardFieldNumber() {
        creditPage.setCardNumber("1111 2323");
        creditPage.setCardMonth("10");
        creditPage.setCardYear("22");
        creditPage.setCardOwner("Антон Попов");
        creditPage.setCardCVV("999");
        creditPage.pushСontinueButton();
        creditPage.checkMessageWrongFormat();
    }

    @Test
    @DisplayName("Оплата картой со статусом APPROVED, покупка в кредит, введение невалидных данных в поле Номер карты с использованием запрещенных символов")
    @SneakyThrows
    void shouldPayByApprovedCardInCreditNumberCardFieldSymbol() {
        creditPage.setCardNumber("HjgОр!#%&");
        creditPage.setCardMonth("10");
        creditPage.setCardYear("22");
        creditPage.setCardOwner("Антон Попов");
        creditPage.setCardCVV("999");
        creditPage.pushСontinueButton();
        creditPage.checkMessageWrongFormat();
    }

    @Test // Баг № 3, неверное описание текста ошибки, должно быть "Поле обязательно для заполнения"
    @DisplayName("Оплата картой со статусом APPROVED, покупка в кредит, пустые данные в поле Номер карты")
    @SneakyThrows
    void shouldPayByApprovedCardInCreditNumberCardEmpty() {
        creditPage.setCardNumber("");
        creditPage.setCardMonth("10");
        creditPage.setCardYear("22");
        creditPage.setCardOwner("Антон Попов");
        creditPage.setCardCVV("999");
        creditPage.pushСontinueButton();
        creditPage.checkMessageRequiredField();
    }

    @Test // Баг № 12, неверное описание текста ошибки, должно быть "Истек срок действия карты"
    @DisplayName("Оплата картой со статусом APPROVED, покупка в кредит, введение невалидных данных в поле Месяц с введением предыдущего месяца")
    @SneakyThrows
    void shouldPayByApprovedCardInCreditMonthFieldInCorrectMonth() {
        creditPage.setCardNumber("4444444444444441");
        creditPage.setCardMonth("08");
        creditPage.setCardYear("22");
        creditPage.setCardOwner("Антон Попов");
        creditPage.setCardCVV("999");
        creditPage.pushСontinueButton();
        creditPage.checkMessageOverDate();
    }

    @Test // Баг № 13, неверное описание текста ошибки, должно быть "Неверный формат"
    @DisplayName("Оплата картой со статусом APPROVED, покупка в кредит, введение невалидных данных в поле Месяц с использованием некорректного значения")
    @SneakyThrows
    void shouldPayByApprovedCardInCreditMonthFieldInCorrect() {
        creditPage.setCardNumber("4444444444444441");
        creditPage.setCardMonth("44");
        creditPage.setCardYear("22");
        creditPage.setCardOwner("Антон Попов");
        creditPage.setCardCVV("999");
        creditPage.pushСontinueButton();
        creditPage.checkMessageWrongFormat();
    }

    @Test
    @DisplayName("Оплата картой со статусом APPROVED, покупка в кредит, введение невалидных данных в поле Месяц с использованием 0")
    @SneakyThrows
    void shouldPayByApprovedCardInCreditMonthField0() {
        creditPage.setCardNumber("4444444444444441");
        creditPage.setCardMonth("0");
        creditPage.setCardYear("22");
        creditPage.setCardOwner("Антон Попов");
        creditPage.setCardCVV("999");
        creditPage.pushСontinueButton();
        creditPage.checkMessageWrongFormat();
    }

    @Test
    @DisplayName("Оплата картой со статусом APPROVED, покупка в кредит, введение невалидных данных в поле Месяц с использованием запрещенных символов")
    @SneakyThrows
    void shouldPayByApprovedCardInCreditMonthFieldSymbol() {
        creditPage.setCardNumber("4444444444444441");
        creditPage.setCardMonth("HjgОр!#%&");
        creditPage.setCardYear("22");
        creditPage.setCardOwner("Антон Попов");
        creditPage.setCardCVV("999");
        creditPage.pushСontinueButton();
        creditPage.checkMessageWrongFormat();
    }

    @Test // Баг № 3, неверное описание текста ошибки, должно быть "Поле обязательно для заполнения"
    @DisplayName("Оплата картой со статусом APPROVED, покупка в кредит, пустые данные в поле Месяц")
    @SneakyThrows
    void shouldPayByApprovedCardInCreditMonthEmpty() {
        creditPage.setCardNumber("4444444444444441");
        creditPage.setCardMonth("");
        creditPage.setCardYear("22");
        creditPage.setCardOwner("Антон Попов");
        creditPage.setCardCVV("999");
        creditPage.pushСontinueButton();
        creditPage.checkMessageRequiredField();
    }

    @Test
    @DisplayName("Оплата картой со статусом APPROVED, покупка в кредит, введение невалидных данных в поле Год с использованием предыдущего года")
    @SneakyThrows
    void shouldPayByApprovedCardInCreditYearFieldInCorrectYear() {
        creditPage.setCardNumber("4444444444444441");
        creditPage.setCardMonth("10");
        creditPage.setCardYear("21");
        creditPage.setCardOwner("Антон Попов");
        creditPage.setCardCVV("999");
        creditPage.pushСontinueButton();
        creditPage.checkMessageOverDate();
    }

    @Test
    @DisplayName("Оплата картой со статусом APPROVED, покупка в кредит, введение невалидных данных в поле Год с использованием некорректного значения")
    @SneakyThrows
    void shouldPayByApprovedCardInCreditYearFieldInCorrect() {
        creditPage.setCardNumber("4444444444444441");
        creditPage.setCardMonth("10");
        creditPage.setCardYear("44");
        creditPage.setCardOwner("Антон Попов");
        creditPage.setCardCVV("999");
        creditPage.pushСontinueButton();
        creditPage.checkMessageWrongDate();
    }

    @Test
    @DisplayName("Оплата картой со статусом APPROVED, покупка в кредит, введение невалидных данных в поле Год с использованием 0")
    @SneakyThrows
    void shouldPayByApprovedCardInCreditYearField0() {
        creditPage.setCardNumber("4444444444444441");
        creditPage.setCardMonth("10");
        creditPage.setCardYear("0");
        creditPage.setCardOwner("Антон Попов");
        creditPage.setCardCVV("999");
        creditPage.pushСontinueButton();
        creditPage.checkMessageWrongFormat();
    }

    @Test
    @DisplayName("Оплата картой со статусом APPROVED, покупка в кредит, введение невалидных данных в поле Год с использованием запрещенных символов")
    @SneakyThrows
    void shouldPayByApprovedCardInCreditYearFieldSymbol() {
        creditPage.setCardNumber("4444444444444441");
        creditPage.setCardMonth("10");
        creditPage.setCardYear("HjgОр!#%&");
        creditPage.setCardOwner("Антон Попов");
        creditPage.setCardCVV("999");
        creditPage.pushСontinueButton();
        creditPage.checkMessageWrongFormat();
    }

    @Test // Баг № 3, неверное описание текста ошибки, должно быть "Поле обязательно для заполнения"
    @DisplayName("Оплата картой со статусом APPROVED, покупка в кредит, пустые данные в поле Год")
    @SneakyThrows
    void shouldPayByApprovedCardInCreditYearEmpty() {
        creditPage.setCardNumber("4444444444444441");
        creditPage.setCardMonth("10");
        creditPage.setCardYear("");
        creditPage.setCardOwner("Антон Попов");
        creditPage.setCardCVV("999");
        creditPage.pushСontinueButton();
        creditPage.checkMessageRequiredField();
    }

    @Test // Баг № 14, отсутствует появление ошибки "Неверный формат"
    @DisplayName("Оплата картой со статусом APPROVED, покупка в кредит, введение невалидных данных в поле Владелец с использованием некорректного значения")
    @SneakyThrows
    void shouldPayByApprovedCardInCreditOwnerFieldInCorrect() {
        creditPage.setCardNumber("4444444444444441");
        creditPage.setCardMonth("10");
        creditPage.setCardYear("22");
        creditPage.setCardOwner("TUhjk");
        creditPage.setCardCVV("999");
        creditPage.pushСontinueButton();
        creditPage.checkMessageWrongFormat();
    }

    @Test // Баг № 15, отсутствует появление ошибки "Неверный формат"
    @DisplayName("Оплата картой со статусом APPROVED, покупка в кредит, введение невалидных данных в поле Владелец с использованием запрещенных символов")
    @SneakyThrows
    void shouldPayByApprovedCardInCreditOwnerFieldSymbol() {
        creditPage.setCardNumber("4444444444444441");
        creditPage.setCardMonth("10");
        creditPage.setCardYear("22");
        creditPage.setCardOwner("11;?*!(;%");
        creditPage.setCardCVV("999");
        creditPage.pushСontinueButton();
        creditPage.checkMessageWrongFormat();
    }

    @Test
    @DisplayName("Оплата картой со статусом APPROVED, покупка в кредит, пустые данные в поле Владелец")
    @SneakyThrows
    void shouldPayByApprovedCardInCreditOwnerEmpty() {
        creditPage.setCardNumber("4444444444444441");
        creditPage.setCardMonth("10");
        creditPage.setCardYear("20");
        creditPage.setCardOwner("");
        creditPage.setCardCVV("999");
        creditPage.pushСontinueButton();
        creditPage.checkMessageRequiredField();
    }

    @Test
    @DisplayName("Оплата картой со статусом APPROVED, покупка в кредит, введение невалидных данных в поле CVC/CVV с использованием 0")
    @SneakyThrows
    void shouldPayByApprovedCardInCreditCVCCVVField0() {
        creditPage.setCardNumber("4444444444444441");
        creditPage.setCardMonth("10");
        creditPage.setCardYear("22");
        creditPage.setCardOwner("Антон Попов");
        creditPage.setCardCVV("0");
        creditPage.pushСontinueButton();
        creditPage.checkMessageWrongFormat();
    }

    @Test
    @DisplayName("Оплата картой со статусом APPROVED, покупка в кредит, введение невалидных данных в поле CVC/CVV с использованием запрещенных символов")
    @SneakyThrows
    void shouldPayByApprovedCardInCreditCVCCVVFieldSymbol() {
        creditPage.setCardNumber("4444444444444441");
        creditPage.setCardMonth("10");
        creditPage.setCardYear("22");
        creditPage.setCardOwner("Антон Попов");
        creditPage.setCardCVV("@$!");
        creditPage.pushСontinueButton();
        creditPage.checkMessageWrongFormat();
    }

    @Test
    @DisplayName("Оплата картой со статусом APPROVED, покупка в кредит, пустые данные в поле CVC/CVV")
    @SneakyThrows
    void shouldPayByApprovedCardInCreditCVCCVVEmpty() {
        creditPage.setCardNumber("4444444444444441");
        creditPage.setCardMonth("10");
        creditPage.setCardYear("22");
        creditPage.setCardOwner("Антон Попов");
        creditPage.setCardCVV("");
        creditPage.pushСontinueButton();
        creditPage.checkMessageRequiredField();
    }

    @Test
    @DisplayName("Оплата картой со статусом APPROVED, покупка в кредит, введение валидных данных с последующей проверкой данных в СУБД")
    @SneakyThrows
    void shouldPayByApprovedCardInCreditStatusInDB() {
        creditPage.setCardNumber("4444444444444441");
        creditPage.setCardMonth("10");
        creditPage.setCardYear("22");
        creditPage.setCardOwner("Антон Попов");
        creditPage.setCardCVV("999");
        creditPage.pushСontinueButton();
        creditPage.checkMessageSuccess();
        DBUtils.checkCreditStatus(Status.APPROVED);
    }

    @Test
    @DisplayName("Оплата картой со статусом DECLINED, покупка в кредит, введение валидных данныхс последующей проверкой данных в СУБД")
    @SneakyThrows
    void shouldNoPayByDeclidedCardInCreditStatusInDB() {
        creditPage.setCardNumber("4444444444444442");
        creditPage.setCardMonth("10");
        creditPage.setCardYear("22");
        creditPage.setCardOwner("Антон Попов");
        creditPage.setCardCVV("999");
        creditPage.pushСontinueButton();
        creditPage.checkMessageSuccess();
        DBUtils.checkCreditStatus(Status.DECLINED);
    }

    @Test
    @DisplayName("Оплата картой со статусом APPROVED, покупка в кредит, введение валидных данных с последующей проверкой предоставленной суммы в СУБД")
    @SneakyThrows
    void shouldApprovedPayAmount() {
        creditPage.setCardNumber("4444444444444441");
        creditPage.setCardMonth("10");
        creditPage.setCardYear("22");
        creditPage.setCardOwner("Антон Попов");
        creditPage.setCardCVV("999");
        creditPage.pushСontinueButton();
        creditPage.checkMessageSuccess();
        assertTrue(DBUtils.checkEntityCount() == 3);
        assertEquals(mainPage.checkAmount(), DBUtils.getPayAmount());
    }
}
