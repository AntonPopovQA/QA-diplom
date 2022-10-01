package tests;

import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;
import lombok.SneakyThrows;
import org.junit.jupiter.api.*;
import ru.netology.data.DBUtils;
import ru.netology.data.Status;
import ru.netology.page.FormPage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class FormPayment {

    private FormPage formPage;

    @BeforeEach
    void setUpPage() {
        formPage = new FormPage();
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
        formPage.buyForYourMoney();
        formPage.setCardNumber("4444444444444441");
        formPage.setCardMonth("10");
        formPage.setCardYear("22");
        formPage.setCardOwner("Антон Попов");
        formPage.setCardCVV("999");
        formPage.pushСontinueButton();
        formPage.checkMessageSuccess();
    }

    @Test
    @DisplayName("Оплата картой со статусом APPROVED, граничные значения в поле месяц, введение валидных данных")
    @SneakyThrows
    void shouldPayByApprovedCardMonth() {
        formPage.buyForYourMoney();
        formPage.setCardNumber("4444444444444441");
        formPage.setCardMonth("11");
        formPage.setCardYear("22");
        formPage.setCardOwner("Антон Попов");
        formPage.setCardCVV("999");
        formPage.pushСontinueButton();
        formPage.checkMessageSuccess();
    }

    @Test
    @DisplayName("Оплата картой со статусом APPROVED, граничные значения в поле год, введение валидных данных")
    @SneakyThrows
    void shouldPayByApprovedCardYear() {
        formPage.buyForYourMoney();
        formPage.setCardNumber("4444444444444441");
        formPage.setCardMonth("10");
        formPage.setCardYear("23");
        formPage.setCardOwner("Антон Попов");
        formPage.setCardCVV("999");
        formPage.pushСontinueButton();
        formPage.checkMessageSuccess();
    }

    @Test   // Баг № 1, оплата проходит
    @DisplayName("Оплата картой со статусом DECLINED, обычная оплата, введение валидных данных")
    @SneakyThrows
    void shouldNoPayByDeclinedCard() {
        formPage.buyForYourMoney();
        formPage.setCardNumber("4444444444444442");
        formPage.setCardMonth("10");
        formPage.setCardYear("22");
        formPage.setCardOwner("Антон Попов");
        formPage.setCardCVV("999");
        formPage.pushСontinueButton();
        formPage.checkMessageError();
    }

    // Негативные тесты
    @Test   // Баг № 2, появляется два уведомления: "Ошибка", а затем "Успешно"
    @DisplayName("Оплата неизвестной картой, обычная оплата, введение валидных данных, за исключением номера карты")
    @SneakyThrows
    void shouldNoPayByUnknownCard() {
        formPage.buyForYourMoney();
        formPage.setCardNumber("4444444444444443");
        formPage.setCardMonth("10");
        formPage.setCardYear("22");
        formPage.setCardOwner("Антон Попов");
        formPage.setCardCVV("999");
        formPage.pushСontinueButton();
        formPage.checkMessageError();
    }

    @Test // Баг № 3, неверное описание текста ошибки в некоторых полях, должно быть "Поле обязательно для заполнения"
    @DisplayName("Оплата картой со статусом APPROVED, обычная покупка, пустые данные во всех полях")
    @SneakyThrows
    void shouldPayByApprovedCardEmpty() {
        formPage.buyForYourMoney();
        formPage.setCardNumber("");
        formPage.setCardMonth("");
        formPage.setCardYear("");
        formPage.setCardOwner("");
        formPage.setCardCVV("");
        formPage.pushСontinueButton();
        formPage.checkMessageRequiredField();
    }

    @Test
    @DisplayName("Оплата картой со статусом APPROVED, обычная покупка, введение невалидных данных в поле Номер карты с использованием цифр длиной от 1 до 15")
    @SneakyThrows
    void shouldPayByApprovedCardNumberCardFieldNumber() {
        formPage.buyForYourMoney();
        formPage.setCardNumber("1111 2323");
        formPage.setCardMonth("10");
        formPage.setCardYear("22");
        formPage.setCardOwner("Антон Попов");
        formPage.setCardCVV("999");
        formPage.pushСontinueButton();
        formPage.checkMessageWrongFormat();
    }

    @Test
    @DisplayName("Оплата картой со статусом APPROVED, обычная покупка, введение невалидных данных в поле Номер карты с использованием запрещенных символов")
    @SneakyThrows
    void shouldPayByApprovedCardNumberCardFieldSymbol() {
        formPage.buyForYourMoney();
        formPage.setCardNumber("HjgОр!#%&");
        formPage.setCardMonth("10");
        formPage.setCardYear("22");
        formPage.setCardOwner("Антон Попов");
        formPage.setCardCVV("999");
        formPage.pushСontinueButton();
        formPage.checkMessageWrongFormat();
    }

    @Test // Баг № 3, неверное описание текста ошибки, должно быть "Поле обязательно для заполнения"
    @DisplayName("Оплата картой со статусом APPROVED, обычная покупка, пустые данные в поле Номер карты")
    @SneakyThrows
    void shouldPayByApprovedCardNumberCardEmpty() {
        formPage.buyForYourMoney();
        formPage.setCardNumber("");
        formPage.setCardMonth("10");
        formPage.setCardYear("22");
        formPage.setCardOwner("Антон Попов");
        formPage.setCardCVV("999");
        formPage.pushСontinueButton();
        formPage.checkMessageRequiredField();
    }

    @Test // Баг № 4, неверное описание текста ошибки, должно быть "Истек срок действия карты"
    @DisplayName("Оплата картой со статусом APPROVED, обычная покупка, введение невалидных данных в поле Месяц с введением предыдущего месяца")
    @SneakyThrows
    void shouldPayByApprovedCardMonthFieldInCorrectMonth() {
        formPage.buyForYourMoney();
        formPage.setCardNumber("4444444444444441");
        formPage.setCardMonth("08");
        formPage.setCardYear("22");
        formPage.setCardOwner("Антон Попов");
        formPage.setCardCVV("999");
        formPage.pushСontinueButton();
        formPage.checkMessageOverDate();
    }

    @Test // Баг № 5, неверное описание текста ошибки, должно быть "Неверный формат"
    @DisplayName("Оплата картой со статусом APPROVED, обычная покупка, введение невалидных данных в поле Месяц с использованием некорректного значения")
    @SneakyThrows
    void shouldPayByApprovedCardMonthFieldInCorrect() {
        formPage.buyForYourMoney();
        formPage.setCardNumber("4444444444444441");
        formPage.setCardMonth("44");
        formPage.setCardYear("22");
        formPage.setCardOwner("Антон Попов");
        formPage.setCardCVV("999");
        formPage.pushСontinueButton();
        formPage.checkMessageWrongFormat();
    }

    @Test
    @DisplayName("Оплата картой со статусом APPROVED, обычная покупка, введение невалидных данных в поле Месяц с использованием 0")
    @SneakyThrows
    void shouldPayByApprovedCardMonthField0() {
        formPage.buyForYourMoney();
        formPage.setCardNumber("4444444444444441");
        formPage.setCardMonth("0");
        formPage.setCardYear("22");
        formPage.setCardOwner("Антон Попов");
        formPage.setCardCVV("999");
        formPage.pushСontinueButton();
        formPage.checkMessageWrongFormat();
    }

    @Test
    @DisplayName("Оплата картой со статусом APPROVED, обычная покупка, введение невалидных данных в поле Месяц с использованием запрещенных символов")
    @SneakyThrows
    void shouldPayByApprovedCardMonthFieldSymbol() {
        formPage.buyForYourMoney();
        formPage.setCardNumber("4444444444444441");
        formPage.setCardMonth("HjgОр!#%&");
        formPage.setCardYear("22");
        formPage.setCardOwner("Антон Попов");
        formPage.setCardCVV("999");
        formPage.pushСontinueButton();
        formPage.checkMessageWrongFormat();
    }

    @Test // Баг № 3, неверное описание текста ошибки, должно быть "Поле обязательно для заполнения"
    @DisplayName("Оплата картой со статусом APPROVED, обычная покупка, пустые данные в поле Месяц")
    @SneakyThrows
    void shouldPayByApprovedCardMonthEmpty() {
        formPage.buyForYourMoney();
        formPage.setCardNumber("4444444444444441");
        formPage.setCardMonth("");
        formPage.setCardYear("22");
        formPage.setCardOwner("Антон Попов");
        formPage.setCardCVV("999");
        formPage.pushСontinueButton();
        formPage.checkMessageRequiredField();
    }

    @Test
    @DisplayName("Оплата картой со статусом APPROVED, обычная покупка, введение невалидных данных в поле Год с использованием предыдущего года")
    @SneakyThrows
    void shouldPayByApprovedCardYearFieldInCorrectYear() {
        formPage.buyForYourMoney();
        formPage.setCardNumber("4444444444444441");
        formPage.setCardMonth("10");
        formPage.setCardYear("21");
        formPage.setCardOwner("Антон Попов");
        formPage.setCardCVV("999");
        formPage.pushСontinueButton();
        formPage.checkMessageOverDate();
    }

    @Test
    @DisplayName("Оплата картой со статусом APPROVED, обычная покупка, введение невалидных данных в поле Год с использованием некорректного значения")
    @SneakyThrows
    void shouldPayByApprovedCardYearFieldInCorrect() {
        formPage.buyForYourMoney();
        formPage.setCardNumber("4444444444444441");
        formPage.setCardMonth("10");
        formPage.setCardYear("44");
        formPage.setCardOwner("Антон Попов");
        formPage.setCardCVV("999");
        formPage.pushСontinueButton();
        formPage.checkMessageWrongDate();
    }

    @Test
    @DisplayName("Оплата картой со статусом APPROVED, обычная покупка, введение невалидных данных в поле Год с использованием 0")
    @SneakyThrows
    void shouldPayByApprovedCardYearField0() {
        formPage.buyForYourMoney();
        formPage.setCardNumber("4444444444444441");
        formPage.setCardMonth("10");
        formPage.setCardYear("0");
        formPage.setCardOwner("Антон Попов");
        formPage.setCardCVV("999");
        formPage.pushСontinueButton();
        formPage.checkMessageWrongFormat();
    }

    @Test
    @DisplayName("Оплата картой со статусом APPROVED, обычная покупка, введение невалидных данных в поле Год с использованием запрещенных символов")
    @SneakyThrows
    void shouldPayByApprovedCardYearFieldSymbol() {
        formPage.buyForYourMoney();
        formPage.setCardNumber("4444444444444441");
        formPage.setCardMonth("10");
        formPage.setCardYear("HjgОр!#%&");
        formPage.setCardOwner("Антон Попов");
        formPage.setCardCVV("999");
        formPage.pushСontinueButton();
        formPage.checkMessageWrongFormat();
    }

    @Test // Баг № 3, неверное описание текста ошибки, должно быть "Поле обязательно для заполнения"
    @DisplayName("Оплата картой со статусом APPROVED, обычная покупка, пустые данные в поле Год")
    @SneakyThrows
    void shouldPayByApprovedCardYearEmpty() {
        formPage.buyForYourMoney();
        formPage.setCardNumber("4444444444444441");
        formPage.setCardMonth("10");
        formPage.setCardYear("");
        formPage.setCardOwner("Антон Попов");
        formPage.setCardCVV("999");
        formPage.pushСontinueButton();
        formPage.checkMessageRequiredField();
    }

    @Test // Баг № 6, отсутствует появление ошибки "Неверный формат"
    @DisplayName("Оплата картой со статусом APPROVED, обычная покупка, введение невалидных данных в поле Владелец с использованием некорректного значения")
    @SneakyThrows
    void shouldPayByApprovedCardOwnerFieldInCorrect() {
        formPage.buyForYourMoney();
        formPage.setCardNumber("4444444444444441");
        formPage.setCardMonth("10");
        formPage.setCardYear("22");
        formPage.setCardOwner("TUhjk");
        formPage.setCardCVV("999");
        formPage.pushСontinueButton();
        formPage.checkMessageWrongFormat();
    }

    @Test // Баг № 7, отсутствует появление ошибки "Неверный формат"
    @DisplayName("Оплата картой со статусом APPROVED, обычная покупка, введение невалидных данных в поле Владелец с использованием запрещенных символов")
    @SneakyThrows
    void shouldPayByApprovedCardOwnerFieldSymbol() {
        formPage.buyForYourMoney();
        formPage.setCardNumber("4444444444444441");
        formPage.setCardMonth("10");
        formPage.setCardYear("22");
        formPage.setCardOwner("11;?*!(;%");
        formPage.setCardCVV("999");
        formPage.pushСontinueButton();
        formPage.checkMessageWrongFormat();
    }

    @Test
    @DisplayName("Оплата картой со статусом APPROVED, обычная покупка, пустые данные в поле Владелец")
    @SneakyThrows
    void shouldPayByApprovedCardOwnerEmpty() {
        formPage.buyForYourMoney();
        formPage.setCardNumber("4444444444444441");
        formPage.setCardMonth("10");
        formPage.setCardYear("20");
        formPage.setCardOwner("");
        formPage.setCardCVV("999");
        formPage.pushСontinueButton();
        formPage.checkMessageRequiredField();
    }

    @Test
    @DisplayName("Оплата картой со статусом APPROVED, обычная покупка, введение невалидных данных в поле CVC/CVV с использованием 0")
    @SneakyThrows
    void shouldPayByApprovedCardCVCCVVField0() {
        formPage.buyForYourMoney();
        formPage.setCardNumber("4444444444444441");
        formPage.setCardMonth("10");
        formPage.setCardYear("22");
        formPage.setCardOwner("Антон Попов");
        formPage.setCardCVV("0");
        formPage.pushСontinueButton();
        formPage.checkMessageWrongFormat();
    }

    @Test
    @DisplayName("Оплата картой со статусом APPROVED, обычная покупка, введение невалидных данных в поле CVC/CVV с использованием запрещенных символов")
    @SneakyThrows
    void shouldPayByApprovedCardCVCCVVFieldSymbol() {
        formPage.buyForYourMoney();
        formPage.setCardNumber("4444444444444441");
        formPage.setCardMonth("10");
        formPage.setCardYear("22");
        formPage.setCardOwner("Антон Попов");
        formPage.setCardCVV("@$!");
        formPage.pushСontinueButton();
        formPage.checkMessageWrongFormat();
    }

    @Test
    @DisplayName("Оплата картой со статусом APPROVED, обычная покупка, пустые данные в поле CVC/CVV")
    @SneakyThrows
    void shouldPayByApprovedCardCVCCVVEmpty() {
        formPage.buyForYourMoney();
        formPage.setCardNumber("4444444444444441");
        formPage.setCardMonth("10");
        formPage.setCardYear("22");
        formPage.setCardOwner("Антон Попов");
        formPage.setCardCVV("");
        formPage.pushСontinueButton();
        formPage.checkMessageRequiredField();
    }

    @Test
    @DisplayName("Оплата картой со статусом APPROVED, обычная покупка, введение валидных данных с последующей проверкой статуса в СУБД")
    @SneakyThrows
    void shouldPayByApprovedCardStatusInDB() {
        formPage.buyForYourMoney();
        formPage.setCardNumber("4444444444444441");
        formPage.setCardMonth("10");
        formPage.setCardYear("22");
        formPage.setCardOwner("Антон Попов");
        formPage.setCardCVV("999");
        formPage.pushСontinueButton();
        formPage.checkMessageSuccess();
        DBUtils.checkPaymentStatus(Status.APPROVED);
    }

    @Test
    @DisplayName("Оплата картой со статусом DECLINED, обычная покупка, введение валидных данныхс последующей проверкой статуса в СУБД")
    @SneakyThrows
    void shouldNoPayByDeclidedCardStatusInDB() {
        formPage.buyForYourMoney();
        formPage.setCardNumber("4444444444444442");
        formPage.setCardMonth("10");
        formPage.setCardYear("22");
        formPage.setCardOwner("Антон Попов");
        formPage.setCardCVV("999");
        formPage.pushСontinueButton();
        formPage.checkMessageSuccess();
        DBUtils.checkPaymentStatus(Status.DECLINED);
    }

    @Test  // Баг № 8, стоимость на сайте не совпадает со стоимостью в БД
    @DisplayName("Оплата картой со статусом APPROVED, обычная покупка, введение валидных данных с последующей проверкой суммы в СУБД")
    @SneakyThrows
    void shouldApprovedPayAmount() {
        formPage.buyForYourMoney();
        formPage.setCardNumber("4444444444444441");
        formPage.setCardMonth("10");
        formPage.setCardYear("22");
        formPage.setCardOwner("Антон Попов");
        formPage.setCardCVV("999");
        formPage.pushСontinueButton();
        formPage.checkMessageSuccess();
        assertTrue(DBUtils.checkEntityCount() == 5);
        DBUtils.checkPaymentStatus(Status.APPROVED);
        assertEquals(formPage.checkAmount(), DBUtils.getPayAmount());
    }
}


