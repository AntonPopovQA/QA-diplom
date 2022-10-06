package ru.netology.page;

import lombok.val;

import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$$;
import static com.codeborne.selenide.Selenide.$x;

public class MainPage {

    public PaymentPage buttonBuyClick() {
        $$(".button__content").find(exactText("Купить")).click();
        $$(".heading_theme_alfa-on-white").find(exactText("Оплата по карте")).shouldBe(visible);
        return new PaymentPage();
    }

    public CreditPage buttonCreditClick() {
        $$(".button__content").find(exactText("Купить в кредит")).click();
        $$(".heading_theme_alfa-on-white").find(exactText("Кредит по данным карты")).shouldBe(visible);
        return new CreditPage();
    }

    public int checkAmount() {
        String amountStart = "Всего ";
        String amountFinish = " руб.!";
        String text = $x("//*[contains(text(),'руб')]").getText();
        val start = text.indexOf(amountStart);
        val finish = text.indexOf(amountFinish);
        val value = text.substring(start + amountStart.length(), finish).replaceAll(" ", "");
        return Integer.parseInt(value);
    }
}
