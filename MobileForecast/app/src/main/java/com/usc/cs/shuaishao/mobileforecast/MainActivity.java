package com.usc.cs.shuaishao.mobileforecast;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Message;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.test.suitebuilder.annotation.Suppress;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.os.Handler;

import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.Console;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.logging.LogRecord;


public class MainActivity extends AppCompatActivity {
    String address_str, city_str, state_str, degree_str;
    StringBuffer json;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button search = (Button)findViewById(R.id.search);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String warn = "";
                EditText street = (EditText) findViewById(R.id.street);
                if (street.length() == 0) {
                    warn += "Please enter a Street.\n";
                }
                EditText city = (EditText) findViewById(R.id.city);
                if (city.length() == 0) {
                    warn += "Please enter a City.\n";
                }
                Spinner state = (Spinner) findViewById(R.id.state);
                if (state.getSelectedItem().toString().equals("Select")) {
                    warn += "Please select a State.\n";
                }
                TextView error = (TextView) findViewById(R.id.error);
                error.setText(warn);

                if (warn == ""){
                    address_str = URLEncoder.encode(street.getText().toString());
                    city_str = URLEncoder.encode(city.getText().toString());
                    state_str = URLEncoder.encode(state.getSelectedItem().toString());
                    RadioGroup degree_group = (RadioGroup)findViewById(R.id.degree);
                    RadioButton degree = (RadioButton)findViewById(degree_group.getCheckedRadioButtonId());
                    degree_str = URLEncoder.encode(degree.getText().toString());

                    new Thread(runnable).start();
                }
            }
        });

        Button clear = (Button) findViewById(R.id.clear);
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText street = (EditText) findViewById(R.id.street);
                street.setText("");
                EditText city = (EditText) findViewById(R.id.city);
                city.setText("");
                Spinner state = (Spinner) findViewById(R.id.state);
                state.setSelection(0);
                TextView error = (TextView) findViewById(R.id.error);
                error.setText("");
            }
        });

        ImageView logo = (ImageView) findViewById(R.id.logo);
        logo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("http://forecast.io");
                Intent web = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(web);
            }
        });

        Button about = (Button)findViewById(R.id.about);
        about.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent int_about = new Intent();
                int_about.setClass(MainActivity.this, AboutActivity.class);
                startActivity(int_about);
            }
        });
    }

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg){
            super.handleMessage(msg);
            Intent int_result = new Intent();
            int_result.setClass(MainActivity.this, ResultActivity.class);
            int_result.putExtra("json",json.toString());
            startActivity(int_result);
        }
    };

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            URL url = null;
            HttpURLConnection conn = null;
            InputStreamReader in = null;
            try {
                url = new URL("http://ss570hw-env.elasticbeanstalk.com/forecast_api.php?address=" + address_str + "&city=" + city_str + "&state=" + state_str + "&degree=" + degree_str);
                conn = (HttpURLConnection) url.openConnection();
                in = new InputStreamReader(conn.getInputStream());
                BufferedReader bufferedReader = new BufferedReader(in);
                json = new StringBuffer();
                for (String s = bufferedReader.readLine(); s != null; s = bufferedReader.readLine()) {
                    json.append(s);
                }
                System.out.print(json);
                handler.sendEmptyMessage(0);

            } catch (Exception e){
                e.printStackTrace();
            } finally {
                if (conn != null) {
                    conn.disconnect();
                }
                if (in != null) {
                    try {
                        in.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }
        }
    };

}
