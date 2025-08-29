package com.example.bajaj;

import org.springframework.boot.CommandLineRunner;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Component
public class WebhookProcessor implements CommandLineRunner {

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public void run(String... args) {
        try {
            // STEP 1: Generate webhook and access token
            String registrationUrl = "https://bfhldevapigw.healthrx.co.in/hiring/generateWebhook/JAVA";

            Map<String, String> registrationBody = new HashMap<>();
            registrationBody.put("name", "Sai Manogna Jyesta"); 
            registrationBody.put("regNo", "22BCE9630");
            registrationBody.put("email", "manogna.22bce9630@vitapstudent.ac.in"); 

            HttpHeaders regHeaders = new HttpHeaders();
            regHeaders.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, String>> regRequest = new HttpEntity<>(registrationBody, regHeaders);

            ResponseEntity<Map> regResponse = restTemplate.exchange(
                    registrationUrl, HttpMethod.POST, regRequest, Map.class);

            if (regResponse.getStatusCode() == HttpStatus.OK && regResponse.getBody() != null) {
                String webhookUrl = (String) regResponse.getBody().get("webhook");
                String accessToken = (String) regResponse.getBody().get("accessToken");

                System.out.println("Webhook: " + webhookUrl);
                System.out.println("Access Token: " + accessToken);

                // SQL query for Question 2 (even regNo)
                String sqlQuery = """
                    SELECT 
                        e1.EMP_ID,
                        e1.FIRST_NAME,
                        e1.LAST_NAME,
                        d.DEPARTMENT_NAME,
                        COUNT(e2.EMP_ID) AS YOUNGER_EMPLOYEES_COUNT
                    FROM 
                        EMPLOYEE e1
                    JOIN 
                        DEPARTMENT d ON e1.DEPARTMENT = d.DEPARTMENT_ID
                    LEFT JOIN 
                        EMPLOYEE e2 ON e1.DEPARTMENT = e2.DEPARTMENT 
                                AND e1.DOB < e2.DOB
                    GROUP BY 
                        e1.EMP_ID, e1.FIRST_NAME, e1.LAST_NAME, d.DEPARTMENT_NAME
                    ORDER BY 
                        e1.EMP_ID DESC
                    """;

                // STEP 2: Submit answer to webhook
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                headers.set("Authorization", "Bearer " + accessToken);

                Map<String, String> body = new HashMap<>();
                body.put("finalQuery", sqlQuery.trim());

                HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);

                ResponseEntity<String> submissionResponse = restTemplate.exchange(
                        webhookUrl, HttpMethod.POST, request, String.class);

                System.out.println("Submission Response: " + submissionResponse.getStatusCode());
                System.out.println("Response Body: " + submissionResponse.getBody());
            } else {
                System.out.println("Registration failed: " + regResponse.getStatusCode());
            }

        } catch (Exception e) {
            System.out.println("Error occurred during execution:");
            e.printStackTrace();
        }
    }
}
