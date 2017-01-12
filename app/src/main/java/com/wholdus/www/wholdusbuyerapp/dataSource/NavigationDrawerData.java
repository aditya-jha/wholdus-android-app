package com.wholdus.www.wholdusbuyerapp.dataSource;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by aditya on 16/11/16.
 */

public class NavigationDrawerData {
    public static LinkedHashMap<String, List<String>> getData() {
        LinkedHashMap<String, List<String>> data = new LinkedHashMap<>();

        data.put("Home", new ArrayList<String>());
        data.put("Hand Picked For You", new ArrayList<String>());

        ArrayList<String> shortlist = new ArrayList<>();
        data.put("Shortlist", shortlist);

        ArrayList<String> account = new ArrayList<>();
        account.add("My Profile");
        account.add("My Addresses");
        account.add("My Orders");
        account.add("My Preferences");
        account.add("Rejected Products");
        data.put("My Account", account);

        ArrayList<String> helpSupport = new ArrayList<>();
        helpSupport.add("Contact Us");
        helpSupport.add("About Us");
        helpSupport.add("FAQs");
        helpSupport.add("Return & Refund Policy");
        helpSupport.add("Privacy Policy");
        data.put("Help And Support", helpSupport);

        data.put("Updates", new ArrayList<String>());
        data.put("Logout", new ArrayList<String>());

        return data;
    }
}
