package com.suchakra.lcdwatchface;

import java.text.SimpleDateFormat;
import java.util.Date;
import android.annotation.SuppressLint;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

import org.json.JSONException;
import org.json.JSONObject;


public class WatchFaceLCDClockProvider extends AppWidgetProvider {

	static int numbers[] =  {
		R.drawable._0, R.drawable._1, R.drawable._2, R.drawable._3,
		R.drawable._4, R.drawable._5, R.drawable._6, R.drawable._7,
		R.drawable._8, R.drawable._9,
	};

    String ACTION_WEATHER_CHANGE = "cn.indroid.action.weather.freshwidget";

    public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		super.onUpdate(context, appWidgetManager, appWidgetIds);
		
		updateWidgets(context);
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		super.onReceive(context, intent);

		String action = intent.getAction();
		if (Intent.ACTION_TIME_TICK.equals(action)
				|| Intent.ACTION_TIME_CHANGED.equals(action)) {
			updateWidgets(context);
		}
        else if(ACTION_WEATHER_CHANGE.equals(action)){
            updateWeather(context, intent);
        }
    }

	@Override
	public void onEnabled(Context context) {
		super.onEnabled(context);

		Intent serviceIntent = new Intent(context, LCDClockService.class);
		context.startService(serviceIntent);
		
		registerReceivers(context);
	}

	private void registerReceivers(Context context) {
		IntentFilter filter = new IntentFilter();
		filter.addAction(Intent.ACTION_TIME_CHANGED);
		filter.addAction(Intent.ACTION_TIME_TICK);
        filter.addAction(ACTION_WEATHER_CHANGE);

		context.getApplicationContext().registerReceiver(this, filter);
	}

	@SuppressLint({ "SimpleDateFormat", "DefaultLocale" })
	private void updateWidgets(Context context) {
		String timeFormat = "HH:mm";
		String dateFormat = "MMM   d";
		Date date = new Date();

		String textDate = new SimpleDateFormat(dateFormat).format(date);
		String textTime = new SimpleDateFormat(timeFormat).format(date);

		AppWidgetManager manager = AppWidgetManager.getInstance(context);
		RemoteViews views = new RemoteViews(context.getPackageName(),
				R.layout.lcd_widget);

		views.setTextViewText(R.id.date, textDate.toUpperCase());
		views.setImageViewResource(R.id.hours_first_value,
				numbers[Character.getNumericValue(textTime.charAt(0))]);
		
		views.setImageViewResource(R.id.hours_second_value,
				numbers[Character.getNumericValue(textTime.charAt(1))]);
		
		views.setImageViewResource(R.id.minutes_first_value,
				numbers[Character.getNumericValue(textTime.charAt(3))]);
		
		views.setImageViewResource(R.id.minutes_second_value,
				numbers[Character.getNumericValue(textTime.charAt(4))]);

		manager.updateAppWidget(new ComponentName(context,
				WatchFaceLCDClockProvider.class), views);
	}
    
    private void updateWeatherWidgets(Context context, String weather, String temp) {
        //System.out.println(weather);
        AppWidgetManager manager = AppWidgetManager.getInstance(context);
        RemoteViews views = new RemoteViews(context.getPackageName(),
                R.layout.lcd_widget);
        
        /* clear visibility for all widgets*/
        views.setViewVisibility(R.id.sunny, View.INVISIBLE);
        views.setViewVisibility(R.id.cloudy, View.INVISIBLE);
        views.setViewVisibility(R.id.rainy, View.INVISIBLE);
        views.setViewVisibility(R.id.snowy, View.INVISIBLE);
        views.setViewVisibility(R.id.temp_minus, View.INVISIBLE);
        
        // set high temperature view
        if (temp.contains("-")){
            views.setViewVisibility(R.id.temp_minus, View.VISIBLE);
            String[] str = temp.split("-");
            temp = str[1];
        }
        
        int units = Integer.parseInt(temp) % 10;
        int tens = Integer.parseInt(temp) / 10;

        views.setImageViewResource(R.id.temp_first_val,
                numbers[tens]);

        views.setImageViewResource(R.id.temp_second_val,
                numbers[units]);
        
        if (weather.equalsIgnoreCase("sunny")){
            views.setViewVisibility(R.id.sunny, View.VISIBLE);
        }
        else if (weather.equalsIgnoreCase("cloudy")){
            views.setViewVisibility(R.id.cloudy, View.VISIBLE);
        }
        else if (weather.equalsIgnoreCase("rainy")){
            views.setViewVisibility(R.id.rainy, View.VISIBLE);
        }
        else if (weather.equalsIgnoreCase("snowy")){
            views.setViewVisibility(R.id.snowy, View.VISIBLE);
        }

        manager.updateAppWidget(new ComponentName(context,
                WatchFaceLCDClockProvider.class), views);
    }
    
    private void updateWeather(Context context, Intent intent){
        try {
            JSONObject json = new JSONObject(intent.getExtras().getString("weather_full"));
            String cond = json.getString("conditions");
            String temp = json.getString("low");
            if (cond.toLowerCase().contains("clear") || 
                    cond.toLowerCase().contains("hot") || 
                    cond.toLowerCase().contains("fair") || 
                    cond.toLowerCase().contains("sunny") || 
                    cond.toLowerCase().contains("cold")){
                updateWeatherWidgets(context, "sunny", temp);
            }
            else if (cond.toLowerCase().contains("cloud") || 
                    cond.toLowerCase().contains("dust") || 
                    cond.toLowerCase().contains("fog") || 
                    cond.toLowerCase().contains("smoke") || 
                    cond.toLowerCase().contains("haze") || 
                    cond.toLowerCase().contains("partly")){
                updateWeatherWidgets(context, "cloudy", temp);
            }
            else if (cond.toLowerCase().contains("rain") || 
                    cond.toLowerCase().contains("drizzle") || 
                    cond.toLowerCase().contains("sleet") || 
                    cond.toLowerCase().contains("thunder") ||
                    cond.equalsIgnoreCase("showers") ||
                    cond.toLowerCase().contains("scattered") && cond.toLowerCase().contains("shower")){
                updateWeatherWidgets(context, "rainy", temp);
            }
            else if (cond.toLowerCase().contains("snow")){
                updateWeatherWidgets(context, "snow", temp);
            }
            else {
                updateWeatherWidgets(context, "NA", temp);
            }
            //System.out.println(json.toString(2));
        } catch (JSONException e) {
            Log.e(ACTION_WEATHER_CHANGE, "JSONException: " + e.getMessage());
        }
    }
}
