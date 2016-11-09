package com.usc.cs.shuaishao.mobileforecast;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
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
import com.hamweather.aeris.tiles.AerisTile;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;


public class MapFragment extends MapViewFragment
{

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        String json_str = getActivity().getIntent().getStringExtra("json");
        double longitude=0, latitude=0;
        try {
            JSONArray json = new JSONArray(json_str);
            JSONObject loc = json.getJSONObject(3);
            longitude = Double.parseDouble(loc.getString("longitude"));
            latitude = Double.parseDouble(loc.getString("latitude"));
        }catch (Exception e){
            e.printStackTrace();
        }

        AerisEngine.initWithKeys(this.getString(R.string.aeris_client_id), this.getString(R.string.aeris_client_secret), getActivity());

        PlaceParameter place = new PlaceParameter(latitude, longitude);

        ObservationsTask task = new ObservationsTask(getActivity(),
                new ObservationsTaskCallback() {

                    @Override
                    public void onObservationsFailed(AerisError error) {
                        // handle fail here
                    }

                    @Override
                    public void onObservationsLoaded(List responses) {
                        // handle successful loading here.
                    }

                });
        task.requestClosest(place);

        View view=inflater.inflate(R.layout.fragment_map,container,false);
        mapView=(AerisMapView)view.findViewById(R.id.aerisfragment_map);
        mapView.init(savedInstanceState, AerisMapView.AerisMapType.GOOGLE);
        mapView.addLayer(AerisTile.RADSAT);
        LatLng latlng = new LatLng(latitude, longitude);
        mapView.moveToLocation(latlng, 10);

        return view;
    }

}