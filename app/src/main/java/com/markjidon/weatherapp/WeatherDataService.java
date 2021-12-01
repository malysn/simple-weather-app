package com.markjidon.weatherapp;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Cache;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

public class WeatherDataService {

    public static final String QUERY_FOR_CITY_ID = "https://www.metaweather.com/api/location/search/?query=";
    public static final String QUERY_FOR_CITY_WEATHER_BY_ID = "https://www.metaweather.com/api/location/";
    public static final String QUERY_FOR_CITY_WEATHERIMAGE = "https://www.metaweather.com/static/img/weather/png/";

    Context context;
    String cityID;


    public WeatherDataService(Context context) {
        this.context = context;
    }


    public void getWeatherImage(String weather_state_abbr) {
        String url = QUERY_FOR_CITY_WEATHERIMAGE + weather_state_abbr + ".png";

        ImageRequest imageRequest = new ImageRequest(url, new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap response) {
                ImageView img_weatherImage = (ImageView) ((Activity) context).findViewById(R.id.img_weatherImage);
                img_weatherImage.setImageBitmap(response);
            }
        }, 150, 150, Bitmap.Config.ARGB_8888, null);
        MySingleton.getInstance(context).addToRequestQueue(imageRequest);
    }


    public interface VolleyResponseListener {
        void onError(String message);

        void onResponse(String cityID);
    }

    public void getCityID(String cityName, VolleyResponseListener volleyResponseListener) {
        String url = QUERY_FOR_CITY_ID + cityName;
        TextView tv_result = (TextView) ((Activity) context).findViewById(R.id.tv_result);

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    JSONObject cityInfo = response.getJSONObject(0);
                    cityID = cityInfo.getString("woeid");
                    volleyResponseListener.onResponse(cityID);

                } catch (JSONException e) {
                    tv_result.setText("Location not found");
                    Toast.makeText(context, "Location not found", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();

                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Toast.makeText(context, "Error occurred", Toast.LENGTH_SHORT).show();
                volleyResponseListener.onError("Something wrong");
            }
        });
        MySingleton.getInstance(context).addToRequestQueue(request);
        Log.i("Button 1", "Clicked");
    }

    public interface ForecastByIDResponse {
        void onError(String message);

        void onResponse(List<WeatherReportModel   > weatherReportModels);
    }

    public void getCityForecastByID(String cityID, ForecastByIDResponse forecastByIDResponse) {
        List<WeatherReportModel> weatherReportModels = new ArrayList<>();
        String url = QUERY_FOR_CITY_WEATHER_BY_ID + cityID;
        //Get JSON Object
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray consolidated_weather_list = response.getJSONArray("consolidated_weather");

                    for(int i = 0; i < 2; i++) {
                        WeatherReportModel one_day = new WeatherReportModel();

                        JSONObject first_day_from_api = (JSONObject) consolidated_weather_list.get(i);
                        one_day.setWeather_state_name(first_day_from_api.getString("weather_state_name"));
                        one_day.setApplicable_date(first_day_from_api.getString("applicable_date"));
                        one_day.setWeather_state_abbr(first_day_from_api.getString("weather_state_abbr"));
//                        one_day.setMin_temp(first_day_from_api.getLong("min_temp"));
//                        one_day.setMax_temp(first_day_from_api.getLong("max_temp"));
                        one_day.setThe_temp(first_day_from_api.getLong("the_temp"));
                        one_day.setWind_speed(first_day_from_api.getLong("wind_speed"));
                        one_day.setAir_pressure(first_day_from_api.getLong("air_pressure"));
                        one_day.setHumidity(first_day_from_api.getInt("humidity"));
                        one_day.setVisibility(first_day_from_api.getLong("visibility"));
                        one_day.setPredictability(first_day_from_api.getInt("predictability"));
                        weatherReportModels.add(one_day);
                    }

                    forecastByIDResponse.onResponse(weatherReportModels);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                  //  Toast.makeText(context, "Error occurred", Toast.LENGTH_SHORT).show();
                    forecastByIDResponse.onError("Something wrong");
                }
            });

            //Get Property called "Consolidated Weather" which is an array

        //Get each item in the array and assign it to a new WeatherReportModel object
        MySingleton.getInstance(context).addToRequestQueue(request);

    }

    public interface GetCityForeCastByNameCallback {
        void onError(String message);
        void onResponse(List<WeatherReportModel> weatherReportModels);
    }

    public void getCityForecastByName(String cityName, GetCityForeCastByNameCallback getCityForeCastByNameCallback) {
        //Fetch the City ID given City Name
        getCityID(cityName, new VolleyResponseListener() {
            @Override
            public void onError(String message)  {
                getCityForeCastByNameCallback.onError("Something wrong");
               // Toast.makeText(context, "Error occurred", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(String cityID) {
                //City ID Found
                getCityForecastByID(cityID, new ForecastByIDResponse() {
                    @Override
                    public void onError(String message) {
                        getCityForeCastByNameCallback.onError("Something wrong");
                       // Toast.makeText(context, "Error occurred", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(List<WeatherReportModel> weatherReportModels) {
                        //Weather Report Created
                        getCityForeCastByNameCallback.onResponse(weatherReportModels);
                    }
                });
            }
        });
    }
}
