package edu.cs.birzeit.weatherapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    EditText edtInput;
    ListView lst;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        edtInput = findViewById(R.id.edtInput);
        lst = findViewById(R.id.lst);
    }

    public void btn_getCityID_click(View view) {
        // Instantiate the RequestQueue.

        String url ="https://www.metaweather.com/api/location/search/?query=" + edtInput.getText();


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

                        edtInput.setText(cityID);
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error

                    }
                });
        MySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);

    }

    public void btn_weather_cityID_click(View view) {
        String url = "https://www.metaweather.com/api/location/" +edtInput.getText();


        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url,
                        null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        String[] days;
                        try {
                            JSONArray array = response.getJSONArray("consolidated_weather");
                            days = new String[array.length()];
                            for(int i = 0; i<array.length(); i++){
                                JSONObject obj = array.getJSONObject(i);
                                String weatherDay = "";
                                weatherDay = "state: " + obj.getString("weather_state_name") +
                                        "\n, date: " + obj.getString("applicable_date") +
                                        "\n, min: " + obj.getString("min_temp") +
                                        ", max: " + obj.getString("max_temp");
                                days[i] = weatherDay;
                            }
                            ArrayAdapter<String> itemsAdapter =
                                    new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1,
                                            days);
                            lst.setAdapter(itemsAdapter);
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


}
