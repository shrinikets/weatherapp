package com.crunchypebble.weather.weatherapp;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;


import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;

import static java.nio.file.Paths.get;


public class MainActivity extends AppCompatActivity {
    EditText txtZip;
    EditText txtCity;
    Spinner spinner;
    Spinner spinner2;
    Spinner spinner3;
    Button btnSearch;
    String url = "http://api.openweathermap.org/data/2.5/weather?zip=";
    String city_url = "api.openweathermap.org/data/2.5/weather?q=";
    String zip_url = "https://www.zipcodeapi.com/rest/";
    String api_key = "834b8a2ae3f2d4b9c399ad64f8fd371b";
    String zip_api_key = "PQ8rQknxqIxDXSZoG7PX6tMsmN052K5a6EAzsx15OiRvnL2hj0VOmWQ33Svm4xc0";
    RequestQueue requestQueue;
    RequestQueue requestQueueCity;
    String tempUnit;
    String full_api_url;
    String zip_full_url;

    String zipcodeFromApi;

    String txtCityText;
    String txtZipText;
    String txtCCText;

    TextView txtLocation;
    TextView txtTemp;
    TextView txtSky;
    TextView txtHigh;
    TextView txtLow;

    ImageView imageView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.txtZip = (EditText) findViewById(R.id.txtZip);
        this.txtCity = (EditText) findViewById(R.id.txtCity);
        this.btnSearch = (Button) findViewById(R.id.btnSearch);

        this.txtLocation = (TextView) findViewById(R.id.txtLocation);
        this.txtTemp = (TextView) findViewById(R.id.txtTemp);
        this.txtSky = (TextView) findViewById(R.id.txtSky);
        this.txtHigh = (TextView) findViewById(R.id.txtHigh);
        this.txtLow = (TextView) findViewById(R.id.txtLow);


        this.imageView = (ImageView) findViewById(R.id.imageView);

        this.spinner = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.countries, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        this.spinner2 = (Spinner) findViewById(R.id.spinner2);
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this,
                R.array.states, android.R.layout.simple_spinner_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner2.setAdapter(adapter2);

        this.spinner3 = (Spinner) findViewById(R.id.TempUnit);
        ArrayAdapter<CharSequence> adapter3 = ArrayAdapter.createFromResource(this,
                R.array.temp_unit, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner3.setAdapter(adapter3);

        requestQueue = Volley.newRequestQueue(this);
        requestQueueCity = Volley.newRequestQueue(this);

    }

    public void search(View v) {
        String txtZipText = String.valueOf(txtZip.getText());
        txtZipText = txtZipText.trim();
        String txtCCText = String.valueOf(spinner.getSelectedItem());
        txtCCText = txtCCText.trim();
        String txtCityText = String.valueOf(txtCity.getText());
        txtCityText = txtCityText.trim();
        String txtStateText = String.valueOf(spinner2.getSelectedItem());
        txtStateText = txtStateText.trim();
        String txtUnit = String.valueOf(spinner3.getSelectedItem());
        txtUnit.trim();

        if(txtUnit.equals("Celsius")) {
            tempUnit = "metric";
        }
        else if(txtUnit.equals("Fahrenheit")) {
            tempUnit = "imperial";
        }
        else{
            tempUnit = "imperial";
        }

        if(!txtZipText.equals("") && txtCityText.equals("")) {
            full_api_url = url + txtZipText + ',' + txtCCText + "&mode=json&units=" + tempUnit + "&APPID=" + api_key;
            new myAsyncTask().execute(full_api_url);
        }
        else if(!txtCityText.equals("") && txtZipText.equals("")) {
            full_api_url = city_url + txtCityText + ',' + txtCCText + "&mode=json&units=" + tempUnit + "&APPID=" + api_key;
            new myAsyncTask().execute(full_api_url);
        }
        else if(!txtCityText.equals("") && !txtZipText.equals("")) {
            full_api_url = url + txtZipText + ',' + txtCCText + "&mode=json&units=" + tempUnit + "&APPID=" + api_key;
            new myAsyncTask().execute(full_api_url);
        }
        else {
            Toast.makeText(MainActivity.this, "Not enough information.", Toast.LENGTH_LONG).show();
            txtLocation.setText("");
            txtTemp.setText("");
            txtSky.setText("");
            txtHigh.setText("");
            txtLow.setText("");
            imageView.setImageResource(android.R.color.transparent);
        }
    }




    public class myAsyncTask extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... url) {
            JsonObjectRequest arrReq = new JsonObjectRequest(url[0], null, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {
                    try {

                        JSONObject jsonObj = response;
                        String city = jsonObj.get("name").toString();

                        JSONObject sys = (JSONObject) jsonObj.get("sys");
                        String country = sys.get("country").toString();

                        JSONObject main = (JSONObject) jsonObj.get("main");
                        String temp = main.get("temp").toString();
                        String temp_max = main.get("temp_max").toString();
                        String temp_min = main.get("temp_min").toString();

                        JSONArray weather = (JSONArray) jsonObj.get("weather");
                        JSONObject objSky = (JSONObject) weather.get(0);

                        String sky = objSky.get("main").toString();

                        displayWeather(city, country, temp, temp_max, temp_min, sky);



                    } catch (JSONException e) {
                        Log.e("Volley", "Invalid JSON Object.");

                    }

                }
            }, new Response.ErrorListener(){

                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(MainActivity.this, "Unable to fetch data.", Toast.LENGTH_LONG).show();
                }
            });
            requestQueue.add(arrReq);

            String done = "Done!";

            return done;
        }

        protected void onPostExecute(String done){
            Toast.makeText(MainActivity.this, done, Toast.LENGTH_LONG).show();
        }




        private void displayWeather(String city, String country, String temp, String temp_max, String temp_min, String sky){


            String location = city + ", " + country;
            txtLocation.setText(location);

            String textForTemp = temp + (char) 0x00B0;
            txtTemp.setText(textForTemp);

            if(sky.equals("Clouds")) {
                sky = "Cloudy";
                txtSky.setText(sky);
            }
            else if(sky.equals("Mist")) {
                sky = "Light Rain";
                txtSky.setText(sky);
            }
            else {
                txtSky.setText(sky);
            }



            String max = "High - " + temp_max;
            txtHigh.setText(max);

            String min = "Low - " + temp_min;
            txtLow.setText(min);

            sky.trim();



            if(sky.equals("Cloudy")) {
                imageView.setImageResource(R.drawable.cloudy);
            }
            else if(sky.equals("Clear")) {
                imageView.setImageResource(R.drawable.sunny);
            }

            else if(sky.equals("Light Rain")) {
                imageView.setImageResource(R.drawable.mist);
            }
            else if(sky.equals("Rain")) {
                imageView.setImageResource(R.drawable.rainy);
            }
            else if(sky.equals("Thunderstorm")) {
                imageView.setImageResource(R.drawable.thunderstorm);
            }
            else if(sky.equals("Haze")) {
                imageView.setImageResource(R.drawable.cloudy);
            }
            else{
                imageView.setImageResource(R.drawable.partly_cloudy);
            }


        }



    }
}
