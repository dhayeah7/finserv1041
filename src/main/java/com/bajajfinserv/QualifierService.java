package com.bajajfinserv;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import java.util.HashMap;

@Service
public class QualifierService {

    private static final String WEBHOOK_GENERATION_URL = "https://bfhldevapigw.healthrx.co.in/hiring/generateWebhook/JAVA";
    private static final String WEBHOOK_TEST_URL = "https://bfhldevapigw.healthrx.co.in/hiring/testWebhook/JAVA";

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    public void executeQualifierProcess() {
        try {
            System.out.println("Starting Bajaj Health Qualifier process...");

            // Step 1: Generate webhook
            WebhookResponse webhookResponse = generateWebhook();
            System.out.println("Webhook generated successfully: " + webhookResponse.getWebhook());

            // Step 2: Solve SQL problem and get the query
            String finalQuery = solveSqlProblem();
            System.out.println("SQL problem solved. Final query: " + finalQuery);

            // Step 3: Submit solution
            submitSolution(webhookResponse.getWebhook(), webhookResponse.getAccessToken(), finalQuery);
            System.out.println("Solution submitted successfully!");

        } catch (Exception e) {
            System.err.println("Error in qualifier process: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private WebhookResponse generateWebhook() {
        try {
            // Prepare request body
            Map<String, String> requestBody = new HashMap<>();
            requestBody.put("name", "John Doe");
            requestBody.put("regNo", "REG12347");
            requestBody.put("email", "john@example.com");

            // Prepare headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // Create HTTP entity
            HttpEntity<Map<String, String>> entity = new HttpEntity<>(requestBody, headers);

            // Make POST request
            ResponseEntity<WebhookResponse> response = restTemplate.exchange(
                    WEBHOOK_GENERATION_URL,
                    HttpMethod.POST,
                    entity,
                    WebhookResponse.class);

            return response.getBody();
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate webhook: " + e.getMessage(), e);
        }
    }

    private String solveSqlProblem() {
        // Based on the problem statement, we need to find:
        // 1. Highest salary not credited on 1st day of any month
        // 2. Employee name (first + last), age, and department

        String sqlQuery = """
                SELECT
                    p.AMOUNT AS SALARY,
                    CONCAT(e.FIRST_NAME, ' ', e.LAST_NAME) AS NAME,
                    FLOOR(DATEDIFF(CURDATE(), e.DOB) / 365.25) AS AGE,
                    d.DEPARTMENT_NAME
                FROM PAYMENTS p
                JOIN EMPLOYEE e ON p.EMP_ID = e.EMP_ID
                JOIN DEPARTMENT d ON e.DEPARTMENT = d.DEPARTMENT_ID
                WHERE DAY(p.PAYMENT_TIME) != 1
                ORDER BY p.AMOUNT DESC
                LIMIT 1
                """;

        return sqlQuery.trim();
    }

    private void submitSolution(String webhookUrl, String accessToken, String finalQuery) {
        try {
            // Prepare request body
            Map<String, String> requestBody = new HashMap<>();
            requestBody.put("finalQuery", finalQuery);

            // Prepare headers with JWT token
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", accessToken);

            // Create HTTP entity
            HttpEntity<Map<String, String>> entity = new HttpEntity<>(requestBody, headers);

            // Make POST request to the webhook URL
            ResponseEntity<String> response = restTemplate.exchange(
                    webhookUrl,
                    HttpMethod.POST,
                    entity,
                    String.class);

            System.out.println("Solution submission response: " + response.getBody());
        } catch (Exception e) {
            throw new RuntimeException("Failed to submit solution: " + e.getMessage(), e);
        }
    }

    // Inner class to represent webhook response
    public static class WebhookResponse {
        private String webhook;
        private String accessToken;

        // Getters and setters
        public String getWebhook() {
            return webhook;
        }

        public void setWebhook(String webhook) {
            this.webhook = webhook;
        }

        public String getAccessToken() {
            return accessToken;
        }

        public void setAccessToken(String accessToken) {
            this.accessToken = accessToken;
        }
    }
}