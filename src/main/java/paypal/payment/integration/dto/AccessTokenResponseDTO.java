package paypal.payment.integration.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;

public class AccessTokenResponseDTO {

    private String scope;
    @JsonProperty("access_token")
    private String accessToken;
    @JsonProperty("token_type")
    private String tokenType;
    @JsonProperty("app_id")
    private String applicationId;
    @JsonProperty("expires_in")
    private int expiresIn;
    private String nonce;
    @JsonIgnore
    private Instant expiration;
}
