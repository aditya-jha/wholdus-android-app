package com.wholdus.www.wholdusbuyerapp.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wholdus.www.wholdusbuyerapp.R;
import com.wholdus.www.wholdusbuyerapp.activities.HomeActivity;
import com.wholdus.www.wholdusbuyerapp.adapters.NotificationAdapter;
import com.wholdus.www.wholdusbuyerapp.decorators.RecyclerViewSpaceItemDecoration;
import com.wholdus.www.wholdusbuyerapp.interfaces.ItemClickListener;
import com.wholdus.www.wholdusbuyerapp.interfaces.NotificationListenerInterface;
import com.wholdus.www.wholdusbuyerapp.loaders.NotificationLoader;
import com.wholdus.www.wholdusbuyerapp.models.Notification;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;

/**
 * Created by kaustubh on 11/1/17.
 */

public class NotificationFragment extends Fragment
        implements ItemClickListener, LoaderManager.LoaderCallbacks<ArrayList<Notification>> {

    private NotificationListenerInterface mListener;
    private RecyclerView mNotificationsListView;
    private final int NOTIFICATIONS_DB_LOADER = 101;
    private ArrayList<Notification> mNotificationArrayList;
    private NotificationAdapter mNotificationAdapter;
    private CardView mNoNotifications;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mListener = (NotificationListenerInterface) context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mNotificationArrayList = new ArrayList<>();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_notifications, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mNoNotifications = (CardView) view.findViewById(R.id.no_notification);
        mNoNotifications.setVisibility(View.INVISIBLE);

        mNotificationsListView = (RecyclerView) view.findViewById(R.id.notifications_recycler_view);
        mNotificationsListView.setLayoutManager(new LinearLayoutManager(getContext()));
        mNotificationsListView.setItemAnimator(new DefaultItemAnimator());
        mNotificationsListView.addItemDecoration(new RecyclerViewSpaceItemDecoration(40, 0));
        mNotificationAdapter = new NotificationAdapter(getContext(), mNotificationArrayList, this);
        mNotificationsListView.setAdapter(mNotificationAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().getSupportLoaderManager().restartLoader(NOTIFICATIONS_DB_LOADER, null, this);
        mListener.fragmentCreated(getString(R.string.notification_key));
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private void setViewForNotifications(ArrayList<Notification> notifications) {
        if (notifications.size() > 0) {
            mNotificationArrayList.clear();
            mNotificationArrayList.addAll(notifications);
            mNotificationAdapter.notifyDataSetChanged();
        } else {
            mNoNotifications.setVisibility(View.VISIBLE);
            mNotificationsListView.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void itemClicked(View view, int position, int id) {
        Notification notification = mNotificationArrayList.get(position);
        Intent intent = new Intent(getContext(), HomeActivity.class);
        intent.setFlags(FLAG_ACTIVITY_CLEAR_TOP);
        try {
            Bundle bundle = new Bundle();
            JSONObject jsonObject = new JSONObject(notification.getNotificationJSON());
            Iterator<String> keys = jsonObject.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                bundle.putString(key, jsonObject.getString(key));
            }
            bundle.putString("notificationID", String.valueOf(notification.getID()));
            intent.putExtra("router", bundle);
        } catch (JSONException e) {

        }
        getContext().startActivity(intent);
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<Notification>> loader) {

    }

    @Override
    public void onLoadFinished(Loader<ArrayList<Notification>> loader, ArrayList<Notification> data) {
        if (data != null) {
            setViewForNotifications(data);
        }
    }

    @Override
    public Loader<ArrayList<Notification>> onCreateLoader(int id, Bundle args) {
        return new NotificationLoader(getContext(), -1, null);
    }
}
