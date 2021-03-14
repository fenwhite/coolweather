package com.coolweather.android.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.coolweather.android.R;
import com.coolweather.android.gson.Forecast;
import com.coolweather.android.gson.Weather;
import com.coolweather.android.util.HttpUtil;
import com.coolweather.android.util.Utility;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private ScrollView weatherLayout;
    private TextView degree, elseInfo,regionName,updateTime;
    private LinearLayout forecastLayout;
    private TextView comfBrf,comfTxt;
    private TextView carwashBrf,carwashTxt;
    private TextView sportBrf,sportTxt;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //init
        weatherLayout = (ScrollView)findViewById(R.id.weather_layout);
        degree = (TextView)findViewById(R.id.now_degree);
        elseInfo = (TextView)findViewById(R.id.else_info);
        regionName = (TextView)findViewById(R.id.region_name);
        updateTime = (TextView)findViewById(R.id.update_time);
        forecastLayout = (LinearLayout)findViewById(R.id.forecast_layout);
        comfBrf = (TextView)findViewById(R.id.comf_brf);
        comfTxt = (TextView)findViewById(R.id.comf_txt);
        carwashBrf = (TextView)findViewById(R.id.carwash_brf);
        carwashTxt = (TextView)findViewById(R.id.carwash_txt);
        sportBrf = (TextView)findViewById(R.id.sport_brf);
        sportTxt = (TextView)findViewById(R.id.sport_txt);

        SharedPreferences prefs = getPreferences(Context.MODE_PRIVATE);
        String weatherString =prefs.getString("weather",null);
        if(weatherString!=null){
            Weather weather = Utility.handleWeatherResponse(weatherString);
            showWeather(weather);
        }else{
            // todo change weatherId
            weatherLayout.setVisibility(View.INVISIBLE);
            requestWeather("CN101210107");
        }
    }

    private void requestWeather(final String weatherId){
        String url = "http://guolin.tech/api/weather?cityid="+weatherId;
        Log.d(TAG, "request url is "+url);
        HttpUtil.requestGet(url, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this,"获取天气信息失败",Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                final String responseText = response.body().string();
                final Weather weather = Utility.handleWeatherResponse(responseText);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(weather!=null && "ok".equals(weather.status)){
                            SharedPreferences.Editor editor = getPreferences(Context.MODE_PRIVATE).edit();
                            editor.putString("weather",responseText);
                            editor.apply();
                            showWeather(weather);
                        }else{
                            Toast.makeText(MainActivity.this,"获取天气信息失败",Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });
    }

    private void showWeather(Weather weather){
        degree.setText(weather.now.temperature);
        elseInfo.setText(weather.now.weather+"    "+weather.now.windDir);
        regionName.setText(weather.basic.regionName);
        String oriTime = weather.basic.update.updateTime.split(" ")[0];
        updateTime.setText(dateFormat(oriTime));
        comfBrf.setText(weather.suggestion.comfort.suit);
        comfTxt.setText(weather.suggestion.comfort.info);
        carwashBrf.setText(weather.suggestion.carWash.suit);
        carwashTxt.setText(weather.suggestion.carWash.info);
        sportBrf.setText(weather.suggestion.sport.suit);
        sportTxt.setText(weather.suggestion.sport.info);
        forecastLayout.removeAllViews();
        for (Forecast forecast: weather.forecastList) {
            View view = LayoutInflater.from(this).inflate(R.layout.forecast_item,forecastLayout,false);
            TextView dateText = (TextView)view.findViewById(R.id.date_text);
            TextView infoText = (TextView)view.findViewById(R.id.info_text);
            TextView temperatureText = (TextView)view.findViewById(R.id.temperature_text);
            dateText.setText(dateFormat(forecast.date));
            infoText.setText(forecast.more.info);
            temperatureText.setText(forecast.temperature.max+"℃/"+forecast.temperature.min+"℃");
            forecastLayout.addView(view);
        }
        weatherLayout.setVisibility(View.VISIBLE);
    }

    private String dateFormat(String date){
        Log.d(TAG, "date is "+date);
        StringBuffer tmp = new StringBuffer(date);
        tmp.setCharAt(4,'年');
        tmp.setCharAt(7,'月');
        tmp.append('日');
        return tmp.toString();
    }
}