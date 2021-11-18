package com.example.stripealone.model;

public class PaymentIntent {
    private String email;
    private String cardNumber;
    private String cardExpMonth;
    private String cardExpYear;
    private String cardCvc;

    public PaymentIntent(String email, String cardNumber, String cardExpMonth, String cardExpYear, String cardCvc) {
        this.email = email;
        this.cardNumber = cardNumber;
        this.cardExpMonth = cardExpMonth;
        this.cardExpYear = cardExpYear;
        this.cardCvc = cardCvc;
    }

    public String getEmail() {
        return email;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public String getCardExpMonth() {
        return cardExpMonth;
    }

    public String getCardExpYear() {
        return cardExpYear;
    }

    public String getCardCvc() {
        return cardCvc;
    }
}
