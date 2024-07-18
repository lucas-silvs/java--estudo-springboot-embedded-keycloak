package com.lucassilvs.springbootkeycloak.config.providers.crud.crud;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.Charset;
import java.util.List;

public class CrudHttpClient {

    private final static Logger LOGGER = LoggerFactory.getLogger(CrudHttpClient.class);

    private static ObjectMapper objectMapper = new ObjectMapper();


    public static void createUser(String url, CrudUserModel crudUserModel) {
        //implement http client send post to url with userModel
        try {
            String requestBody = objectMapper.writeValueAsString(crudUserModel);

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // Example response handling
            LOGGER.info("CrudHttpClient - Create USer Response status code: {}", response.statusCode());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static CrudUserModel getUserByUsername(String url, String path, String username) {
        //implement http client send get to url with cpf
        CrudUserModel crudUserModel = null;
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(String.format("%s/%s/%s", url, path, username)))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString(Charset.defaultCharset()));

            // Example response handling
            LOGGER.info("CrudHttpClient - Get User By Cpf Response status code: {}", response.statusCode());
            LOGGER.info("CrudHttpClient - Get User By Cpf Response body: {}", response.body());

                crudUserModel = objectMapper.readValue(response.body(), CrudUserModel.class);
        } catch (Exception e) {
            e.printStackTrace();

        }
        return crudUserModel;
    }

    public static List<CrudUserModel> listUsers(String url, String path) {
        //implement http client send get to url
        List<CrudUserModel> crudUserModelList = null;
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(String.format("%s/%s", url, path)))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // Example response handling
            LOGGER.info("CrudHttpClient - List Users Response status code: {}", response.statusCode());
            LOGGER.info("CrudHttpClient - List Users Response body: {}", response.body());

            return objectMapper.readValue(response.body(), new TypeReference<List<CrudUserModel>>() {
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return List.of();
    }

    public static boolean validateUserCredentials(String url, String path, CrudUserModel userModel) {
        //implement http client send get to url with cpf and senha
        try {
            LOGGER.info("CrudHttpClient - Validate User Credentials Request: {}", userModel);
            String requestBody = objectMapper.writeValueAsString(userModel);

            LOGGER.info("CrudHttpClient - Validate User Credentials Request body: {}", requestBody);

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(String.format("%s/%s", url, path)))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // Example response handling
            LOGGER.info("CrudHttpClient - Validate User Credentials Response status code: {}", response.statusCode());
            LOGGER.info("CrudHttpClient - Validate User Credentials Response body: {}", response.body());
            return response.statusCode() == 204;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void healthCheck(String healthCheckUrl) {
        //implement http client send get to healthCheckUrl
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(healthCheckUrl))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // Example response handling
            LOGGER.info("CrudHttpClient - Health Check Response status code: {}", response.statusCode());
            LOGGER.info("CrudHttpClient - Health Check Response body: {}", response.body());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
