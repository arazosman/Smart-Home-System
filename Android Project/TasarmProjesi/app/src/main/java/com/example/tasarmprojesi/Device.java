package com.example.tasarmprojesi;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class Device
{
    public String name;
    public float watt, usedKW, minute, price, payment;
    public int index;

    public int begHour, begMinute, endHour, endMinute;

    public Switch aSwitch;
    public ImageButton button;
    public ImageView icon, onoff;
    public TextView textTime, textKW, textPayment;

    SharedPreferences pref;
    SharedPreferences.Editor editor;

    public Device(String name, int watt, int index, Context context)
    {
        this.name = name;
        this.watt = watt;
        this.index = index;
        minute = 0;
        usedKW = 0;

        pref = context.getSharedPreferences(name + MainActivity.userID, 0); // 0 - for private mode
        editor = pref.edit();

        loadTimerFromFile();
    }

    public void loadTimerFromFile()
    {
        begHour = pref.getInt("begHour", 0);
        begMinute = pref.getInt("begMinute", 0);
        endHour = pref.getInt("endHour", 0);
        endMinute = pref.getInt("endMinute", 0);
    }

    public void writeTimerToFile()
    {
        editor.putInt("begHour", begHour);
        editor.putInt("begMinute", begMinute);
        editor.putInt("endHour", endHour);
        editor.putInt("endMinute", endMinute);
        editor.commit();
    }

    public void resetTimer()
    {
        begHour = begMinute = endHour = endMinute = 0;
        writeTimerToFile();
    }

    public void setControlWidgets(Switch aSwitch, ImageButton button)
    {
        this.aSwitch = aSwitch;
        this.button = button;
    }

    public void setWidgets(ImageView icon, ImageView onoff, TextView textTime, TextView textKW, TextView textPayment)
    {
        this.icon = icon;
        this.onoff = onoff;
        this.textTime = textTime;
        this.textKW = textKW;
        this.textPayment = textPayment;
    }

    public void calculatePayment()
    {
        price = watt * (MainActivity.pricePerKW / 1000);
        payment = (price * (minute / 60)) / 100;
    }

    public void calculateKW()
    {
        usedKW = (watt / 1000) * (minute / 60);
    }

    public void initializeWidgets()
    {
        updateWidgets();
    }

    public void updateWidgets()
    {
        calculatePayment();
        calculateKW();

        textTime.setText(String.format("%.1f", minute/60) + " sa.");
        textKW.setText(String.format("%.2f", usedKW) + " kW");
        textPayment.setText(String.format("%.2f", payment) + " ₺");
    }

    public void readFromUser()
    {
        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                MainActivity.database.getReference(name + MainActivity.userID).setValue(isChecked);
            }
        });
    }

    public void readFromDatabase()
    {
        DatabaseReference reference = MainActivity.database.getReference(name  + MainActivity.userID);

        reference.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                boolean value = (boolean)dataSnapshot.getValue();

                if (aSwitch.isChecked() != value)
                    aSwitch.setChecked(value);
            }

            @Override
            public void onCancelled(DatabaseError error)
            {
                Log.w("Hata: ", "Failed to read value.", error.toException());
            }
        });
    }

    public void timerTrigger()
    {
        button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(v.getContext(), PopupActivity.class);
                intent.putExtra("index", index);
                v.getContext().startActivity(intent);
            }
        });
    }

    public void updateCurrentTimeOnDB()
    {
        final DatabaseReference reference = MainActivity.database.getReference(name  + MainActivity.userID + "_time");

        reference.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                Long currTime = (Long)dataSnapshot.getValue();
                reference.setValue(currTime + 6);
            }

            @Override
            public void onCancelled(DatabaseError error)
            {
                Log.w("Hata: ", "Failed to read value.", error.toException());
            }
        });
    }
}