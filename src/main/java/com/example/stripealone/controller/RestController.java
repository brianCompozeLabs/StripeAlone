package com.example.stripealone.controller;


import com.example.stripealone.model.PaymentIntent;
import com.google.gson.Gson;
import com.stripe.exception.StripeException;
import com.stripe.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@org.springframework.web.bind.annotation.RestController
public class RestController {

    @CrossOrigin(origins = "*")
    @PostMapping(path="/coupon",consumes="application/json")
    public String getCouponByPayment(@RequestBody PaymentIntent payment) throws StripeException {

        Map<String, Object> options = new HashMap<>();
        options.put("email", payment.getEmail());
        List<Customer> customers = Customer.list(options).getData();

        Customer customer = defineCustomer(customers, payment.getEmail());
//create card
        Card card = defineCard(payment, customer);
//create card

        //charge
        Map<String, Object> chargeParams = new HashMap<String, Object>();
        chargeParams.put("amount","5000");
        chargeParams.put("currency","usd");
        chargeParams.put("customer",customer.getId());

        Charge.create(chargeParams);
        //charge

        Map<String, Object> params = new HashMap<>();
        params.put("percent_off", 100);
        params.put("duration", "repeating");
        params.put("duration_in_months", 3);

        Coupon coupon = Coupon.create(params);

        //email sender
        EmailConfiguration emailConfiguration = new EmailConfiguration();
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setPort(emailConfiguration.getPort());
        mailSender.setHost(emailConfiguration.getHost());
        mailSender.setUsername(emailConfiguration.getUsername());
        mailSender.setPassword(emailConfiguration.getPassword());

        //mail message
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom(mailSender.getUsername());
        mailMessage.setTo(payment.getEmail());
        mailMessage.setSubject("Your new Coupon");
        mailMessage.setText("Your coupon: " + coupon.getId());

        mailSender.send(mailMessage);
       //

        return coupon.getId();

    }

    private Card defineCard(PaymentIntent payment, Customer customer) throws StripeException {
        Gson gson = new Gson();

        Map<String, Object> cardParam = new HashMap<String, Object>();
        cardParam.put("number", payment.getCardNumber());
        cardParam.put("exp_month", payment.getCardExpMonth());
        cardParam.put("exp_year", payment.getCardExpYear());
        cardParam.put("cvc", payment.getCardCvc());

        Map<String, Object> tokenParam = new HashMap<String, Object>();
        tokenParam.put("card", cardParam);

        Token token = Token.create(tokenParam);

        Map<String, Object> source = new HashMap<String, Object>();
        source.put("source", token.getId());

        Map<String, Object> params = new HashMap<>();
        params.put("object", "card");
        params.put("limit", 5);

        PaymentSourceCollection cards =
                customer.getSources().list(params);

        Boolean cardExist = false;
        String existingCardId = "";
        for(int i = 0; i < cards.getData().size(); i++) {
            String jsonCard = gson.toJson(cards.getData().get(i));
            Card c = gson.fromJson(jsonCard, Card.class);
            if (c.getFingerprint().equals(token.getCard().getFingerprint())) {
                cardExist = true;
                existingCardId = c.getId();
            }
        }

        Card card;
        if(cardExist){
            card = (Card) customer.getSources().retrieve(existingCardId);
        }else{
            card = (Card) customer.getSources().create(source);
        }



        return card;
    }

    private Customer defineCustomer(List<Customer> customers, String email) throws StripeException {
        Customer customer;

        if (customers.size() > 0) {
            Map<String, Object> retrieveParams = new HashMap<String, Object>();
            List<String> expandList = new ArrayList<String>();
            expandList.add("sources");
            retrieveParams.put("expand", expandList);
            customer = Customer.retrieve(customers.get(0).getId(), retrieveParams, null);

        }else{
            Map<String, Object> customerParameter = new HashMap<String, Object>();
            customerParameter.put("email", email);
            customer = Customer.create(customerParameter);
        }

        return customer;
    }
}
