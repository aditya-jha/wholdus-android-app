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

        ArrayList<String> store = new ArrayList<>();
        store.add("Store Home");
        store.add("Store Products");
        store.add("Purchase Requests");
        data.put("My Store", store);

        ArrayList<String> account = new ArrayList<>();
        account.add("My Profile");
        account.add("My Orders");
        account.add("My Preferences");
        account.add("Rejected Products");
        data.put("My Account", account);

        ArrayList<String> helpSupport = new ArrayList<>();
        helpSupport.add("FAQs");
        helpSupport.add("Contact Us");
        helpSupport.add("Policies");
        helpSupport.add("About Us");
        data.put("Help And Support", helpSupport);

        data.put("Notifications", new ArrayList<String>());
        data.put("Logout", new ArrayList<String>());

        return data;
    }
}
