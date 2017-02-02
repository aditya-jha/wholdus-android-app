package com.wholdus.www.wholdusbuyerapp.helperClasses;

/**
 * Created by aditya on 17/12/16.
 */

public class APIConstants {

    public static final String API_BASE = "http://api.wholdus.com/";

    public static final String LOGIN_URL = "users/buyer/login/";
    public static final String REGISTER_URL = "users/buyer/register/";
    public static final String VERIFY_OTP_URL = "users/buyer/register/verify/";
    public static final String RESEND_OTP_URL = "users/buyer/register/resend_sms/";
    public static final String FORGOT_PASSWORD_URL = "users/buyer/forgotpassword/";
    public static final String FORGOT_PASSWORD_VERIFY_URL = "users/buyer/forgotpassword/verify/";
    public static final String FORGOT_PASSWORD_RESEND_OTP_URL = "users/buyer/forgotpassword/resend_sms/";

    public static final String CATEGORY_URL = "category/";
    public static final String BUYER_INTEREST_URL = "users/buyer/buyerinterest/";
    public static final String FIREBASE_TOKEN_REGISTRATION_URL = "users/buyer/firebase/token/";

    public static final String PRODUCT_URL = "products/";
    public static final String PRODUCT_DELETED_OFFLINE_URL = "products/offlinedeleted/";

    public static final String BUYER_PRODUCT_URL = "users/buyer/buyerproducts/";
    public static final String BUYER_PRODUCT_RESPONSE_URL = "users/buyer/buyerproducts/response/";

    public static final String FAQ_URL = "general/faqs/";
    public static final String ABOUT_US_URL = "general/aboutus/";
    public static final String PRIVACY_POLICY_URL = "general/privacypolicy/";
    public static final String RETURN_REFUND_POLICY_URL = "general/termsandconditions/";
    public static final String CONTACT_US_URL = "leads/contactus/";

    public static final String PINCODE_SERVICEABILITY_URL = "logistics/pincodeserviceability/";

    public static final String API_PAGE_NUMBER_KEY = "page_number";
    public static final String API_TOTAL_PAGES_KEY = "total_pages";
    public static final String API_TOTAL_ITEMS_KEY = "total_items";
    public static final String API_ITEM_PER_PAGE_KEY = "items_per_page";
    public static final String TOTAL_ITEMS_KEY = "total_items";

    public static final String BUYER_LOGIN_KEY = "buyer_login";
    public static final String LOGIN_API_DATA = "login_api_data";

    public static final String HEADER_ACCEPT = "Accept";
    public static final String HEADER_AUTH = "Authorization";
    public static final String HEADER_ACCEPT_V0 = "version=0";
    public static final String HEADER_ACCEPT_V1 = "version=1";
    public static final String RESPONSE_CODE = "responseCode";

    public static final String REGISTRATION_TOKEN_KEY = "registration_token";
    public static final String FORGOT_PASSWORD_TOKEN = "forgot_password_token";
    public static final String OTP_NUMBER_KEY = "otp_number";
    public static final String API_RESPONSE_CODE_KEY = "responded";
}
