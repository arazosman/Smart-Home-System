package com.example.tasarmprojesi;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.NumberPicker;

public class PopupActivity extends Activity
{
    int index;
    Device device;

    ImageView icon;
    NumberPicker startHourPicker, startMinutePicker, endHourPicker, endMinutePicker;

    String hours[] = new String[] {"00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11",
                                   "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23"};

    String minutes[] = new String[] {"00", "06", "12", "18", "24", "30", "36", "42", "48", "54"};

    Button saveButton, deleteButton, backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_popup);

        index = getIntent().getIntExtra("index", 0);
        device = MainActivity.devices.get(index);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int)(width*.6), (int)(height*.35));

        setWindowIcon();

        startHourPicker = findViewById(R.id.start_hour);
        startMinutePicker = findViewById(R.id.start_minute);
        endHourPicker = findViewById(R.id.end_hour);
        endMinutePicker = findViewById(R.id.end_minute);

        initializeNumberPickers();

        saveButton = findViewById(R.id.timerSave);
        deleteButton = findViewById(R.id.timerDelete);

        loadTimer();
    }

    private void setWindowIcon()
    {
        icon = findViewById(R.id.tool_icon);
        int iconID = getResources().getIdentifier(device.name + "_mini", "drawable", getPackageName());
        icon.setImageDrawable(getDrawable(iconID));
    }

    private void initializeNumberPickers()
    {
        startHourPicker.setMinValue(0);
        startHourPicker.setMaxValue(23);
        startHourPicker.setDisplayedValues(hours);

        startMinutePicker.setMinValue(0);
        startMinutePicker.setMaxValue(9);
        startMinutePicker.setDisplayedValues(minutes);

        endHourPicker.setMinValue(0);
        endHourPicker.setMaxValue(23);
        endHourPicker.setDisplayedValues(hours);

        endMinutePicker.setMinValue(0);
        endMinutePicker.setMaxValue(9);
        endMinutePicker.setDisplayedValues(minutes);
    }

    public void loadTimer()
    {
        device.loadTimerFromFile();

        startHourPicker.setValue(device.begHour);
        startMinutePicker.setValue(device.begMinute);
        endHourPicker.setValue(device.endHour);
        endMinutePicker.setValue(device.endMinute);
    }

    public void saveTimer(View view)
    {
        device.begHour = startHourPicker.getValue();
        device.begMinute = startMinutePicker.getValue();
        device.endHour = endHourPicker.getValue();
        device.endMinute = endMinutePicker.getValue();
        device.writeTimerToFile();
        onBackPressed();
    }

    public void cleanTimer(View view)
    {
        device.resetTimer();
        onBackPressed();
    }

    public void back(View view)
    {
        onBackPressed();
    }
}