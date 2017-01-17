package com.wholdus.www.wholdusbuyerapp.dataSource;

import com.wholdus.www.wholdusbuyerapp.R;
import com.wholdus.www.wholdusbuyerapp.models.NavDrawerData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import static android.R.attr.data;
import static android.R.attr.key;

/**
 * Created by aditya on 16/11/16.
 */

public class NavigationDrawerData {
    public static List<NavDrawerData> getData() {
        List<NavDrawerData> data = new ArrayList<>();

        data.add(new NavDrawerData("Home", R.drawable.ic_home_black_24dp, new ArrayList<NavDrawerData>()));
        data.add(new NavDrawerData("Hand Picked For You", R.drawable.ic_style_black_24dp, new ArrayList<NavDrawerData>()));
        data.add(new NavDrawerData("Categories", R.drawable.ic_list_black_24dp, new ArrayList<NavDrawerData>()));
        data.add(new NavDrawerData("Shortlist", R.drawable.ic_favorite_black_24dp, new ArrayList<NavDrawerData>()));
        data.add(new NavDrawerData("Updates", R.drawable.ic_notifications_black_24dp, new ArrayList<NavDrawerData>()));

        List<NavDrawerData> myAccount = new ArrayList<>();
        myAccount.add(new NavDrawerData("My Profile", R.drawable.ic_person_black_24dp, null));
        myAccount.add(new NavDrawerData("My Address", R.drawable.ic_place_black_24dp, null));
        myAccount.add(new NavDrawerData("My Orders", R.drawable.ic_assignment_black_24dp, null));
        //myAccount.add(new NavDrawerData("My Preferences", R.drawable.ic_thumb_up_black_24dp, null));
        myAccount.add(new NavDrawerData("Rejected Products", R.drawable.ic_thumb_down_black_24dp, null));
        data.add(new NavDrawerData("My Account", R.drawable.ic_account_circle_black_24dp, myAccount));

        List<NavDrawerData> helpSupport = new ArrayList<>();
        helpSupport.add(new NavDrawerData("Contact Us", R.drawable.ic_contact_phone_black_24dp, null));
        helpSupport.add(new NavDrawerData("About Us", R.drawable.ic_people_black_24dp, null));
        helpSupport.add(new NavDrawerData("FAQs", R.drawable.ic_question_answer_black_24dp, null));
        helpSupport.add(new NavDrawerData("Return & Refund Policy", R.drawable.ic_assignment_return_black_24dp, null));
        helpSupport.add(new NavDrawerData("Privacy Policy", R.drawable.ic_visibility_off_black_24dp, null));
        data.add(new NavDrawerData("Help And Support", R.drawable.ic_help_black_24dp, helpSupport));

        data.add(new NavDrawerData("Logout", R.drawable.ic_exit_to_app_black_24dp, new ArrayList<NavDrawerData>()));

        return data;
    }
}
