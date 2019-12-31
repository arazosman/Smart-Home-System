package com.example.tasarmprojesi;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.appcompat.app.AppCompatActivity;

import androidx.viewpager.widget.ViewPager;

import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity
{
    private ViewPager viewPager;

    public static FirebaseDatabase database;
    public static ArrayList<Device> devices;
    public static String userID;

    Timer timer;
    TextView clockTW;
    ImageView periodIcon;

    Calendar calendar;
    int hour, minute;

    public static float pricePerKW = 60;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewPager = findViewById(R.id.container);
        setupViewPager(viewPager);

        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        userID = getIntent().getStringExtra("userID");
        database = FirebaseDatabase.getInstance();

        clockTW = findViewById(R.id.clockText);
        periodIcon = findViewById(R.id.periodIcon);

        Typeface type = Typeface.createFromAsset(getAssets(),"digital.ttf");
        clockTW.setTypeface(type);

        initializeDevices();

        timeIncrementer();
    }

    private void setupViewPager(ViewPager viewPager)
    {
        SectionsPageAdapter adapter = new SectionsPageAdapter(getSupportFragmentManager());
        adapter.addFragment(new ControlFragment(), "Kontrol");
        adapter.addFragment(new BillFragment(), "Fatura");
        adapter.addFragment(new CarbonFragment(), "Detaylar");
        viewPager.setAdapter(adapter);
    }

    private void initializeDevices()
    {
        devices = new ArrayList<>();

        devices.add(new Device("bulb", 40, 0, this));
        devices.add(new Device("dishwasher", 1800, 1, this));
        devices.add(new Device("fridge", 45, 2, this));
        devices.add(new Device("washer",  800, 3, this));
        devices.add(new Device("oven", 2000, 4, this));
        devices.add(new Device("fan", 2500, 5, this));
        devices.add(new Device("pc", 90, 6, this));
        devices.add(new Device("tv", 90, 7, this));
        devices.add(new Device("iron", 2200, 8, this));
    }

    private void timeIncrementer()
    {
        TimerTask task = new TimerTask()
        {
            @Override
            public void run()
            {
                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        updateClock();
                        updateTimers();
                        updateDevices();
                    }
                });
            }
        };

        Timer timer = new Timer();
        timer.schedule(task, 0, 1000);
    }

    private void updateClock()
    {
        calendar = Calendar.getInstance();

        int realMinute = calendar.get(Calendar.MINUTE);
        int realSecond = calendar.get(Calendar.SECOND);

        hour = ((realMinute % 4) * 6 + (realSecond / 10)) % 24;
        minute = (realSecond % 10) * 6;

        clockTW.setText(String.format("%02d", hour) + ":" + String.format("%02d", minute));
        setPeriod();
    }

    private void setPeriod()
    {
        if (hour >= 6 && hour < 17) // gündüz
        {
            periodIcon.setImageResource(R.drawable.sun);
            pricePerKW = 72;
        }
        else if (hour >= 17 && hour < 22) // puant
        {
            periodIcon.setImageResource(R.drawable.peak);
            pricePerKW = 105;
        }
        else // gece
        {
            periodIcon.setImageResource(R.drawable.moon);
            pricePerKW = 45;
        }
    }

    private void updateTimers()
    {
        for (final Device device: MainActivity.devices)
        {
            if (device.begHour != device.endHour || device.begMinute != device.endMinute)
            {
                if (device.begHour == hour && device.begMinute*6 == minute)
                    device.aSwitch.setChecked(true);
                else if (device.endHour == hour && device.endMinute*6 == minute)
                    device.aSwitch.setChecked(false);
            }
        }
    }

    private void updateDevices()
    {
        for (final Device device: MainActivity.devices)
        {
            DatabaseReference reference = MainActivity.database.getReference(device.name  + MainActivity.userID);

            reference.addListenerForSingleValueEvent(new ValueEventListener()
            {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot)
                {
                    if ((boolean)dataSnapshot.getValue())
                        device.updateCurrentTimeOnDB();
                }

                @Override
                public void onCancelled(DatabaseError error)
                {
                    Log.w("Hata: ", "Failed to read value.", error.toException());
                }
            });
        }
    }

    @Override
    public void onBackPressed()
    {
        timer.cancel();
        timer.purge();
        super.onBackPressed();
    }
}
