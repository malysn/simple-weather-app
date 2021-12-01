package com.markjidon.weatherapp;

import static android.graphics.Color.BLACK;
import static android.graphics.Color.DKGRAY;
import static android.graphics.Color.GRAY;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.NetworkImageView;

import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    Button btn_getCityID, btn_getWeatherByID, btn_getWeatherByName, btn_resetListView;
    EditText et_dataInput;
    TextView tv_result, tv_weatherText, tv_cityName, tv_temps, degreeSymbol, tv_humidity, tv_visibility, tv_windSpeed, tv_pressure, tv_confidence, tv_tomorrow, tv_today;
//    ListView lv_weatherReport;
    ImageView img_weatherImage, iv_chevron;
    RelativeLayout rL_reportBody;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        btn_getCityID = findViewById(R.id.btn_getCityID);
        btn_getWeatherByID = findViewById(R.id.btn_getWeatherByCityID);
        btn_getWeatherByName = findViewById(R.id.btn_searchButton);
        btn_resetListView = findViewById(R.id.btn_resetListView);
        tv_result = findViewById(R.id.tv_result);
        tv_weatherText = findViewById(R.id.tv_weatherText);
        tv_cityName = findViewById(R.id.tv_cityName);
        tv_temps = findViewById(R.id.tv_temps);
        degreeSymbol = findViewById(R.id.degreeSymbol);
        tv_humidity = findViewById(R.id.tv_humidity);
        tv_visibility = findViewById(R.id.tv_visibility);
        tv_windSpeed = findViewById(R.id.tv_windSpeed);
        tv_pressure = findViewById(R.id.tv_pressure);
        tv_confidence = findViewById(R.id.tv_confidence);
        tv_tomorrow = findViewById(R.id.tv_tomorrow);
        tv_today = findViewById(R.id.tv_today);
        et_dataInput = findViewById(R.id.et_dataInput);
