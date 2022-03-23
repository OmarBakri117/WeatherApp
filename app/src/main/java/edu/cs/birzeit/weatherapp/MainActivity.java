package edu.cs.birzeit.weatherapp;

import static edu.cs.birzeit.weatherapp.R.drawable.ic_clear;
import static edu.cs.birzeit.weatherapp.R.drawable.ic_hail;
import static edu.cs.birzeit.weatherapp.R.drawable.ic_heavycloud;
import static edu.cs.birzeit.weatherapp.R.drawable.ic_heavyrain;
import static edu.cs.birzeit.weatherapp.R.drawable.ic_lightcloud;
import static edu.cs.birzeit.weatherapp.R.drawable.ic_lightrain;
import static edu.cs.birzeit.weatherapp.R.drawable.ic_showers;
import static edu.cs.birzeit.weatherapp.R.drawable.ic_sleet;
import static edu.cs.birzeit.weatherapp.R.drawable.ic_snow;
import static edu.cs.birzeit.weatherapp.R.drawable.ic_thund;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import Adapters.CaptionedImagesAdapter;

public class MainActivity extends AppCompatActivity {

    EditText edtInput;
    private static final int REQUEST_LOCATION = 1;
    RecyclerView recycler;
    CaptionedImagesAdapter daysAdapter;
    LocationManager locationManager ;
    String latitude, longitude;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//will hide the title
        getSupportActionBar().hide(); //hide the title bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN); //enable full screen
        setContentView(R.layout.activity_main);
        recycler = findViewById(R.id.myRecycler);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        ActivityCompat.requestPermissions( this,
                new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
                locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    OnGPS();
                } else {
                    getLocation();
                }
                findCityId();
    }


    private void OnGPS() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Enable GPS").setCancelable(false).setPositiveButton("Yes", new  DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(
                MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
        } else {
            Location locationGPS = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (locationGPS != null) {
                double lat = locationGPS.getLatitude();
                double longi = locationGPS.getLongitude();
                latitude = String.valueOf(lat);
                longitude = String.valueOf(longi);
                Toast.makeText(this, latitude+longitude, Toast.LENGTH_SHORT).show();

            } else {
                Toast.makeText(this, "Unable to find location.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void findCityId(){

        String url ="https://www.metaweather.com/api/location/search/?lattlong="+latitude+","+longitude;

        JsonArrayRequest jsonObjectRequest = new JsonArrayRequest
                (Request.Method.GET, url,
                        null, new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response) {
                        String cityID = "";
                        try {
                            JSONObject obj = response.getJSONObject(0);

                            cityID = obj.getString("woeid");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        if (cityID != ""){
                            GenerateMyWeather(cityID);
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error

                    }
                });
        MySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);

    }

    private void GenerateMyWeather(String cityID) {
        String url = "https://www.metaweather.com/api/location/" +cityID;


        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url,
                        null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        ArrayList<WeatherDay> daysResult = new ArrayList<>();
                        String[] days;
                        try {
                            JSONArray array = response.getJSONArray("consolidated_weather");
                            days = new String[array.length()];
                            for(int i = 0; i<array.length(); i++){
                                JSONObject obj = array.getJSONObject(i);
                                WeatherDay weatherDay = new WeatherDay(obj.getString("weather_state_name"),
                                        obj.getString("applicable_date"),
                                        obj.getString("min_temp"),
                                        obj.getString("max_temp"));
                                daysResult.add(weatherDay);
                            }
                            createTodaysView(daysResult.get(0));
                            daysResult.remove(0);
                            daysAdapter = new CaptionedImagesAdapter(daysResult);
                            recycler.setAdapter(daysAdapter);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error

                    }
                });
        MySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);
    }

    private void createTodaysView(WeatherDay weatherDay) {
        TextView dayDate = findViewById(R.id.dayDate);
        TextView minTemp = findViewById(R.id.minTemp);
        TextView maxTemp = findViewById(R.id.maxTemp);
        ImageView dayImage = findViewById(R.id.dayImage);
        TextView mystate = findViewById(R.id.state);

        mystate.setText(""+weatherDay.getWeather_state_name());
        dayDate.setText(" "+weatherDay.getApplicable_date());
        minTemp.setText(""+(int)Double.parseDouble(weatherDay.getMin_temp()));
        maxTemp.setText(""+(int)Double.parseDouble(weatherDay.getMax_temp()));
        String state =weatherDay.getWeather_state_name() ;
        if (state.equalsIgnoreCase("snow")){
            dayImage.setImageResource(ic_snow);
        }else if (state.equalsIgnoreCase("sleet")) {
            dayImage.setImageResource(ic_sleet);
        }else if (state.equalsIgnoreCase("hail")) {
            dayImage.setImageResource(ic_hail);
        }else if (state.equalsIgnoreCase("thunderstorm")) {
            dayImage.setImageResource(ic_thund);
        }else if (state.equalsIgnoreCase("heavy rain")) {
            dayImage.setImageResource(ic_heavyrain);
        }else if (state.equalsIgnoreCase("light rain")) {
            dayImage.setImageResource(ic_lightrain);
        }else if (state.equalsIgnoreCase("showers")) {
            dayImage.setImageResource(ic_showers);
        }else if (state.equalsIgnoreCase("heavy cloud")) {
            dayImage.setImageResource(ic_heavycloud);
        }else if (state.equalsIgnoreCase("light cloud")) {
            dayImage.setImageResource(ic_lightcloud);
        }else if (state.equalsIgnoreCase("clear")) {
            dayImage.setImageResource(ic_clear);
        }
    }
}
