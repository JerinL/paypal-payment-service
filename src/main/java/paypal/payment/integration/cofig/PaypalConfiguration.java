package paypal.payment.integration.cofig;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.OAuthTokenCredential;
import com.paypal.base.rest.PayPalRESTException;
import com.paypal.core.PayPalEnvironment;
import com.paypal.core.PayPalHttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.http.HttpClient;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class PaypalConfiguration {

    private final PaypalConfig paypalConfig;
    private final ObjectMapper objectMapper;
    private final HttpClient httpClient;

    @Autowired
    public PaypalConfiguration(PaypalConfig paypalConfig, ObjectMapper objectMapper) {
        this.paypalConfig = paypalConfig;
        this.objectMapper = objectMapper;
        httpClient = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .build();

    }}

