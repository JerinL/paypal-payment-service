package paypal.payment.integration.service;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import paypal.payment.integration.dto.AccessTokenResponseDTO;


@Service
@Slf4j
public class PaypalService {

    @Value("${paypal.clientId}")
    private String paypalClientId;

    @Value("${paypal.clientSecret}")
    private String paypalClientSecret;

    @Value("${stripe.secret-key}")
    private String stripeSecretKey;
    @Value("${paypal.api.url}")
    private String paypalApiUrl;

    private final RestTemplate restTemplate;

    private static final String PAYPAL_API_URL = "https://api.sandbox.paypal.com/v1";

    @Autowired
    public PaypalService(
            @Value("${paypal.clientId}") String paypalClientId,
            @Value("${paypal.clientSecret}") String paypalClientSecret,
            @Value("${stripe.secret-key}") String stripeSecretKey,
            RestTemplate restTemplate) {
        this.paypalClientId = paypalClientId;
        this.paypalClientSecret = paypalClientSecret;
        this.stripeSecretKey = stripeSecretKey;
        this.restTemplate = restTemplate;
    }


    public String createStripePaymentIntent(Double amount, String currency) {
        try {
            Stripe.apiKey = stripeSecretKey;

            PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                    .setAmount(Math.round(amount * 100))
                    .setCurrency(currency)
                    .build();

            PaymentIntent paymentIntent = PaymentIntent.create(params);

            return paymentIntent.getClientSecret();
        } catch (StripeException e) {
            throw new RuntimeException("Error creating Stripe payment intent", e);
        }
    }

//        public Payment createPayment(PaymentRequest paymentRequest) throws PayPalRESTException {
//            try {
//                Amount payAmount = new Amount();
//                payAmount.setCurrency(paymentRequest.getCurrency());
//                payAmount.setTotal(paymentRequest.getAmount());
//
//                Transaction transaction = new Transaction();
//                transaction.setDescription(paymentRequest.getDescription());
//                transaction.setAmount(payAmount);
//
//                List<Transaction> transactions = new ArrayList<>();
//                transactions.add(transaction);
//
//                Payer payer = new Payer();
//                payer.setPaymentMethod("credit_card");
//
//                CreditCardDetails creditCardDetails = paymentRequest.getCreditCard();
//                CreditCard creditCard = new CreditCard();
//                creditCard.setNumber(creditCardDetails.getNumber());
//                creditCard.setType(creditCardDetails.getType());
//                creditCard.setExpireMonth(creditCardDetails.getExpireMonth());
//                creditCard.setExpireYear(creditCardDetails.getExpireYear());
//                creditCard.setCvv2(Integer.valueOf(creditCardDetails.getCvv2()));
//                creditCard.setFirstName(creditCardDetails.getFirstName());
//                creditCard.setLastName(creditCardDetails.getLastName());
//
//                FundingInstrument fundingInstrument = new FundingInstrument();
//                fundingInstrument.setCreditCard(creditCard);
//
//                payer.setFundingInstruments(List.of(fundingInstrument));
//
//                Payment payment = new Payment();
//                payment.setIntent("sale");
//                payment.setPayer(payer);
//                payment.setTransactions(transactions);
//
//                APIContext apiContext = new APIContext("AQFplKJXZ_F5Z_dCrybMKzaBhDbfnfEHra1xPbsLLOInOhCI3MlGKmYQpW2CXQxaQ3cwnc2A7_DvmCIC", "EI4VoHMXG8XIIH-OEVaz3HO5hz0GQpsgDy71W50fsNUASWi91wBm0p9y3aXPujA6GGPg64LJRBquQnv4", "sandbox");
//
//                return payment.create(apiContext);
//            } catch (NumberFormatException | PayPalRESTException e) {
//                e.printStackTrace();
//                throw new RuntimeException(e);
//            }
//        }

//    public Payment createPayment(PaymentRequest paymentRequest) throws PayPalRESTException {
//        try {
//            if (paymentRequest.getCreditCard() == null) {
//                throw new IllegalArgumentException("CreditCardDetails is null in the paymentRequest.");
//            }
//
//            Amount payAmount = new Amount();
//            payAmount.setCurrency(paymentRequest.getCurrency());
//            payAmount.setTotal(paymentRequest.getAmount());
//
//            Transaction transaction = new Transaction();
//            transaction.setDescription(paymentRequest.getDescription());
//            transaction.setAmount(payAmount);
//
//            List<Transaction> transactions = new ArrayList<>();
//            transactions.add(transaction);
//
//            Payer payer = new Payer();
//            payer.setPaymentMethod("credit_card");
//
//            CreditCardDetails creditCardDetails = paymentRequest.getCreditCard();
//            CreditCard creditCard = new CreditCard();
//            creditCard.setNumber(creditCardDetails.getNumber());
//            creditCard.setType(creditCardDetails.getType());
//            creditCard.setExpireMonth(creditCardDetails.getExpireMonth());
//            creditCard.setExpireYear(creditCardDetails.getExpireYear());
//            creditCard.setCvv2(creditCardDetails.getCvv2());
//            creditCard.setFirstName(creditCardDetails.getFirstName());
//            creditCard.setLastName(creditCardDetails.getLastName());
//
//            FundingInstrument fundingInstrument = new FundingInstrument();
//            fundingInstrument.setCreditCard(creditCard);
//
//            payer.setFundingInstruments(List.of(fundingInstrument));
//
//            Payment payment = new Payment();
//            payment.setIntent("sale");
//            payment.setPayer(payer);
//            payment.setTransactions(transactions);
//
//            APIContext apiContext = new APIContext("AQFplKJXZ_F5Z_dCrybMKzaBhDbfnfEHra1xPbsLLOInOhCI3MlGKmYQpW2CXQxaQ3cwnc2A7_DvmCIC", "EI4VoHMXG8XIIH-OEVaz3HO5hz0GQpsgDy71W50fsNUASWi91wBm0p9y3aXPujA6GGPg64LJRBquQnv4", "sandbox");
//
//            return payment.create(apiContext);
//        } catch (NumberFormatException | PayPalRESTException e) {
//            e.printStackTrace();
//            throw new RuntimeException(e);
//        }
////    }
//
//    public Payment createPayment(PaymentRequest paymentRequest) throws PayPalRESTException {
//        try {
//            if (paymentRequest.getCreditCard() == null) {
//                throw new IllegalArgumentException("CreditCardDetails is null in the paymentRequest.");
//            }
//
//            Amount payAmount = new Amount();
//            payAmount.setCurrency(paymentRequest.getCurrency());
//            payAmount.setTotal(paymentRequest.getAmount());
//
//            Transaction transaction = new Transaction();
//            transaction.setDescription(paymentRequest.getDescription());
//            transaction.setAmount(payAmount);
//
//            List<Transaction> transactions = new ArrayList<>();
//            transactions.add(transaction);
//
//            Payer payer = new Payer();
//            payer.setPaymentMethod("credit_card");
//
//            CreditCardDetails creditCardDetails = paymentRequest.getCreditCard();
//            CreditCard creditCard = new CreditCard();
//            creditCard.setNumber(creditCardDetails.getNumber());
//            creditCard.setType(creditCardDetails.getType());
//            creditCard.setExpireMonth(creditCardDetails.getExpireMonth());
//            creditCard.setExpireYear(creditCardDetails.getExpireYear());
//            creditCard.setCvv2(creditCardDetails.getCvv2());
//            creditCard.setFirstName(creditCardDetails.getFirstName());
//            creditCard.setLastName(creditCardDetails.getLastName());
//
//            FundingInstrument fundingInstrument = new FundingInstrument();
//            fundingInstrument.setCreditCard(creditCard);
//
//            payer.setFundingInstruments(List.of(fundingInstrument));
//
//            Payment payment = new Payment();
//            payment.setIntent("sale");
//            payment.setPayer(payer);
//            payment.setTransactions(transactions);
//
//            APIContext apiContext = new APIContext("AQFplKJXZ_F5Z_dCrybMKzaBhDbfnfEHra1xPbsLLOInOhCI3MlGKmYQpW2CXQxaQ3cwnc2A7_DvmCIC", "EI4VoHMXG8XIIH-OEVaz3HO5hz0GQpsgDy71W50fsNUASWi91wBm0p9y3aXPujA6GGPg64LJRBquQnv4", "sandbox");
//
//            return payment.create(apiContext);
//        } catch (NumberFormatException | PayPalRESTException e) {
//            e.printStackTrace();
//            throw new RuntimeException(e);
//        }
//    }



    public AccessTokenResponseDTO getAccessToken() {
        String url = PAYPAL_API_URL + "/oauth2/token";
        String credentials = paypalClientId + ":" + paypalClientSecret;
        String base64Credentials = new String(java.util.Base64.getEncoder().encode(credentials.getBytes()));

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Basic " + base64Credentials);
        headers.add("Content-Type", "application/x-www-form-urlencoded");

        HttpEntity<String> request = new HttpEntity<>("grant_type=client_credentials", headers);
        ResponseEntity<AccessTokenResponseDTO> response = new RestTemplate()
                .exchange(url, HttpMethod.POST, request, AccessTokenResponseDTO.class);

        return response.getBody();

    }

}






