package com.lxy.coolweather;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.lxy.coolweather.gson.Forecast;
import com.lxy.coolweather.gson.Weather;
import com.lxy.coolweather.util.HttpUtil;
import com.lxy.coolweather.util.Utility;


import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class WeatherActivity extends AppCompatActivity {

    private ImageView bingPicImg;
    private ScrollView weatherLayout;
    private TextView titleCity;
    private TextView titleUpdateTime;
    private TextView nowDegree;
    private TextView nowInfo;
    private LinearLayout forecastLayout;
    private TextView aqiAqi;
    private TextView aqiPm25;
    private TextView suggestionComfot;
    private TextView suggestionCarWash;
    private TextView suggestionSport;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        initView();
        SharedPreferences prefs=PreferenceManager.getDefaultSharedPreferences(this);


        String picUrl=prefs.getString("bing_pic",null);
        if(picUrl!=null){
            Glide.with(this).load(picUrl).into(bingPicImg);
        } else{
            loadBingPic();
        }

        String weatherString=prefs.getString("weather",null);
        if(weatherString!=null){
            Weather weather=Utility.handleWeatherResponse(weatherString);
            showWeatherInfo(weather);
        } else{
            String weatherId=getIntent().getStringExtra("weather_id");
            weatherLayout.setVisibility(View.GONE);
            requestWeather(weatherId);
        }


    }

    private void requestWeather(final String weatherId){
        String weatherUrl="http://guolin.tech/api/weather?cityid="+weatherId+
                "&key=46fe771954e34cd6966570e1b36ac4fa";
        HttpUtil.sendHttpRequest(weatherUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(()->{
                    Toast.makeText(WeatherActivity.this,"获取天气信息失败",
                            Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText=response.body().string();
                final Weather weather=Utility.handleWeatherResponse(responseText);
                runOnUiThread(()->{
                    if(weather!=null && "ok".equals(weather.status)){
                        SharedPreferences.Editor editor=PreferenceManager.
                                getDefaultSharedPreferences(WeatherActivity.this).
                                edit();
                        editor.putString("weather",responseText);
                        editor.apply();
                        showWeatherInfo(weather);
                    } else {
                        Toast.makeText(WeatherActivity.this,"获取天气信息失败",
                                Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });
        loadBingPic();
    }

    private void loadBingPic(){
        String requestBingPic="http://guolin.tech/api/bing_pic";
        HttpUtil.sendHttpRequest(requestBingPic, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String picUrl=response.body().string();
                SharedPreferences.Editor editor=PreferenceManager.
                        getDefaultSharedPreferences(WeatherActivity.this).edit();
                editor.putString("bing_pic",picUrl);
                editor.apply();
                runOnUiThread(()->{
                    Glide.with(WeatherActivity.this).load(picUrl).into(bingPicImg);
                });
            }
        });
    }

    private void showWeatherInfo(Weather weather){
        String cityName=weather.basic.cityName;
        String updateTime="更新时间："+weather.basic.update.updateTime.split(" ")[1];
        String degree=weather.now.temperature+"℃";
        String weatherInfo=weather.now.info;

        titleCity.setText(cityName);
        titleUpdateTime.setText(updateTime);
        nowDegree.setText(degree);
        nowInfo.setText(weatherInfo);

        for(Forecast forecast:weather.forecastList){
            View view=LayoutInflater.from(this).inflate(R.layout.forecast_item,
                    forecastLayout,false);
            TextView date=(TextView)view.findViewById(R.id.forecast_date);
            TextView info=(TextView)view.findViewById(R.id.forecast_info);
            TextView max=(TextView)view.findViewById(R.id.forecast_max);
            TextView min=(TextView)view.findViewById(R.id.forecast_min);
            date.setText(forecast.date);
            info.setText(forecast.more.info);
            max.setText(forecast.temperature.max);
            min.setText(forecast.temperature.min);
            forecastLayout.addView(view);
        }

        if(weather.aqi!=null){
            aqiAqi.setText(weather.aqi.city.aqi);
            aqiPm25.setText(weather.aqi.city.pm25);
        }

        String comfort="舒适度:"+weather.suggestion.comfort.info;
        String carWash="洗车指数:"+weather.suggestion.carWash.info;
        String sport="运动建议:"+weather.suggestion.sport.info;

        suggestionComfot.setText(comfort);
        suggestionCarWash.setText(carWash);
        suggestionSport.setText(sport);

        weatherLayout.setVisibility(View.VISIBLE);
    }

    private void initView(){
        bingPicImg=(ImageView)findViewById(R.id.bing_pic_img);
        weatherLayout=(ScrollView)findViewById(R.id.weather_layout);
        titleCity=(TextView)findViewById(R.id.title_city);
        titleUpdateTime=(TextView)findViewById(R.id.title_update_time);
        nowDegree=(TextView)findViewById(R.id.now_degree);
        nowInfo=(TextView)findViewById(R.id.now_info);
        forecastLayout=(LinearLayout)findViewById(R.id.forecast_layout);
        aqiAqi=(TextView)findViewById(R.id.aqi_aqi);
        aqiPm25=(TextView)findViewById(R.id.aqi_pm25);
        suggestionComfot=(TextView)findViewById(R.id.suggest_comfort);
        suggestionCarWash=(TextView)findViewById(R.id.suggest_car_wash);
        suggestionSport=(TextView)findViewById(R.id.suggest_sport);
    }
}
