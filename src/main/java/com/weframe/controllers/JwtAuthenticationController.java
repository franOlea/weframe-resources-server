package com.weframe.controllers;

import com.weframe.controllers.errors.Error;
import com.weframe.controllers.errors.ErrorResponse;
import com.weframe.security.UserCredentials;
import com.weframe.user.model.User;
import com.weframe.user.service.persistence.UserService;
import com.weframe.user.service.persistence.exception.EmailAlreadyUsedException;
import com.weframe.user.service.persistence.exception.ForbiddenOperationException;
import com.weframe.user.service.persistence.exception.InvalidFieldException;
import com.weframe.user.service.persistence.exception.InvalidUserPersistenceException;
import com.weframe.user.service.security.UserIdentityResolver;
import com.weframe.user.service.security.UserPasswordCryptographer;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.security.GeneralSecurityException;
import java.util.Collection;
import java.util.Collections;

@SuppressWarnings({"unused", "WeakerAccess"})
@RestController
@RequestMapping("/authentication")
@CrossOrigin
@Profile("jwt")
public class JwtAuthenticationController {

    private static final Logger logger = Logger.getLogger(Auth0AuthenticationController.class);

    private final ResponseGenerator<User> responseGenerator;

    private final UserService userService;
    private final UserPasswordCryptographer passwordCryptographer;
    private final UserIdentityResolver identityResolver;

    public JwtAuthenticationController(final ResponseGenerator<User> responseGenerator,
                                       final UserService userService,
                                       final UserPasswordCryptographer passwordCryptographer,
                                       final UserIdentityResolver identityResolver) {
        this.responseGenerator = responseGenerator;
        this.userService = userService;
        this.passwordCryptographer = passwordCryptographer;
        this.identityResolver = identityResolver;
    }

    @RequestMapping(value = "/me", method = RequestMethod.GET)
    private ResponseEntity getMe(final Authentication authentication) {
        try {
            String identityEmail = identityResolver.resolve(authentication);
            User user = userService.getByEmail(identityEmail);
            logger.info("Retrieved user [" + user + "] by token id [" + authentication.getName() + "]");
            return responseGenerator.generateResponse(user);
        } catch (EmptyResultException e) {
            return responseGenerator.generateEmptyResponse();
        } catch (Exception e) {
            logger.error(
                    String.format(
                            "There was an unexpected error trying to fetch a user by token id [%s].",
                            authentication.getName()
                    ),
                    e);
            Error error = new Error(
                    "internal-server-error",
                    "There has been an internal server error. Please try again later.");
            return generateErrorResponse(Collections.singleton(error), HttpStatus.SERVICE_UNAVAILABLE);
        }
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    private ResponseEntity login(@RequestBody final UserCredentials userCredentials) {
        try {
            User user = userService.getByEmail(userCredentials.getUsername());
            if(passwordCryptographer.isValidPassword(
                    userCredentials.getPassword(),
                    user.getPassword())) {
                return responseGenerator.generateResponse(user);
            } else {
                return generateAuthenticationError();
            }
        } catch (EmptyResultException e) {
            return generateAuthenticationError();
        } catch (InvalidUserPersistenceException | GeneralSecurityException e) {
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private ResponseEntity generateAuthenticationError() {
        return responseGenerator.generateResponse(HttpStatus.I_AM_A_TEAPOT);
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    private ResponseEntity register(@RequestBody User user) {
        try {
            user.setFirstName(StringUtils.EMPTY);
            user.setLastName(StringUtils.EMPTY);
            userService.create(user);
            logger.info("Created user [" + user.getEmail() + "].");
            return new ResponseEntity<>(null, new HttpHeaders(), HttpStatus.OK);
        } catch(InvalidFieldException e) {
            Error error = new Error(
                    String.format("invalid-%s", e.getField()),
                    String.format(
                            "The %s %s given is invalid, please try again.",
                            e.getField(),
                            e.getFieldContent()
                    )
            );
            return generateErrorResponse(Collections.singleton(error), HttpStatus.UNPROCESSABLE_ENTITY);
        } catch (EmailAlreadyUsedException e) {
            Error error = new Error("email-already-in-use", "The email given is already in use.");
            return generateErrorResponse(Collections.singleton(error), HttpStatus.UNPROCESSABLE_ENTITY);
        } catch(ForbiddenOperationException e) {
            Error error = new Error("invalid-user", "The user parameters given are invalid.");
            return generateErrorResponse(Collections.singleton(error), HttpStatus.UNPROCESSABLE_ENTITY);
        } catch(InvalidUserPersistenceException e) {
            logger.error(String.format("There has been an error creating user [%s]", user.getEmail()), e);
            Error error = new Error(
                    "internal-serer-error",
                    "There has been an internal server error. Please try again later.");
            return generateErrorResponse(Collections.singleton(error), HttpStatus.UNPROCESSABLE_ENTITY);
        } catch (Exception e) {
            logger.error("There was an unexpected error trying to create a user.", e);
            Error error = new Error(
                    "internal-server-error",
                    "There has been an internal server error. Please try again later.");
            return generateErrorResponse(Collections.singleton(error), HttpStatus.SERVICE_UNAVAILABLE);
        }
    }

    private ResponseEntity<ErrorResponse> generateErrorResponse(final Collection<Error> errors,
                                                                final HttpStatus httpStatus) {
        return responseGenerator.generateErrorResponse(errors, httpStatus);
    }
}
