package com.itonyb.helpmessage.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.itonyb.helpmessage.R;
import com.itonyb.helpmessage.model.WifiAdmin;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RescueListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */

/**
 * Rescue List Fragment with the RecyclerView that shows all
 * the RescueNets on the range
 */
public class RescueListFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String CONNECTED_WIFI = "connectedWifi";

    // TODO: Rename and change types of parameters
    private WifiAdmin wifiAdmin;

    public RescueListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment RescueListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RescueListFragment newInstance(WifiAdmin wifiAdm) {
        RescueListFragment fragment = new RescueListFragment();
        Bundle args = new Bundle();
        fragment.setWifiAdmin(wifiAdm);
        fragment.setArguments(args);
        return fragment;
    }

    private void setWifiAdmin(WifiAdmin wifiAdm){
        wifiAdmin = wifiAdm;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View ret = inflater.inflate(R.layout.fragment_rescue_list, container, false);
        if(wifiAdmin == null){
            wifiAdmin = new WifiAdmin(ret.getContext());
        }

        RecyclerView recyclerView = (RecyclerView)ret.findViewById(R.id.wifi_recycler);
        recyclerView.setHasFixedSize(false);

        recyclerView.setAdapter(wifiAdmin);

        LinearLayoutManager layoutManager = new LinearLayoutManager(ret.getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        return ret;
    }

}