//        lv_weatherReport = findViewById(R.id.lv_weatherReports);
        img_weatherImage = findViewById(R.id.img_weatherImage);
        iv_chevron = findViewById(R.id.iv_chevron);
        rL_reportBody = findViewById(R.id.rL_reportBody);




        final WeatherDataService weatherDataService = new WeatherDataService(MainActivity.this);

        //Initial UI Placement
        tv_weatherText.setTranslationY(200);
        tv_cityName.setTranslationY(215);
        tv_temps.setTranslationY(260);
        degreeSymbol.setTranslationY(260);
        img_weatherImage.setTranslationY(300);
        rL_reportBody.setTranslationY(750);
        iv_chevron.setTranslationY(-750);
        iv_chevron.setRotation(-90);
        tv_today.setTranslationY(50);
        tv_tomorrow.setTranslationY(50);


        //Animations
        rL_reportBody.setOnClickListener(new View.OnClickListener() {
            String screenState = "collapsed";
            @Override
            public void onClick(View v) {

                rL_reportBody.setEnabled(false);

                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        // This method will be executed once the timer is over
                        rL_reportBody.setEnabled(true);

                    }
                },1600);// set time as per your requirement
                if (screenState == "collapsed") {
                    rL_reportBody.animate().translationYBy(-750).setDuration(1500).start();
                    tv_weatherText.animate().translationYBy(-200).setDuration(1500).start();
                    tv_cityName.animate().translationYBy(-215).setDuration(1500).start();
                    tv_temps.animate().translationYBy(-260).setDuration(1500).start();
                    degreeSymbol.animate().translationYBy(-260).setDuration(1500).start();
                    img_weatherImage.animate().translationYBy(-300).setDuration(1500).start();
                    iv_chevron.animate().translationYBy(750).setDuration(800).start();
                    iv_chevron.animate().rotation(90).setDuration(800).start();
                    tv_today.animate().translationYBy(-50).setDuration(1000).start();
                    tv_tomorrow.animate().translationYBy(-50).setDuration(1000).start();
                    screenState = "expanded";
                } else {
                    rL_reportBody.animate().translationYBy(750).setDuration(1500).start();
                    tv_weatherText.animate().translationYBy(200).setDuration(1500).start();
                    tv_cityName.animate().translationYBy(215).setDuration(1500).start();
                    tv_temps.animate().translationYBy(260).setDuration(1500).start();
                    degreeSymbol.animate().translationYBy(260).setDuration(1500).start();
                    img_weatherImage.animate().translationYBy(300).setDuration(1500).start();
                    iv_chevron.animate().translationYBy(-750).setDuration(800).start();
                    iv_chevron.animate().rotation(-90).setDuration(800).start();
                    tv_today.animate().translationYBy(50).setDuration(1000).start();
                    tv_tomorrow.animate().translationYBy(50).setDuration(1000).start();
                    screenState = "collapsed";
                }
                Log.i("RelativeLayout: ", "Clicked");
                Log.i("ScreenState", screenState);
            }
        });

        et_dataInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            String theDay = "today";
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    if (et_dataInput.getText().toString().length() > 2) {
                        et_dataInput.clearFocus();
                        InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        in.hideSoftInputFromWindow(et_dataInput.getWindowToken(), 0);
                        tv_result.setText(null);
                        //lv_weatherReport.setAdapter(null);

                        weatherDataService.getCityForecastByName(et_dataInput.getText().toString(), new WeatherDataService.GetCityForeCastByNameCallback() {
                            @Override
                            public void onError(String message) {
                                tv_result.setText("Location not found");
                                Toast.makeText(MainActivity.this, "Search something!", Toast.LENGTH_SHORT).show();
                            }

                            public void infoToday(List<WeatherReportModel> weatherReportModels) {
                                // Main Info
                                tv_weatherText.setText(weatherReportModels.get(0).getWeather_state_name());
                                int theTemp = (int) weatherReportModels.get(0).getThe_temp();
                                tv_temps.setText(Integer.toString(theTemp));
                                degreeSymbol.setVisibility(View.VISIBLE);


                                // Info
                                int theHumidity = (int) weatherReportModels.get(0).getHumidity();
                                tv_humidity.setText(Integer.toString(theHumidity) + "%");

                                long theVisibility = (long) weatherReportModels.get(0).getVisibility();
                                tv_visibility.setText(Long.toString(theVisibility) + " km");

                                long theWindSpeed = (long) weatherReportModels.get(0).getWind_speed();
                                tv_windSpeed.setText(Long.toString(theWindSpeed) + " kmh");

                                long thePressure = (long) weatherReportModels.get(0).getAir_pressure();
                                tv_pressure.setText(Long.toString(thePressure) + "mb");

                                int thePredictability = (int) weatherReportModels.get(0).getPredictability();
                                tv_confidence.setText(Integer.toString(thePredictability) + "%");

                                //ImageLoader
                                String weather_state_abbr = weatherReportModels.get(0).getWeather_state_abbr();
                                weatherDataService.getWeatherImage(weather_state_abbr);
                            }

                            public void infoTomorrow(List<WeatherReportModel> weatherReportModels) {
                                // Main Info
                                tv_weatherText.setText(weatherReportModels.get(1).getWeather_state_name());
                                int theTemp = (int) weatherReportModels.get(1).getThe_temp();
                                tv_temps.setText(Integer.toString(theTemp));
                                degreeSymbol.setVisibility(View.VISIBLE);

                                // Info
                                int theHumidity = (int) weatherReportModels.get(1).getHumidity();
                                tv_humidity.setText(Integer.toString(theHumidity) + "%");

                                long theVisibility = (long) weatherReportModels.get(1).getVisibility();
                                tv_visibility.setText(Long.toString(theVisibility) + " km");

                                long theWindSpeed = (long) weatherReportModels.get(1).getWind_speed();
                                tv_windSpeed.setText(Long.toString(theWindSpeed) + " kmh");

                                long thePressure = (long) weatherReportModels.get(1).getAir_pressure();
                                tv_pressure.setText(Long.toString(thePressure) + "mb");

                                int thePredictability = (int) weatherReportModels.get(1).getPredictability();
                                tv_confidence.setText(Integer.toString(thePredictability) + "%");

                                //ImageLoader
                                String weather_state_abbr = weatherReportModels.get(1).getWeather_state_abbr();
                                weatherDataService.getWeatherImage(weather_state_abbr);
                            }

                            @Override
                            public void onResponse(List<WeatherReportModel> weatherReportModels) {
                                infoToday(weatherReportModels);
                                tv_today.setTextColor(BLACK);
                                tv_tomorrow.setTextColor(GRAY);
                                tv_cityName.setText(et_dataInput.getText().toString().toUpperCase(Locale.ROOT));

                                tv_tomorrow.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        infoTomorrow(weatherReportModels);
                                        tv_tomorrow.setTextColor(BLACK);
                                        tv_today.setTextColor(GRAY);
                                    }
                                });

                                tv_today.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        infoToday(weatherReportModels);
                                        tv_tomorrow.setTextColor(GRAY);
                                        tv_today.setTextColor(BLACK);
                                    }

                                });

