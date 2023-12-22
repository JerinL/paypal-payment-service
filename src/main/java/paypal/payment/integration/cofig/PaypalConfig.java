package paypal.payment.integration.cofig;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
@ConfigurationProperties(prefix = "paypal")
public class PaypalConfig {

    @Value("${paypal.baseUrl}")
    private String baseUrl;
    @NotEmpty
    private String clientId;
    @NotEmpty
    private String secret;

}
