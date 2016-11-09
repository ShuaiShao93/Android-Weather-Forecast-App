package com.usc.cs.shuaishao.mobileforecast;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.ContentHandler;
import java.net.URL;


public class ResultActivity extends AppCompatActivity {
    String json_str;
    JSONArray json = null;
    CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        FacebookSdk.sdkInitialize(getApplicationContext());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        json_str = getIntent().getStringExtra("json");
        try {
            json = new JSONArray(json_str);

            ImageView weather_img = (ImageView)findViewById(R.id.weather_img);
            JSONObject cur_json = json.getJSONObject(0);
            int img_id = getResources().getIdentifier(cur_json.getString("icon_url"), "drawable", getPackageName());
            weather_img.setImageResource(img_id);

            TextView summary = (TextView)findViewById(R.id.summary);
            summary.setText(cur_json.getString("summary")+" in "+cur_json.getString("location"));

            TextView temp = (TextView)findViewById(R.id.temp);
            temp.setText(cur_json.getString("temp"));

            TextView temp_units = (TextView)findViewById(R.id.temp_units);
            char units = (char)Integer.parseInt(cur_json.getString("temp_units").substring(2, cur_json.getString("temp_units").length() - 1));
            temp_units.setText(String.valueOf(units));

            TextView lh = (TextView)findViewById(R.id.low_high);
            lh.setText("L: "+cur_json.getString("minTemp") + "\u00B0 | H: " + cur_json.getString("maxTemp")+"\u00B0");

            TextView precipitation = (TextView)findViewById(R.id.precipitation);
            precipitation.setText(cur_json.getString("precipitation"));

            TextView chanceofrain = (TextView)findViewById(R.id.chanceofrain);
            chanceofrain.setText(cur_json.getString("chanceofRain"));

            TextView windspeed = (TextView)findViewById(R.id.windspeed);
            windspeed.setText(cur_json.getString("windSpeed"));

            TextView dewpoint = (TextView)findViewById(R.id.dewpoint);
            dewpoint.setText(cur_json.getString("dewPoint")+units);

            TextView humidity = (TextView)findViewById(R.id.humidity);
            humidity.setText(cur_json.getString("humidity"));

            TextView visibility = (TextView)findViewById(R.id.visibility);
            visibility.setText(cur_json.getString("visibility"));

            TextView sunrise = (TextView)findViewById(R.id.sunrise);
            sunrise.setText(cur_json.getString("Sunrise"));

            TextView sunset = (TextView)findViewById(R.id.sunset);
            sunset.setText(cur_json.getString("Sunset"));

        } catch (Exception e){
            e.printStackTrace();
        };

        Button details = (Button)findViewById(R.id.details);
        details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent int_details = new Intent();
                int_details.setClass(ResultActivity.this, DetailsActivity.class);
                int_details.putExtra("json",json_str);
                startActivity(int_details);
            }
        });

        Button map = (Button)findViewById(R.id.map);
        map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent int_map = new Intent();
                int_map.setClass(ResultActivity.this, MapActivity.class);
                int_map.putExtra("json", json_str);
                startActivity(int_map);
            }
        });

        ImageView fb = (ImageView)findViewById(R.id.fb_logo);
        fb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShareDialog shareDialog;
                callbackManager = CallbackManager.Factory.create();
                LoginManager.getInstance().logOut();
                String img_url, units;
                try{
                    char unit = (char)Integer.parseInt(json.getJSONObject(0).getString("temp_units").substring(2, json.getJSONObject(0).getString("temp_units").length() - 1));
                    units = String.valueOf(unit);

                    img_url = "http://cs-server.usc.edu:45678/hw/hw8/images/"+json.getJSONObject(0).getString("icon_url")+".png";
                    ShareLinkContent content = new ShareLinkContent.Builder()
                            .setImageUrl(Uri.parse(img_url))
                            .setContentTitle("Current Weather in "+json.getJSONObject(0).getString("location"))
                            .setContentDescription(json.getJSONObject(0).getString("summary")+", "+json.getJSONObject(0).getString("temp")+units)
                            .setContentUrl(Uri.parse("http://forecast.io"))
                            .build();
                    shareDialog = new ShareDialog(ResultActivity.this);
                    shareDialog.show(content);
                    shareDialog.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {
                        @Override
                        public void onSuccess(Sharer.Result result) {
                            String id = result.getPostId();
                            if (id != null){
                                Toast.makeText(ResultActivity.this, "Facebook Post Successful", Toast.LENGTH_LONG).show();
                            }
                            else{
                                Toast.makeText(ResultActivity.this, "Posted Cancelled", Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onCancel() {
                            Toast.makeText(ResultActivity.this, "Posted Cancelled", Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onError(FacebookException error) {
                            Toast.makeText(ResultActivity.this, "Posted Cancelled", Toast.LENGTH_LONG).show();
                        }
                    });
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }

    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
}
