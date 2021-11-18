package com.example.stripealone;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Card;
import com.stripe.model.Customer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@SpringBootApplication
public class StripeAloneApplication {

    public static void main(String[] args) throws StripeException {
        Stripe.apiKey = "sk_test_51JugOfGmN8gexsbcPAaYFWHgLBltjQj1sZghmH95UjCkplDvvucb21hBqMpVohZtiOAKQchLn0ZQUgZY35tWnnnu00aeiV0ER9";
        SpringApplication.run(StripeAloneApplication.class, args);

    }

}


