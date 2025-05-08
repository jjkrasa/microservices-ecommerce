package com.ecommerce.cartservice.cart_service.config;

import com.ecommerce.cartservice.cart_service.exception.BadRequestException;
import com.ecommerce.cartservice.cart_service.exception.ErrorCode;
import feign.Response;
import feign.codec.ErrorDecoder;
import org.springframework.stereotype.Component;

@Component
public class FeignErrorDecoder implements ErrorDecoder {

    private final ErrorDecoder defaultDecoder = new Default();

    @Override
    public Exception decode(String s, Response response) {

        switch (response.status()) {
            case 404:
                if (s.contains("ProductClient#getProductById")) {
                    return new BadRequestException(ErrorCode.PRODUCT_DOES_NOT_EXIST.getMessage());
                }
        }

        return defaultDecoder.decode(s, response);
    }
}
