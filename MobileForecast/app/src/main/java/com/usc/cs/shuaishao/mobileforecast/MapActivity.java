package com.usc.cs.shuaishao.mobileforecast;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hamweather.aeris.communication.AerisCallback;
import com.hamweather.aeris.communication.AerisEngine;
import com.hamweather.aeris.communication.EndpointType;
import com.hamweather.aeris.communication.loaders.ObservationsTask;
import com.hamweather.aeris.communication.loaders.ObservationsTaskCallback;
import com.hamweather.aeris.communication.parameter.PlaceParameter;
import com.hamweather.aeris.maps.AerisMapView;
import com.hamweather.aeris.maps.MapViewFragment;
import com.hamweather.aeris.maps.interfaces.OnAerisMapLongClickListener;
import com.hamweather.aeris.model.AerisError;
import com.hamweather.aeris.model.AerisResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

public class MapActivity extends AppCompatActivity {
    double longitude, latitude;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);





        FragmentManager manager=getFragmentManager();
        FragmentTransaction transaction=manager.beginTransaction();
        MapFragment map=new MapFragment();
        transaction.add(R.id.FrameLayout, map);
        transaction.commit();


    }
}




