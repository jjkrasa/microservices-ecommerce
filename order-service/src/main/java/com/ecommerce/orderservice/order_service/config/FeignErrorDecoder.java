package com.ecommerce.orderservice.order_service.config;

import com.ecommerce.exceptionlib.ErrorCode;
import com.ecommerce.exceptionlib.exception.NotFoundException;
import feign.Response;
import feign.codec.ErrorDecoder;
import org.springframework.stereotype.Component;

@Component
public class FeignErrorDecoder implements ErrorDecoder {

    private final ErrorDecoder defaultDecoder = new Default();

    @Override
    public Exception decode(String methodKey, Response response) {

        if (methodKey.contains("CartClient#clearCart")) {
            if (response.status() == 404) {
                return new NotFoundException(ErrorCode.CART_DOES_NOT_EXIST.getMessage());
            }
        }

        return defaultDecoder.decode(methodKey, response);
    }
}
