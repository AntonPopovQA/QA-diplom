package tests;

import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;
import lombok.SneakyThrows;
import org.junit.jupiter.api.*;
import ru.netology.data.DBUtils;
import ru.netology.data.Status;
import ru.netology.page.MainPage;
import ru.netology.page.PaymentPage;

import static com.codeborne.selenide.Selenide.open;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class FormPayment {

    private static String appURL = System.getProperty("app.url");
    private static String appPORT = System.getProperty("app.port");

    MainPage mainPage = new MainPage();
    PaymentPage paymentPage = new PaymentPage();


    @BeforeEach
    void setUpPage() {
        open(appURL + ":" + appPORT);
        paymentPage = mainPage.buttonBuyClick();
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
    @DisplayName("Оплата картой со статусом APPROVED, обычная оплата, введение валидных данных")
    @SneakyThrows
    void shouldPayByApprovedCard() {
        paymentPage.setCardNumber("4444444444444441");
        paymentPage.setCardMonth("10");
        paymentPage.setCardYear("22");
        paymentPage.setCardOwner("Антон Попов");
        paymentPage.setCardCVV("999");
        paymentPage.pushСontinueButton();
        paymentPage.checkMessageSuccess();
    }

    @Test
    @DisplayName("Оплата картой со статусом APPROVED, граничные значения в поле месяц, введение валидных данных")
    @SneakyThrows
    void shouldPayByApprovedCardMonth() {
        paymentPage.setCardNumber("4444444444444441");
        paymentPage.setCardMonth("11");
        paymentPage.setCardYear("22");
        paymentPage.setCardOwner("Антон Попов");
        paymentPage.setCardCVV("999");
        paymentPage.pushСontinueButton();
        paymentPage.checkMessageSuccess();
    }

    @Test
    @DisplayName("Оплата картой со статусом APPROVED, граничные значения в поле год, введение валидных данных")
    @SneakyThrows
    void shouldPayByApprovedCardYear() {
        paymentPage.setCardNumber("4444444444444441");
        paymentPage.setCardMonth("10");
        paymentPage.setCardYear("23");
        paymentPage.setCardOwner("Антон Попов");
        paymentPage.setCardCVV("999");
        paymentPage.pushСontinueButton();
        paymentPage.checkMessageSuccess();
    }

    @Test   // Баг № 1, оплата проходит
    @DisplayName("Оплата картой со статусом DECLINED, обычная оплата, введение валидных данных")
    @SneakyThrows
    void shouldNoPayByDeclinedCard() {
        paymentPage.setCardNumber("4444444444444442");
        paymentPage.setCardMonth("10");
        paymentPage.setCardYear("22");
        paymentPage.setCardOwner("Антон Попов");
        paymentPage.setCardCVV("999");
        paymentPage.pushСontinueButton();
        paymentPage.checkMessageError();
    }

    // Негативные тесты
    @Test   // Баг № 2, появляется два уведомления: "Ошибка", а затем "Успешно"
    @DisplayName("Оплата неизвестной картой, обычная оплата, введение валидных данных, за исключением номера карты")
    @SneakyThrows
    void shouldNoPayByUnknownCard() {
        paymentPage.setCardNumber("4444444444444443");
        paymentPage.setCardMonth("10");
        paymentPage.setCardYear("22");
        paymentPage.setCardOwner("Антон Попов");
        paymentPage.setCardCVV("999");
        paymentPage.pushСontinueButton();
        paymentPage.checkMessageError();
    }

    @Test // Баг № 3, неверное описание текста ошибки в некоторых полях, должно быть "Поле обязательно для заполнения"
    @DisplayName("Оплата картой со статусом APPROVED, обычная покупка, пустые данные во всех полях")
    @SneakyThrows
    void shouldPayByApprovedCardEmpty() {
        paymentPage.setCardNumber("");
        paymentPage.setCardMonth("");
        paymentPage.setCardYear("");
        paymentPage.setCardOwner("");
        paymentPage.setCardCVV("");
        paymentPage.pushСontinueButton();
        paymentPage.checkMessageRequiredField();
    }

    @Test
    @DisplayName("Оплата картой со статусом APPROVED, обычная покупка, введение невалидных данных в поле Номер карты с использованием цифр длиной от 1 до 15")
    @SneakyThrows
    void shouldPayByApprovedCardNumberCardFieldNumber() {
        paymentPage.setCardNumber("1111 2323");
        paymentPage.setCardMonth("10");
        paymentPage.setCardYear("22");
        paymentPage.setCardOwner("Антон Попов");
        paymentPage.setCardCVV("999");
        paymentPage.pushСontinueButton();
        paymentPage.checkMessageWrongFormat();
    }

    @Test
    @DisplayName("Оплата картой со статусом APPROVED, обычная покупка, введение невалидных данных в поле Номер карты с использованием запрещенных символов")
    @SneakyThrows
    void shouldPayByApprovedCardNumberCardFieldSymbol() {
        paymentPage.setCardNumber("HjgОр!#%&");
        paymentPage.setCardMonth("10");
        paymentPage.setCardYear("22");
        paymentPage.setCardOwner("Антон Попов");
        paymentPage.setCardCVV("999");
        paymentPage.pushСontinueButton();
        paymentPage.checkMessageWrongFormat();
    }

    @Test // Баг № 3, неверное описание текста ошибки, должно быть "Поле обязательно для заполнения"
    @DisplayName("Оплата картой со статусом APPROVED, обычная покупка, пустые данные в поле Номер карты")
    @SneakyThrows
    void shouldPayByApprovedCardNumberCardEmpty() {
        paymentPage.setCardNumber("");
        paymentPage.setCardMonth("10");
        paymentPage.setCardYear("22");
        paymentPage.setCardOwner("Антон Попов");
        paymentPage.setCardCVV("999");
        paymentPage.pushСontinueButton();
        paymentPage.checkMessageRequiredField();
    }

    @Test // Баг № 4, неверное описание текста ошибки, должно быть "Истек срок действия карты"
    @DisplayName("Оплата картой со статусом APPROVED, обычная покупка, введение невалидных данных в поле Месяц с введением предыдущего месяца")
    @SneakyThrows
    void shouldPayByApprovedCardMonthFieldInCorrectMonth() {
        paymentPage.setCardNumber("4444444444444441");
        paymentPage.setCardMonth("08");
        paymentPage.setCardYear("22");
        paymentPage.setCardOwner("Антон Попов");
        paymentPage.setCardCVV("999");
        paymentPage.pushСontinueButton();
        paymentPage.checkMessageOverDate();
    }

    @Test // Баг № 5, неверное описание текста ошибки, должно быть "Неверный формат"
    @DisplayName("Оплата картой со статусом APPROVED, обычная покупка, введение невалидных данных в поле Месяц с использованием некорректного значения")
    @SneakyThrows
    void shouldPayByApprovedCardMonthFieldInCorrect() {
        paymentPage.setCardNumber("4444444444444441");
        paymentPage.setCardMonth("44");
        paymentPage.setCardYear("22");
        paymentPage.setCardOwner("Антон Попов");
        paymentPage.setCardCVV("999");
        paymentPage.pushСontinueButton();
        paymentPage.checkMessageWrongFormat();
    }

    @Test
    @DisplayName("Оплата картой со статусом APPROVED, обычная покупка, введение невалидных данных в поле Месяц с использованием 0")
    @SneakyThrows
    void shouldPayByApprovedCardMonthField0() {
        paymentPage.setCardNumber("4444444444444441");
        paymentPage.setCardMonth("0");
        paymentPage.setCardYear("22");
        paymentPage.setCardOwner("Антон Попов");
        paymentPage.setCardCVV("999");
        paymentPage.pushСontinueButton();
        paymentPage.checkMessageWrongFormat();
    }

    @Test
    @DisplayName("Оплата картой со статусом APPROVED, обычная покупка, введение невалидных данных в поле Месяц с использованием запрещенных символов")
    @SneakyThrows
    void shouldPayByApprovedCardMonthFieldSymbol() {
        paymentPage.setCardNumber("4444444444444441");
        paymentPage.setCardMonth("HjgОр!#%&");
        paymentPage.setCardYear("22");
        paymentPage.setCardOwner("Антон Попов");
        paymentPage.setCardCVV("999");
        paymentPage.pushСontinueButton();
        paymentPage.checkMessageWrongFormat();
    }

    @Test // Баг № 3, неверное описание текста ошибки, должно быть "Поле обязательно для заполнения"
    @DisplayName("Оплата картой со статусом APPROVED, обычная покупка, пустые данные в поле Месяц")
    @SneakyThrows
    void shouldPayByApprovedCardMonthEmpty() {
        paymentPage.setCardNumber("4444444444444441");
        paymentPage.setCardMonth("");
        paymentPage.setCardYear("22");
        paymentPage.setCardOwner("Антон Попов");
        paymentPage.setCardCVV("999");
        paymentPage.pushСontinueButton();
        paymentPage.checkMessageRequiredField();
    }

    @Test
    @DisplayName("Оплата картой со статусом APPROVED, обычная покупка, введение невалидных данных в поле Год с использованием предыдущего года")
    @SneakyThrows
    void shouldPayByApprovedCardYearFieldInCorrectYear() {
        paymentPage.setCardNumber("4444444444444441");
        paymentPage.setCardMonth("10");
        paymentPage.setCardYear("21");
        paymentPage.setCardOwner("Антон Попов");
        paymentPage.setCardCVV("999");
        paymentPage.pushСontinueButton();
        paymentPage.checkMessageOverDate();
    }

    @Test
    @DisplayName("Оплата картой со статусом APPROVED, обычная покупка, введение невалидных данных в поле Год с использованием некорректного значения")
    @SneakyThrows
    void shouldPayByApprovedCardYearFieldInCorrect() {
        paymentPage.setCardNumber("4444444444444441");
        paymentPage.setCardMonth("10");
        paymentPage.setCardYear("44");
        paymentPage.setCardOwner("Антон Попов");
        paymentPage.setCardCVV("999");
        paymentPage.pushСontinueButton();
        paymentPage.checkMessageWrongDate();
    }

    @Test
    @DisplayName("Оплата картой со статусом APPROVED, обычная покупка, введение невалидных данных в поле Год с использованием 0")
    @SneakyThrows
    void shouldPayByApprovedCardYearField0() {
        paymentPage.setCardNumber("4444444444444441");
        paymentPage.setCardMonth("10");
        paymentPage.setCardYear("0");
        paymentPage.setCardOwner("Антон Попов");
        paymentPage.setCardCVV("999");
        paymentPage.pushСontinueButton();
        paymentPage.checkMessageWrongFormat();
    }

    @Test
    @DisplayName("Оплата картой со статусом APPROVED, обычная покупка, введение невалидных данных в поле Год с использованием запрещенных символов")
    @SneakyThrows
    void shouldPayByApprovedCardYearFieldSymbol() {
        paymentPage.setCardNumber("4444444444444441");
        paymentPage.setCardMonth("10");
        paymentPage.setCardYear("HjgОр!#%&");
        paymentPage.setCardOwner("Антон Попов");
        paymentPage.setCardCVV("999");
        paymentPage.pushСontinueButton();
        paymentPage.checkMessageWrongFormat();
    }

    @Test // Баг № 3, неверное описание текста ошибки, должно быть "Поле обязательно для заполнения"
    @DisplayName("Оплата картой со статусом APPROVED, обычная покупка, пустые данные в поле Год")
    @SneakyThrows
    void shouldPayByApprovedCardYearEmpty() {
        paymentPage.setCardNumber("4444444444444441");
        paymentPage.setCardMonth("10");
        paymentPage.setCardYear("");
        paymentPage.setCardOwner("Антон Попов");
        paymentPage.setCardCVV("999");
        paymentPage.pushСontinueButton();
        paymentPage.checkMessageRequiredField();
    }

    @Test // Баг № 6, отсутствует появление ошибки "Неверный формат"
    @DisplayName("Оплата картой со статусом APPROVED, обычная покупка, введение невалидных данных в поле Владелец с использованием некорректного значения")
    @SneakyThrows
    void shouldPayByApprovedCardOwnerFieldInCorrect() {
        paymentPage.setCardNumber("4444444444444441");
        paymentPage.setCardMonth("10");
        paymentPage.setCardYear("22");
        paymentPage.setCardOwner("TUhjk");
        paymentPage.setCardCVV("999");
        paymentPage.pushСontinueButton();
        paymentPage.checkMessageWrongFormat();
    }

    @Test // Баг № 7, отсутствует появление ошибки "Неверный формат"
    @DisplayName("Оплата картой со статусом APPROVED, обычная покупка, введение невалидных данных в поле Владелец с использованием запрещенных символов")
    @SneakyThrows
    void shouldPayByApprovedCardOwnerFieldSymbol() {
        paymentPage.setCardNumber("4444444444444441");
        paymentPage.setCardMonth("10");
        paymentPage.setCardYear("22");
        paymentPage.setCardOwner("11;?*!(;%");
        paymentPage.setCardCVV("999");
        paymentPage.pushСontinueButton();
        paymentPage.checkMessageWrongFormat();
    }

    @Test
    @DisplayName("Оплата картой со статусом APPROVED, обычная покупка, пустые данные в поле Владелец")
    @SneakyThrows
    void shouldPayByApprovedCardOwnerEmpty() {
        paymentPage.setCardNumber("4444444444444441");
        paymentPage.setCardMonth("10");
        paymentPage.setCardYear("20");
        paymentPage.setCardOwner("");
        paymentPage.setCardCVV("999");
        paymentPage.pushСontinueButton();
        paymentPage.checkMessageRequiredField();
    }

    @Test
    @DisplayName("Оплата картой со статусом APPROVED, обычная покупка, введение невалидных данных в поле CVC/CVV с использованием 0")
    @SneakyThrows
    void shouldPayByApprovedCardCVCCVVField0() {
        paymentPage.setCardNumber("4444444444444441");
        paymentPage.setCardMonth("10");
        paymentPage.setCardYear("22");
        paymentPage.setCardOwner("Антон Попов");
        paymentPage.setCardCVV("0");
        paymentPage.pushСontinueButton();
        paymentPage.checkMessageWrongFormat();
    }

    @Test
    @DisplayName("Оплата картой со статусом APPROVED, обычная покупка, введение невалидных данных в поле CVC/CVV с использованием запрещенных символов")
    @SneakyThrows
    void shouldPayByApprovedCardCVCCVVFieldSymbol() {
        paymentPage.setCardNumber("4444444444444441");
        paymentPage.setCardMonth("10");
        paymentPage.setCardYear("22");
        paymentPage.setCardOwner("Антон Попов");
        paymentPage.setCardCVV("@$!");
        paymentPage.pushСontinueButton();
        paymentPage.checkMessageWrongFormat();
    }

    @Test
    @DisplayName("Оплата картой со статусом APPROVED, обычная покупка, пустые данные в поле CVC/CVV")
    @SneakyThrows
    void shouldPayByApprovedCardCVCCVVEmpty() {
        paymentPage.setCardNumber("4444444444444441");
        paymentPage.setCardMonth("10");
        paymentPage.setCardYear("22");
        paymentPage.setCardOwner("Антон Попов");
        paymentPage.setCardCVV("");
        paymentPage.pushСontinueButton();
        paymentPage.checkMessageRequiredField();
    }

    @Test
    @DisplayName("Оплата картой со статусом APPROVED, обычная покупка, введение валидных данных с последующей проверкой статуса в СУБД")
    @SneakyThrows
    void shouldPayByApprovedCardStatusInDB() {
        paymentPage.setCardNumber("4444444444444441");
        paymentPage.setCardMonth("10");
        paymentPage.setCardYear("22");
        paymentPage.setCardOwner("Антон Попов");
        paymentPage.setCardCVV("999");
        paymentPage.pushСontinueButton();
        paymentPage.checkMessageSuccess();
        DBUtils.checkPaymentStatus(Status.APPROVED);
    }

    @Test
    @DisplayName("Оплата картой со статусом DECLINED, обычная покупка, введение валидных данныхс последующей проверкой статуса в СУБД")
    @SneakyThrows
    void shouldNoPayByDeclidedCardStatusInDB() {
        paymentPage.setCardNumber("4444444444444442");
        paymentPage.setCardMonth("10");
        paymentPage.setCardYear("22");
        paymentPage.setCardOwner("Антон Попов");
        paymentPage.setCardCVV("999");
        paymentPage.pushСontinueButton();
        paymentPage.checkMessageSuccess();
        DBUtils.checkPaymentStatus(Status.DECLINED);
    }

    @Test  // Баг № 8, стоимость на сайте не совпадает со стоимостью в БД
    @DisplayName("Оплата картой со статусом APPROVED, обычная покупка, введение валидных данных с последующей проверкой суммы в СУБД")
    @SneakyThrows
    void shouldApprovedPayAmount() {
        paymentPage.setCardNumber("4444444444444441");
        paymentPage.setCardMonth("10");
        paymentPage.setCardYear("22");
        paymentPage.setCardOwner("Антон Попов");
        paymentPage.setCardCVV("999");
        paymentPage.pushСontinueButton();
        paymentPage.checkMessageSuccess();
        assertTrue(DBUtils.checkEntityCount() == 5);
        DBUtils.checkPaymentStatus(Status.APPROVED);
        assertEquals(mainPage.checkAmount(), DBUtils.getPayAmount());
    }
}


