package com.wholdus.www.wholdusbuyerapp.helperClasses;

import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by aditya on 17/11/16.
 */

public class InputValidationHelper {

    public static boolean isValidMobileNumber(@Nullable TextInputLayout wrapper, String inputValue) {
        Pattern phonePattern = Pattern.compile("^[7-9][0-9]{9}$");
        Matcher phoneMatcher = phonePattern.matcher(inputValue);
        String error = null;

        if (inputValue.isEmpty()) {
            error = "Please enter your mobile number";
        } else if (!phoneMatcher.matches()) {
            error = "Please check your number";
        } else if (inputValue.length() < 9) {
            error = "Mobile number has to be 10 digits";
        }

        return returnHelper(wrapper, error);
    }

    public static boolean isValidPassword(@Nullable TextInputLayout wrapper, String inputValue) {
        String error = null;

        if (inputValue.isEmpty()) {
            error = "Please enter your password";
        } else if (inputValue.length() < 5) {
            error = "Too short password";
        }

        return returnHelper(wrapper, error);
    }

    public static boolean isNameValid(@Nullable TextInputLayout wrapper, String inputValue) {
        String error = null;

        if (inputValue.isEmpty()) {
            error = "Please Enter your name";
        }

        return returnHelper(wrapper, error);
    }

    private static boolean returnHelper(@Nullable TextInputLayout wrapper, String error) {
        if (wrapper != null) {
            wrapper.setError(error);
        }
        return (error == null);
    }
}