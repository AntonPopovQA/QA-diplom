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

public class FromCreditPayment {
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
    @DisplayName("Оплата картой со статусом APPROVED, покупка в кредит, введение валидных данных")
    @SneakyThrows
    void shouldPayByApprovedCardInCredit() {
        formPage.buyOnCredit();
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
    void shouldPayByApprovedCardInCreditMonth() {
        formPage.buyOnCredit();
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
    void shouldPayByApprovedCardInCreditYear() {
        formPage.buyOnCredit();
        formPage.setCardNumber("4444444444444441");
        formPage.setCardMonth("10");
        formPage.setCardYear("23");
        formPage.setCardOwner("Антон Попов");
        formPage.setCardCVV("999");
        formPage.pushСontinueButton();
        formPage.checkMessageSuccess();
    }

    @Test   // Баг № 9, оплата проходит
    @DisplayName("Оплата картой со статусом DECLINED, покупка в кредит, введение валидных данных")
    @SneakyThrows
    void shouldNoPayByDeclinedCardInCredit() {
        formPage.buyOnCredit();
        formPage.setCardNumber("4444444444444442");
        formPage.setCardMonth("10");
        formPage.setCardYear("22");
        formPage.setCardOwner("Антон Попов");
        formPage.setCardCVV("999");
        formPage.pushСontinueButton();
        formPage.checkMessageError();
    }

    // Негативные тесты
    @Test   // Баг № 10, появляется два уведомления: "Ошибка", а затем "Успешно"
    @DisplayName("Оплата неизвестной картой, покупка в кредит, введение валидных данных, за исключением номера карты")
    @SneakyThrows
    void shouldNoPayByUnknownCardInCredit() {
        formPage.buyOnCredit();
        formPage.setCardNumber("4444444444444443");
        formPage.setCardMonth("10");
        formPage.setCardYear("22");
        formPage.setCardOwner("Антон Попов");
        formPage.setCardCVV("999");
        formPage.pushСontinueButton();
        formPage.checkMessageError();
    }

    @Test // Баг № 11, неверное описание текста ошибки в некоторых полях, должно быть "Поле обязательно для заполнения"
    @DisplayName("Оплата картой со статусом APPROVED, покупка в кредит, пустые данные во всех полях")
    @SneakyThrows
    void shouldPayByApprovedCardInCreditEmpty() {
        formPage.buyOnCredit();
        formPage.setCardNumber("");
        formPage.setCardMonth("");
        formPage.setCardYear("");
        formPage.setCardOwner("");
        formPage.setCardCVV("");
        formPage.pushСontinueButton();
        formPage.checkMessageRequiredField();
    }

    @Test
    @DisplayName("Оплата картой со статусом APPROVED, покупка в кредит, введение невалидных данных в поле Номер карты с использованием цифр длиной от 1 до 15")
    @SneakyThrows
    void shouldPayByApprovedCardInCreditNumberCardFieldNumber() {
        formPage.buyOnCredit();
        formPage.setCardNumber("1111 2323");
        formPage.setCardMonth("10");
        formPage.setCardYear("22");
        formPage.setCardOwner("Антон Попов");
        formPage.setCardCVV("999");
        formPage.pushСontinueButton();
        formPage.checkMessageWrongFormat();
    }

    @Test
    @DisplayName("Оплата картой со статусом APPROVED, покупка в кредит, введение невалидных данных в поле Номер карты с использованием запрещенных символов")
    @SneakyThrows
    void shouldPayByApprovedCardInCreditNumberCardFieldSymbol() {
        formPage.buyOnCredit();
        formPage.setCardNumber("HjgОр!#%&");
        formPage.setCardMonth("10");
        formPage.setCardYear("22");
        formPage.setCardOwner("Антон Попов");
        formPage.setCardCVV("999");
        formPage.pushСontinueButton();
        formPage.checkMessageWrongFormat();
    }

    @Test // Баг № 3, неверное описание текста ошибки, должно быть "Поле обязательно для заполнения"
    @DisplayName("Оплата картой со статусом APPROVED, покупка в кредит, пустые данные в поле Номер карты")
    @SneakyThrows
    void shouldPayByApprovedCardInCreditNumberCardEmpty() {
        formPage.buyOnCredit();
        formPage.setCardNumber("");
        formPage.setCardMonth("10");
        formPage.setCardYear("22");
        formPage.setCardOwner("Антон Попов");
        formPage.setCardCVV("999");
        formPage.pushСontinueButton();
        formPage.checkMessageRequiredField();
    }

    @Test // Баг № 12, неверное описание текста ошибки, должно быть "Истек срок действия карты"
    @DisplayName("Оплата картой со статусом APPROVED, покупка в кредит, введение невалидных данных в поле Месяц с введением предыдущего месяца")
    @SneakyThrows
    void shouldPayByApprovedCardInCreditMonthFieldInCorrectMonth() {
        formPage.buyOnCredit();
        formPage.setCardNumber("4444444444444441");
        formPage.setCardMonth("08");
        formPage.setCardYear("22");
        formPage.setCardOwner("Антон Попов");
        formPage.setCardCVV("999");
        formPage.pushСontinueButton();
        formPage.checkMessageOverDate();
    }

    @Test // Баг № 13, неверное описание текста ошибки, должно быть "Неверный формат"
    @DisplayName("Оплата картой со статусом APPROVED, покупка в кредит, введение невалидных данных в поле Месяц с использованием некорректного значения")
    @SneakyThrows
    void shouldPayByApprovedCardInCreditMonthFieldInCorrect() {
        formPage.buyOnCredit();
        formPage.setCardNumber("4444444444444441");
        formPage.setCardMonth("44");
        formPage.setCardYear("22");
        formPage.setCardOwner("Антон Попов");
        formPage.setCardCVV("999");
        formPage.pushСontinueButton();
        formPage.checkMessageWrongFormat();
    }

    @Test
    @DisplayName("Оплата картой со статусом APPROVED, покупка в кредит, введение невалидных данных в поле Месяц с использованием 0")
    @SneakyThrows
    void shouldPayByApprovedCardInCreditMonthField0() {
        formPage.buyOnCredit();
        formPage.setCardNumber("4444444444444441");
        formPage.setCardMonth("0");
        formPage.setCardYear("22");
        formPage.setCardOwner("Антон Попов");
        formPage.setCardCVV("999");
        formPage.pushСontinueButton();
        formPage.checkMessageWrongFormat();
    }

    @Test
    @DisplayName("Оплата картой со статусом APPROVED, покупка в кредит, введение невалидных данных в поле Месяц с использованием запрещенных символов")
    @SneakyThrows
    void shouldPayByApprovedCardInCreditMonthFieldSymbol() {
        formPage.buyOnCredit();
        formPage.setCardNumber("4444444444444441");
        formPage.setCardMonth("HjgОр!#%&");
        formPage.setCardYear("22");
        formPage.setCardOwner("Антон Попов");
        formPage.setCardCVV("999");
        formPage.pushСontinueButton();
        formPage.checkMessageWrongFormat();
    }

    @Test // Баг № 3, неверное описание текста ошибки, должно быть "Поле обязательно для заполнения"
    @DisplayName("Оплата картой со статусом APPROVED, покупка в кредит, пустые данные в поле Месяц")
    @SneakyThrows
    void shouldPayByApprovedCardInCreditMonthEmpty() {
        formPage.buyOnCredit();
        formPage.setCardNumber("4444444444444441");
        formPage.setCardMonth("");
        formPage.setCardYear("22");
        formPage.setCardOwner("Антон Попов");
        formPage.setCardCVV("999");
        formPage.pushСontinueButton();
        formPage.checkMessageRequiredField();
    }

    @Test
    @DisplayName("Оплата картой со статусом APPROVED, покупка в кредит, введение невалидных данных в поле Год с использованием предыдущего года")
    @SneakyThrows
    void shouldPayByApprovedCardInCreditYearFieldInCorrectYear() {
        formPage.buyOnCredit();
        formPage.setCardNumber("4444444444444441");
        formPage.setCardMonth("10");
        formPage.setCardYear("21");
        formPage.setCardOwner("Антон Попов");
        formPage.setCardCVV("999");
        formPage.pushСontinueButton();
        formPage.checkMessageOverDate();
    }

    @Test
    @DisplayName("Оплата картой со статусом APPROVED, покупка в кредит, введение невалидных данных в поле Год с использованием некорректного значения")
    @SneakyThrows
    void shouldPayByApprovedCardInCreditYearFieldInCorrect() {
        formPage.buyOnCredit();
        formPage.setCardNumber("4444444444444441");
        formPage.setCardMonth("10");
        formPage.setCardYear("44");
        formPage.setCardOwner("Антон Попов");
        formPage.setCardCVV("999");
        formPage.pushСontinueButton();
        formPage.checkMessageWrongDate();
    }

    @Test
    @DisplayName("Оплата картой со статусом APPROVED, покупка в кредит, введение невалидных данных в поле Год с использованием 0")
    @SneakyThrows
    void shouldPayByApprovedCardInCreditYearField0() {
        formPage.buyOnCredit();
        formPage.setCardNumber("4444444444444441");
        formPage.setCardMonth("10");
        formPage.setCardYear("0");
        formPage.setCardOwner("Антон Попов");
        formPage.setCardCVV("999");
        formPage.pushСontinueButton();
        formPage.checkMessageWrongFormat();
    }

    @Test
    @DisplayName("Оплата картой со статусом APPROVED, покупка в кредит, введение невалидных данных в поле Год с использованием запрещенных символов")
    @SneakyThrows
    void shouldPayByApprovedCardInCreditYearFieldSymbol() {
        formPage.buyOnCredit();
        formPage.setCardNumber("4444444444444441");
        formPage.setCardMonth("10");
        formPage.setCardYear("HjgОр!#%&");
        formPage.setCardOwner("Антон Попов");
        formPage.setCardCVV("999");
        formPage.pushСontinueButton();
        formPage.checkMessageWrongFormat();
    }

    @Test // Баг № 3, неверное описание текста ошибки, должно быть "Поле обязательно для заполнения"
    @DisplayName("Оплата картой со статусом APPROVED, покупка в кредит, пустые данные в поле Год")
    @SneakyThrows
    void shouldPayByApprovedCardInCreditYearEmpty() {
        formPage.buyOnCredit();
        formPage.setCardNumber("4444444444444441");
        formPage.setCardMonth("10");
        formPage.setCardYear("");
        formPage.setCardOwner("Антон Попов");
        formPage.setCardCVV("999");
        formPage.pushСontinueButton();
        formPage.checkMessageRequiredField();
    }

    @Test // Баг № 14, отсутствует появление ошибки "Неверный формат"
    @DisplayName("Оплата картой со статусом APPROVED, покупка в кредит, введение невалидных данных в поле Владелец с использованием некорректного значения")
    @SneakyThrows
    void shouldPayByApprovedCardInCreditOwnerFieldInCorrect() {
        formPage.buyOnCredit();
        formPage.setCardNumber("4444444444444441");
        formPage.setCardMonth("10");
        formPage.setCardYear("22");
        formPage.setCardOwner("TUhjk");
        formPage.setCardCVV("999");
        formPage.pushСontinueButton();
        formPage.checkMessageWrongFormat();
    }

    @Test // Баг № 15, отсутствует появление ошибки "Неверный формат"
    @DisplayName("Оплата картой со статусом APPROVED, покупка в кредит, введение невалидных данных в поле Владелец с использованием запрещенных символов")
    @SneakyThrows
    void shouldPayByApprovedCardInCreditOwnerFieldSymbol() {
        formPage.buyOnCredit();
        formPage.setCardNumber("4444444444444441");
        formPage.setCardMonth("10");
        formPage.setCardYear("22");
        formPage.setCardOwner("11;?*!(;%");
        formPage.setCardCVV("999");
        formPage.pushСontinueButton();
        formPage.checkMessageWrongFormat();
    }

    @Test
    @DisplayName("Оплата картой со статусом APPROVED, покупка в кредит, пустые данные в поле Владелец")
    @SneakyThrows
    void shouldPayByApprovedCardInCreditOwnerEmpty() {
        formPage.buyOnCredit();
        formPage.setCardNumber("4444444444444441");
        formPage.setCardMonth("10");
        formPage.setCardYear("20");
        formPage.setCardOwner("");
        formPage.setCardCVV("999");
        formPage.pushСontinueButton();
        formPage.checkMessageRequiredField();
    }

    @Test
    @DisplayName("Оплата картой со статусом APPROVED, покупка в кредит, введение невалидных данных в поле CVC/CVV с использованием 0")
    @SneakyThrows
    void shouldPayByApprovedCardInCreditCVCCVVField0() {
        formPage.buyOnCredit();
        formPage.setCardNumber("4444444444444441");
        formPage.setCardMonth("10");
        formPage.setCardYear("22");
        formPage.setCardOwner("Антон Попов");
        formPage.setCardCVV("0");
        formPage.pushСontinueButton();
        formPage.checkMessageWrongFormat();
    }

    @Test
    @DisplayName("Оплата картой со статусом APPROVED, покупка в кредит, введение невалидных данных в поле CVC/CVV с использованием запрещенных символов")
    @SneakyThrows
    void shouldPayByApprovedCardInCreditCVCCVVFieldSymbol() {
        formPage.buyOnCredit();
        formPage.setCardNumber("4444444444444441");
        formPage.setCardMonth("10");
        formPage.setCardYear("22");
        formPage.setCardOwner("Антон Попов");
        formPage.setCardCVV("@$!");
        formPage.pushСontinueButton();
        formPage.checkMessageWrongFormat();
    }

    @Test
    @DisplayName("Оплата картой со статусом APPROVED, покупка в кредит, пустые данные в поле CVC/CVV")
    @SneakyThrows
    void shouldPayByApprovedCardInCreditCVCCVVEmpty() {
        formPage.buyOnCredit();
        formPage.setCardNumber("4444444444444441");
        formPage.setCardMonth("10");
        formPage.setCardYear("22");
        formPage.setCardOwner("Антон Попов");
        formPage.setCardCVV("");
        formPage.pushСontinueButton();
        formPage.checkMessageRequiredField();
    }

    @Test
    @DisplayName("Оплата картой со статусом APPROVED, покупка в кредит, введение валидных данных с последующей проверкой данных в СУБД")
    @SneakyThrows
    void shouldPayByApprovedCardInCreditStatusInDB() {
        formPage.buyOnCredit();
        formPage.setCardNumber("4444444444444441");
        formPage.setCardMonth("10");
        formPage.setCardYear("22");
        formPage.setCardOwner("Антон Попов");
        formPage.setCardCVV("999");
        formPage.pushСontinueButton();
        formPage.checkMessageSuccess();
        DBUtils.checkCreditStatus(Status.APPROVED);
    }

    @Test
    @DisplayName("Оплата картой со статусом DECLINED, покупка в кредит, введение валидных данныхс последующей проверкой данных в СУБД")
    @SneakyThrows
    void shouldNoPayByDeclidedCardInCreditStatusInDB() {
        formPage.buyOnCredit();
        formPage.setCardNumber("4444444444444442");
        formPage.setCardMonth("10");
        formPage.setCardYear("22");
        formPage.setCardOwner("Антон Попов");
        formPage.setCardCVV("999");
        formPage.pushСontinueButton();
        formPage.checkMessageSuccess();
        DBUtils.checkCreditStatus(Status.DECLINED);
    }

    @Test
    @DisplayName("Оплата картой со статусом APPROVED, покупка в кредит, введение валидных данных с последующей проверкой предоставленной суммы в СУБД")
    @SneakyThrows
    void shouldApprovedPayAmount() {
        formPage.buyOnCredit();
        formPage.setCardNumber("4444444444444441");
        formPage.setCardMonth("10");
        formPage.setCardYear("22");
        formPage.setCardOwner("Антон Попов");
        formPage.setCardCVV("999");
        formPage.pushСontinueButton();
        formPage.checkMessageSuccess();
        assertTrue(DBUtils.checkEntityCount() == 3);
        assertEquals(formPage.checkAmount(), DBUtils.getPayAmount());
    }
}
