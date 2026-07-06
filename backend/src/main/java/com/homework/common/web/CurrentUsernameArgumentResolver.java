package com.homework.common.web;

import com.homework.customer.service.CustomerService;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
public class CurrentUsernameArgumentResolver implements HandlerMethodArgumentResolver {

    private static final String USERNAME_HEADER = "X-Username";

    private final CustomerService findCustomer;

    public CurrentUsernameArgumentResolver(CustomerService findCustomer) {
        this.findCustomer = findCustomer;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(CurrentUsername.class)
                && String.class.equals(parameter.getParameterType());
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory)
            throws MissingRequestHeaderException {
        String username = webRequest.getHeader(USERNAME_HEADER);
        if (username == null || username.isBlank()) {
            throw new MissingRequestHeaderException(USERNAME_HEADER, parameter);
        }
        return findCustomer.byUsername(username).username();
    }
}