//                            //Put list into listview
//                            ArrayAdapter arrayAdapter = new ArrayAdapter(MainActivity.this, android.R.layout.simple_list_item_1, weatherReportModels);
//                            lv_weatherReport.setAdapter(arrayAdapter);
//                            arrayAdapter.notifyDataSetChanged();
                                tv_result.setText("Success");
                            }
                        });
                        return true;
                    } else {
                        et_dataInput.clearFocus();
                        InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        in.hideSoftInputFromWindow(et_dataInput.getWindowToken(), 0);
                        Toast.makeText(MainActivity.this, "Location not found", Toast.LENGTH_SHORT).show();
                    }
                }
                return false;
            }
        });


//        btn_resetListView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                weatherDataService.getCityForecastByName(et_dataInput.getText().toString(), new WeatherDataService.GetCityForeCastByNameCallback() {
//                    @Override
//                    public void onError(String message) {
//                    }
//
//                    @Override
//                    public void onResponse(List<WeatherReportModel> weatherReportModels) {
//                        tv_result.setText(null);
//                        lv_weatherReport.setAdapter(null);
//                        MySingleton.getInstance(MainActivity.this).getRequestQueue().getCache().clear();
//
//                    }
//                });
//            }
//        });

//        btn_getCityID.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                weatherDataService.getCityID(et_dataInput.getText().toString(), new WeatherDataService.VolleyResponseListener() {
//                    @Override
//                    public void onError(String message) {
//                        tv_result.setText("Something wrong");
//                        //Toast.makeText(MainActivity.this, "Something wrong", Toast.LENGTH_SHORT).show();
//                    }
//
//                    @Override
//                    public void onResponse(String cityID) {
//                        //Toast.makeText(MainActivity.this, "Return City ID: " + cityID, Toast.LENGTH_SHORT).show();
//                        if (cityID == null) {
//                            tv_result.setText("Something wrong");
//                        } else {
//                            tv_result.setText("City ID: " + cityID);
//                        }
//                    }
//                });
//            }
//        });
//
//        btn_getWeatherByID.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                weatherDataService.getCityForecastByID(et_dataInput.getText().toString(), new WeatherDataService.ForecastByIDResponse() {
//                    @Override
//                    public void onError(String message) {
//                        tv_result.setText("Something wrong");
//                    }
//
//                    @Override
//                    public void onResponse(List<WeatherReportModel> weatherReportModels) {
//                        //Put list into listview
//                        ArrayAdapter arrayAdapter = new ArrayAdapter(MainActivity.this, android.R.layout.simple_list_item_1, weatherReportModels);
//                        lv_weatherReport.setAdapter(arrayAdapter);
//                        tv_result.setText("Success");
//
//                    }
//                });
//            }
//        });
//
//        btn_getWeatherByName.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                weatherDataService.getCityForecastByName(et_dataInput.getText().toString(), new WeatherDataService.GetCityForeCastByNameCallback() {
//                    @Override
//                    public void onError(String message) {
//                        tv_result.setText("Something wrong");
//                        Toast.makeText(MainActivity.this, "Something wrong", Toast.LENGTH_SHORT).show();
//                    }
//
//                    @Override
//                    public void onResponse(List<WeatherReportModel> weatherReportModels) {
//                        //Put list into listview
//                        ArrayAdapter arrayAdapter = new ArrayAdapter(MainActivity.this, android.R.layout.simple_list_item_1, weatherReportModels);
//                        lv_weatherReport.setAdapter(arrayAdapter);
//                        tv_result.setText("Success");
//                    }
//                });
//            }
//        });
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

}