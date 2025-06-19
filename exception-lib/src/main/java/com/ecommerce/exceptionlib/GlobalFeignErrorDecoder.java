package com.ecommerce.exceptionlib;

import com.ecommerce.exceptionlib.exception.BadRequestException;
import com.ecommerce.exceptionlib.exception.ConflictException;
import com.ecommerce.exceptionlib.exception.InternalServerException;
import com.ecommerce.exceptionlib.exception.NotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Response;
import feign.Util;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Map;

@Slf4j
@Component
public class GlobalFeignErrorDecoder implements ErrorDecoder {

    private final ObjectMapper objectMapper = new ObjectMapper();


    @Override
    public Exception decode(String methodKey, Response response) {
        HttpStatus httpStatus = HttpStatus.valueOf(response.status());

        try {
            String body = Util.toString(response.body().asReader(StandardCharsets.UTF_8));
            log.info("Feign error response body: {}", body);

            ErrorResponse errorResponse = objectMapper.readValue(body, ErrorResponse.class);
            Map<String, String> errors = errorResponse.errors();


            return switch (httpStatus) {
                case HttpStatus.BAD_REQUEST -> new BadRequestException(errors);
                case HttpStatus.NOT_FOUND -> new NotFoundException(errors.getOrDefault("message", null));
                case HttpStatus.CONFLICT -> new ConflictException(errors.getOrDefault("message", null));
                case HttpStatus.INTERNAL_SERVER_ERROR -> new InternalServerException(errors.getOrDefault("message", null));
                default -> new RuntimeException("Unexpected error");
            };
        } catch (Exception e) {
            log.error("Failed to decode Feign error response", e);

            return new InternalServerException();
        }
    }
}
