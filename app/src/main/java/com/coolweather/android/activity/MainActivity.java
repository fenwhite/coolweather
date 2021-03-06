package com.coolweather.android.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.coolweather.android.R;
import com.coolweather.android.gson.Forecast;
import com.coolweather.android.gson.Weather;
import com.coolweather.android.util.HttpUtil;
import com.coolweather.android.util.Utility;
import com.coolweather.android.view.CircleProgressView;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private ScrollView weatherLayout;
    private ImageView bg;
    private TextView degree, elseInfo,regionName,updateTime;
    private LinearLayout forecastLayout;
    private TextView comfBrf,comfTxt;
    private TextView carwashBrf,carwashTxt;
    private TextView sportBrf,sportTxt;
    private CircleProgressView aqi;
    private CircleProgressView pm25;
    private Button chooseArea;

    public SwipeRefreshLayout swipeRefresh;
    public DrawerLayout drawer;

    // 当前页面请求weather id
    private String weatherIdtmp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(Build.VERSION.SDK_INT >= 30){
            getWindow().getDecorView().getWindowInsetsController().hide(
                    android.view.WindowInsets.Type.statusBars()
                            | android.view.WindowInsets.Type.navigationBars()
            );
        }else if(Build.VERSION.SDK_INT >= 16){
            View decorView = getWindow().getDecorView();
            // Hide the status bar.
            int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
        }
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
        aqi = (CircleProgressView)findViewById(R.id.aqi_progress);
        pm25 = (CircleProgressView)findViewById(R.id.pm25_progress);
        bg = (ImageView)findViewById(R.id.random_bg);
        chooseArea = (Button)findViewById(R.id.choose_area_button);
        drawer = (DrawerLayout)findViewById(R.id.drawer_layout);
        swipeRefresh = (SwipeRefreshLayout)findViewById(R.id.swipe_refresh);

        SharedPreferences prefs = getPreferences(Context.MODE_PRIVATE);
        String weatherString =prefs.getString("weather",null);
        if(weatherString!=null){
            Weather weather = Utility.handleWeatherResponse(weatherString);
            weatherIdtmp = weather.basic.weatherId;
            showWeather(weather);
        }else{
            weatherLayout.setVisibility(View.INVISIBLE);
            requestWeather("CN101210107");
        }
        String picUrl = prefs.getString("pic",null);
        if(picUrl!=null && isToday(prefs.getString("picTime","1998-11-10"))){
            Glide.with(this).load(picUrl).into(bg);
        }else{
            loadPic();
        }
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestWeather(weatherIdtmp);
            }
        });
        chooseArea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.openDrawer(findViewById(R.id.choose_area));
            }
        });
    }

    public void requestWeather(final String weatherId){
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
                        swipeRefresh.setRefreshing(false);
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
                            weatherIdtmp = weather.basic.weatherId;
                            showWeather(weather);
                        }else{
                            Toast.makeText(MainActivity.this,"获取天气信息失败",Toast.LENGTH_SHORT).show();
                        }
                        swipeRefresh.setRefreshing(false);
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
        aqi.startAnimProgress(Integer.valueOf(weather.aqi.city.aqi),5000);
        aqi.setText(weather.aqi.city.qlty);
        pm25.startAnimProgress(Integer.valueOf(weather.aqi.city.pm25),5000);
        pm25.setText(weather.aqi.city.qlty);
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

    private void loadPic(){
        String apiPic = "https://api.btstu.cn/sjbz/api.php?method=mobile&lx=dongman&format=json";
        HttpUtil.requestGet(apiPic, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                final String picUrl = Utility.handleAnimePic(response.body().string());
                Log.d(TAG, "picture url:"+picUrl);
                SharedPreferences.Editor editor = getPreferences(Context.MODE_PRIVATE).edit();
                editor.putString("pic",picUrl);
                Date date = new Date();
                SimpleDateFormat format = new SimpleDateFormat("YYYY-MM-DD");
                String today = format.format(date);
                editor.putString("picTime",today);
                editor.apply();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.with(MainActivity.this).load(picUrl).into(bg);
                    }
                });
            }
        });
    }

    private String dateFormat(String date){
        StringBuffer tmp = new StringBuffer(date);
        tmp.setCharAt(4,'年');
        tmp.setCharAt(7,'月');
        tmp.append('日');
        return tmp.toString();
    }

    /**
     * 判断时间是否为今天
     * @param time YYYY-MM-DD
     */
    private boolean isToday(String time){
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("YYYY-MM-DD");
        String today = format.format(date);
        return today.equals(time);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(aqi!=null){
            aqi.destroy();
        }
    }
}