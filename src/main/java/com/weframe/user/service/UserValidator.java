package com.weframe.user.service;

import com.weframe.user.model.User;
import com.weframe.user.service.persistence.exception.InvalidFieldException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.EmailValidator;

public class UserValidator {

    public void validateInsert(User user) throws InvalidFieldException {

        if(!EmailValidator.getInstance().isValid(user.getEmail())) {
            throw new InvalidFieldException(
                    "email",
                    user.getEmail(),
                    "The provided email is not valid, please try again."
            );
        }

        if(StringUtils.isBlank(user.getFirstName())) {
            throw new InvalidFieldException(
                    "firstName",
                    user.getFirstName(),
                    "The provided first name cannot be blank."
            );
        }

        if(StringUtils.isBlank(user.getLastName())) {
            throw new InvalidFieldException(
                    "lastName",
                    user.getLastName(),
                    "The provided last name cannot be blank."
            );
        }

        if(StringUtils.isBlank(user.getPassword())) {
            throw new InvalidFieldException(
                    "password",
                    StringUtils.EMPTY,
                    "The provided first name cannot be blank."
            );
        }

    }

    public boolean isValidInsert(User user) {
        return user != null &&
                !StringUtils.isBlank(user.getEmail()) &&
                !StringUtils.isBlank(user.getFirstName()) &&
                !StringUtils.isBlank(user.getLastName()) &&
                !StringUtils.isBlank(user.getPassword());
    }

    public boolean isValidUpdate(User user) {
        return user != null &&
                !StringUtils.isBlank(user.getEmail()) &&
                !StringUtils.isBlank(user.getFirstName()) &&
                !StringUtils.isBlank(user.getLastName());
    }

}