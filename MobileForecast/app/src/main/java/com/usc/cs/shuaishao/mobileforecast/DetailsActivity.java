package com.usc.cs.shuaishao.mobileforecast;

import android.app.ActionBar;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.opengl.Visibility;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.lang.reflect.Type;

public class DetailsActivity extends AppCompatActivity {
    JSONArray json = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        String json_str = getIntent().getStringExtra("json");
        try {
            json = new JSONArray(json_str);

            TextView title = (TextView)findViewById(R.id.title);
            title.setText("More Details for " + json.getJSONObject(0).getString("location"));

            JSONObject hourly_json;

            for (int h = 0; h < 48; h ++) {
                hourly_json = json.getJSONArray(1).getJSONObject(h);

                TextView temp_title = (TextView) findViewById(R.id.temp);
                char units = (char) Integer.parseInt(hourly_json.getString("temp_units").substring(2, hourly_json.getString("temp_units").length() - 1));
                temp_title.setText("Temp(" + units + ")");

                TableRow row = new TableRow(this);
                row.setPadding(0, 0, 0, 10);
                if (h%2 == 0){
                    row.setBackgroundColor(Color.rgb(203,203,204));
                }

                TextView time = new TextView(this);
                time.setText(hourly_json.getString("time"));
                time.setTextSize(15);
                time.setTypeface(null, Typeface.BOLD);
                TableRow.LayoutParams lp_time = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
                lp_time.leftMargin = 10;
                row.addView(time,lp_time);

                ImageView summary = new ImageView(this);
                Integer logo_id = getResources().getIdentifier(hourly_json.getString("icon_url"), "drawable", getPackageName());
                summary.setImageResource(logo_id);
                TableRow.LayoutParams lp_summary = new TableRow.LayoutParams(500, 100);
                row.addView(summary, lp_summary);

                TextView temp = new TextView(this);
                temp.setText(hourly_json.getString("temp"));
                temp.setGravity(Gravity.RIGHT);
                temp.setTextSize(15);
                temp.setPadding(0,0,20,0);
                TableRow.LayoutParams lp_temp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
                lp_temp.weight = 1;
                row.addView(temp, lp_temp);

                TableLayout tableLayout = (TableLayout) findViewById(R.id.hourly_table);
                tableLayout.addView(row);
            }

            JSONObject daily_json;

            for (int d = 1; d<=7; d++){
                daily_json = json.getJSONArray(2).getJSONObject(d);

                TextView date = (TextView)findViewById(getResources().getIdentifier("d" + d + "_date", "id", getPackageName()));
                date.setText(daily_json.getString("weekday") + ", " + daily_json.getString("date"));

                char units = (char)Integer.parseInt(json.getJSONObject(0).getString("temp_units").substring(2, json.getJSONObject(0).getString("temp_units").length() - 1));

                TextView lh = (TextView)findViewById(getResources().getIdentifier("d"+d+"_lh", "id", getPackageName()));
                lh.setText("Min: "+ daily_json.getString("minTemp") + units + " | " + "Max: " + daily_json.getString("maxTemp") + units);

                ImageView logo = (ImageView)findViewById(getResources().getIdentifier("d" + d + "_logo", "id", getPackageName()));
                logo.setImageResource(getResources().getIdentifier(daily_json.getString("icon_url"), "drawable", getPackageName()));
            }
        } catch (Exception e){
            e.printStackTrace();
        }

        final Button hourly = (Button) findViewById(R.id.hourly);
        final Button daily = (Button) findViewById(R.id.daily);

        hourly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout daily_table = (LinearLayout)findViewById(R.id.daily_table);
                daily_table.setVisibility(View.GONE);
                TableLayout hourly_table = (TableLayout)findViewById(R.id.hourly_table);
                hourly_table.setVisibility(View.VISIBLE);
                hourly.setBackgroundTintList(ColorStateList.valueOf(Color.rgb(134, 174, 212)));
                daily.setBackgroundTintList(ColorStateList.valueOf(Color.rgb(220, 220, 220)));
            }
        });


        daily.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TableLayout hourly_table = (TableLayout) findViewById(R.id.hourly_table);
                hourly_table.setVisibility(View.GONE);
                LinearLayout daily_table = (LinearLayout) findViewById(R.id.daily_table);
                daily_table.setVisibility(View.VISIBLE);
                daily.setBackgroundTintList(ColorStateList.valueOf(Color.rgb(134, 174, 212)));
                hourly.setBackgroundTintList(ColorStateList.valueOf(Color.rgb(220,220,220)));
            }
        });

        hourly.performClick();
    }
}
