package com.example.smartorders;


import com.stripe.Stripe;
import com.stripe.exception.CardException;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.model.PaymentIntent;
import com.stripe.model.PaymentMethod;
import com.stripe.model.PaymentMethodCollection;
import com.stripe.param.CustomerCreateParams;
import com.stripe.param.PaymentIntentCreateParams;
import com.stripe.param.PaymentMethodListParams;

public class Server {

    public Server() throws StripeException {
        // Set your secret key. Remember to switch to your live secret key in production!
// See your keys here: https://dashboard.stripe.com/account/apikeys
        Stripe.apiKey = "sk_test_50wnmiroW2ia9FhnXBPj2slO00E1JGcvFg";

        CustomerCreateParams params =
                CustomerCreateParams.builder()
                        .build();


        Customer customer = Customer.create(params);

        PaymentIntentCreateParams paymentParams =
                PaymentIntentCreateParams.builder()
                        .setAmount(1099L)
                        .setCurrency("gbp")
                        .setPaymentMethod("{{PAYMENT_METHOD_ID}}")
                        .setCustomer("{{CUSTOMER_ID}}")
                        .setConfirm(true)
                        .setOffSession(true)
                        .build();

        try {
            PaymentIntent.create(paymentParams);
        } catch (CardException e) {
            // Error code will be authentication_required if authentication is needed
            System.out.println("Error code is : " + e.getCode());
            String paymentIntentId = e.getStripeError().getPaymentIntent().getId();
            PaymentIntent paymentIntent = PaymentIntent.retrieve(paymentIntentId);
            System.out.println(paymentIntent.getId());
        }

        PaymentMethodListParams params2 =
                PaymentMethodListParams.builder()
                        .setCustomer("{{CUSTOMER_ID}}")
                        .setType(PaymentMethodListParams.Type.CARD)
                        .build();

        PaymentMethodCollection paymentMethods = PaymentMethod.list(params2);
    }
}
