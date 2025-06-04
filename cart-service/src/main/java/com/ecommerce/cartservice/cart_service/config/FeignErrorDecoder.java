package com.ecommerce.cartservice.cart_service.config;

import com.ecommerce.exceptionlib.ErrorCode;
import com.ecommerce.exceptionlib.exception.NotFoundException;
import feign.Response;
import feign.codec.ErrorDecoder;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
public class FeignErrorDecoder implements ErrorDecoder {

    private final ErrorDecoder defaultDecoder = new Default();

    @Override
    public Exception decode(String s, Response response) {
        HttpStatus status = HttpStatus.valueOf(response.status());

        switch (status) {
            case NOT_FOUND:
                if (s.contains("ProductClient#getProductById")) {
                    return new NotFoundException(ErrorCode.PRODUCT_NOT_FOUND.getMessage());
                }

            default:
                return defaultDecoder.decode(s, response);
        }
    }
}
