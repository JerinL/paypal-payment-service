    package paypal.payment.integration.controller;



    import lombok.extern.slf4j.Slf4j;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.beans.factory.annotation.Value;
    import org.springframework.core.ParameterizedTypeReference;
    import org.springframework.http.*;
    import org.springframework.web.bind.annotation.*;
    import org.springframework.web.client.HttpClientErrorException;
    import org.springframework.web.client.RestTemplate;
    import org.springframework.web.util.UriComponentsBuilder;
    import paypal.payment.integration.dto.AccessTokenResponseDTO;
    import paypal.payment.integration.service.PaypalService;

    import java.net.URI;
    import java.util.*;

    @RestController
    @Slf4j
    public class paymentController {
        @Autowired
        private PaypalService paypalService;

        @Value("${paypal.clientId}")
        private String paypalClientId;

        @Value("${paypal.clientSecret}")
        private String paypalClientSecret;

        @Autowired
        private final RestTemplate restTemplate = new RestTemplate();

        private final String basePayPalApiUrl = "https://api-m.sandbox.paypal.com/v2";  // Define base PayPal API URL

        @GetMapping("/access-token")
        public AccessTokenResponseDTO getAccessToken() {
            return paypalService.getAccessToken();
        }

        @RequestMapping(value = "/create-order", method = RequestMethod.POST)
        public Object createOrder() {
            String credentials = paypalClientId + ":" + paypalClientSecret;
            String base64Credentials = new String(Base64.getEncoder().encode(credentials.getBytes()));

            HttpHeaders headers = new HttpHeaders();
            headers.add("Authorization", "Basic " + base64Credentials);
            headers.add("Content-Type", "application/json");
            headers.add("Accept", "application/json");

            String requestJson = "{\"intent\":\"CAPTURE\",\"purchase_units\":[{\"amount\":{\"currency_code\":\"USD\",\"value\":\"100.00\"}}]}";
            HttpEntity<String> entity = new HttpEntity<>(requestJson, headers);

            String basePayPalApiUrl = "https://api-m.sandbox.paypal.com/v2";
            ResponseEntity<Object> response = restTemplate.exchange(
                    basePayPalApiUrl + "/checkout/orders",
                    HttpMethod.POST,
                    entity,
                    Object.class
            );

            if (response.getStatusCode() == HttpStatus.CREATED) {
                log.info( "ORDER CAPTURE");
                return response.getBody();
            } else {
                log.info( "FAILED CAPTURING ORDER");
                return "Failed to capture order. Status code: " + response.getStatusCode();
            }
        }

        @GetMapping("/order-details/{orderId}")
        public Object getOrderDetails(@PathVariable String orderId) {
            String credentials = paypalClientId + ":" + paypalClientSecret;
            String base64Credentials = new String(Base64.getEncoder().encode(credentials.getBytes()));

            HttpHeaders headers = new HttpHeaders();
            headers.add("Authorization", "Basic " + base64Credentials);
            headers.add("Content-Type", "application/json");
            headers.add("Accept", "application/json");

            String basePayPalApiUrl = "https://api-m.sandbox.paypal.com/v2";

            String apiUrl = UriComponentsBuilder.fromUriString(basePayPalApiUrl)
                    .path("/checkout/orders/{orderId}")
                    .buildAndExpand(orderId)
                    .toUriString();

            ResponseEntity<Object> response = restTemplate.exchange(
                    apiUrl,
                    HttpMethod.GET,
                    new HttpEntity<>(headers),
                    Object.class
            );

            if (response.getStatusCode() == HttpStatus.OK) {
                return response.getBody();
            } else {
                return "Failed to fetch order details. Status code: " + response.getStatusCode();
            }
        }


//    @PostMapping("/authorize-order/{orderId}")
//    public ResponseEntity<Object> authorizePayments(@PathVariable("orderId") String orderId) {
//        String credentials = paypalClientId + ":" + paypalClientSecret;
//        String base64Credentials = new String(Base64.getEncoder().encode(credentials.getBytes()));
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.add("Authorization", "Basic " + base64Credentials);
//        headers.add("Content-Type", "application/json");
//        headers.add("Accept", "application/json");
//
//        String basePayPalApiUrl = "https://api-m.sandbox.paypal.com/v2";
//
//        String captureOrderUrl = UriComponentsBuilder.fromUriString(basePayPalApiUrl)
//                .path("/checkout/orders/{orderId}/capture")
//                .buildAndExpand(orderId)
//                .toUriString();
//
//        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
//                captureOrderUrl,
//                HttpMethod.POST,
//                new HttpEntity<>(headers),
//                new ParameterizedTypeReference<Map<String, Object>>() {});
//
//        return ResponseEntity.status(HttpStatus.OK).body(response.getBody());
//    }

        @PostMapping("/authorize-order/{orderId}")
        public ResponseEntity<Object> authorizePayments(@PathVariable("orderId") String orderId) {
            try {
                String credentials = paypalClientId + ":" + paypalClientSecret;
                String base64Credentials = new String(Base64.getEncoder().encode(credentials.getBytes()));

                HttpHeaders headers = new HttpHeaders();
                headers.add("Authorization", "Basic " + base64Credentials);
                headers.add("Content-Type", "application/json");
                headers.add("Accept", "application/json");

                String basePayPalApiUrl = "https://api-m.sandbox.paypal.com/v2";

                String captureOrderUrl = UriComponentsBuilder.fromUriString(basePayPalApiUrl)
                        .path("/checkout/orders/{orderId}/capture")
                        .buildAndExpand(orderId)
                        .toUriString();

                ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                        captureOrderUrl,
                        HttpMethod.POST,
                        new HttpEntity<>(headers),
                        new ParameterizedTypeReference<Map<String, Object>>() {});

                return ResponseEntity.status(HttpStatus.OK).body(response.getBody());

            } catch (HttpClientErrorException.UnprocessableEntity ex) {
                if (ex.getStatusCode() == HttpStatus.UNPROCESSABLE_ENTITY) {
                    String responseBody = ex.getResponseBodyAsString();

                    String approvalUrl = extractApprovalUrl(responseBody);

                    return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                            .header("Location", approvalUrl)
                            .body("Payer has not yet approved the Order. Redirect to the approval URL.");

                } else {
                    return ResponseEntity.status(ex.getRawStatusCode()).body(ex.getStatusText());
                }
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
            }
        }
        private String extractApprovalUrl(String responseBody) {

            Map<String, Object> responseMap = restTemplate.getForObject(responseBody, Map.class);
            List<Map<String, String>> links = (List<Map<String, String>>) responseMap.get("links");

            for (Map<String, String> link : links) {
                if ("approve".equals(link.get("rel"))) {
                    return link.get("href");
                }
            }
            throw new RuntimeException("Approval URL not found in the response.");
        }

        @RequestMapping(value="/api/orders/{orderId}/capture", method = RequestMethod.POST)
        @CrossOrigin
        public Object capturePayment(@PathVariable("orderId") String orderId) {
            HttpHeaders headers = new HttpHeaders();
            headers.setBasicAuth(paypalClientId, paypalClientSecret);
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<String> entity = new HttpEntity<String>(null, headers);
            String paypalHostname = "https://api-m.sandbox.paypal.com";
            ResponseEntity<Object> response = restTemplate.exchange(
                    paypalHostname + "/v2/checkout/orders/" + orderId + "/capture",
                    HttpMethod.POST,
                    entity,
                    Object.class
            );

            if (response.getStatusCode() == HttpStatus.CREATED) {
                log.info("ORDER CREATED");
                return response.getBody();
            } else {
                log.info( "FAILED CREATING ORDER");

                return "Unavailable to get CREATE AN ORDER, STATUS CODE " + response.getStatusCode();
            }
        }




    }




