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

        if (inputValue.isEmpty() || !phoneMatcher.matches()) {
            error = "Please enter valid mobile number";
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
            error = "Please enter your name";
        }

        return returnHelper(wrapper, error);
    }

    public static boolean isValidPincode(@Nullable TextInputLayout wrapper, String inputValue) {
        String error = null;

        if (inputValue.isEmpty()) {
            error = "Please enter your pincode";
        } else if (inputValue.length() != 6) {
            error = "Please check your pincode";
        } else {
            try {
                Integer.parseInt(inputValue);
            } catch (Exception e) {
                error = "Please check your pincode";
            }
        }

        return returnHelper(wrapper, error);
    }

    public static boolean isValidOTP(@Nullable TextInputLayout wrapper, String inputValue) {
        String error = null;

        if (inputValue.isEmpty()) {
            error = "Please enter the OTP received";
        } else if (inputValue.length() != 6) {
            error = "Invalid OTP";
        } else {
            try {
                Integer.parseInt(inputValue);
            } catch (Exception e) {
                error = "Invalid OTP";
            }
        }

        return returnHelper(wrapper, error);
    }

    private static boolean returnHelper(@Nullable TextInputLayout wrapper, String error) {
        if (wrapper != null) {
            wrapper.setError(error);
            wrapper.requestFocus();
        }
        return (error == null);
    }

    public static boolean isNotEmpty(@Nullable TextInputLayout wrapper, String inputValue, String errorMessage){
        String error = null;

        if (inputValue.isEmpty()) {
            error = errorMessage;
        }

        return returnHelper(wrapper, error);
    }
}