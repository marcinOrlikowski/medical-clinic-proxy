package com.marcinorlikowski.medicalclinicproxy.exceptions;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.FeignException;
import feign.Response;
import feign.RetryableException;
import feign.codec.ErrorDecoder;
import org.springframework.http.HttpStatus;

import java.io.InputStream;

public class MedicalClinicErrorDecoder implements ErrorDecoder {
    private final ErrorDecoder defaultDecoder = new ErrorDecoder.Default();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Exception decode(String methodKey, Response response) {
        String defaultMessage = "Medical clinic API error occurred";
        int status = response.status();
        if (status >= 400 && status < 500) {
            try (InputStream bodyIs = response.body().asInputStream()) {
                JsonNode jsonNode = objectMapper.readTree(bodyIs);
                String message = jsonNode.has("message")
                        ? jsonNode.get("message").asText()
                        : defaultMessage;
                return new MedicalClinicException(message, HttpStatus.valueOf(status));
            } catch (Exception e) {
                return new MedicalClinicException("Unknown API error occurred", HttpStatus.valueOf(status));
            }
        }

        FeignException feignException = FeignException.errorStatus(methodKey, response);
        switch (status) {
            case 500, 502, 504 -> {
                return new MedicalClinicException(defaultMessage, HttpStatus.valueOf(status));
            }
            case 503 -> {
                return new RetryableException(
                        response.status(),
                        feignException.getMessage(),
                        response.request().httpMethod(),
                        feignException,
                        50L,
                        response.request());
            }
            default -> {
                return defaultDecoder.decode(methodKey, response);
            }
        }
    }
}
