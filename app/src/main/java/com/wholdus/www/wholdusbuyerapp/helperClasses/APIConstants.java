package com.wholdus.www.wholdusbuyerapp.helperClasses;

/**
 * Created by aditya on 17/12/16.
 */

public class APIConstants {

    public static final String API_BASE = "http://api.wholdus.com/";
    public static final String TEMP_API_BASE = "http://13.76.211.119:8000/";

    public static final String LOGIN_URL = "users/buyer/login/";
    public static final String REGISTER_URL = "users/buyer/register/";
    public static final String VERIFY_OTP_URL = "users/buyer/register/verify/";
    public static final String RESEND_OTP_URL = "users/buyer/register/resend_sms/";
    public static final String FORGOT_PASSWORD_URL = "users/buyer/forgotpassword/";

    public static final String CATEGORY_URL = "category/";
    public static final String BUYER_INTEREST_URL = "users/buyer/buyerinterest/";
    public static final String FIREBASE_TOKEN_REGISTRATION_URL = "users/buyer/firebase/token/";

    public static final String API_PAGE_NUMBER_KEY = "page_number";
    public static final String API_TOTAL_PAGES_KEY = "total_pages";
    public static final String BUYER_LOGIN_KEY = "buyer_login";
    public static final String LOGIN_API_DATA = "login_api_data";

    public static final String HEADER_ACCEPT = "Accept";
    public static final String HEADER_AUTH = "Authorization";
    public static final String HEADER_ACCEPT_V0 = "version=0";
    public static final String HEADER_ACCEPT_V1 = "version=1";
    public static final String RESPONSE_CODE = "responseCode";

    public static final String REGISTRATION_TOKEN_KEY = "registration_token";
    public static final String OTP_NUMBER_KEY = "otp_number";
}
