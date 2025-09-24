package com.example.order_service.component;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Objects;

@Component
public class FeignComponent implements RequestInterceptor {
    private static final String AUTHORIZATION_HEADER = "Authorization";

    @Override
    public void apply(RequestTemplate requestTemplate) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        if(attributes != null){
            HttpServletRequest request = attributes.getRequest();
            String authHeader = request.getHeader(AUTHORIZATION_HEADER);
            if(Objects.nonNull(authHeader)){
                requestTemplate.header(AUTHORIZATION_HEADER , authHeader);
            }
        }
    }
}
