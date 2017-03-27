package com.weframe.configuration;

import com.weframe.user.UserPasswordCryptographer;
import com.weframe.user.UserValidator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.security.GeneralSecurityException;

@Configuration
public class ControllerConfiguration {
    @Value("${service.user.password.hash.iterations}")
    private int userPasswordCryptographerHashIterations;

    @Bean
    public UserPasswordCryptographer getUserPasswordCryptographer() throws GeneralSecurityException {
        return new UserPasswordCryptographer(userPasswordCryptographerHashIterations);
    }

    @Bean
    public UserValidator getUSerValidator() {
        return new UserValidator();
    }
}
