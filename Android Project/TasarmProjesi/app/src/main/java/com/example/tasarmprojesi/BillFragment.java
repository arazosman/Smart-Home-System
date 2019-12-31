package com.example.tasarmprojesi;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class BillFragment extends Fragment
{
    TextView totalPaymentText, totalKWText;
    Button resetButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.bill_tab, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        totalPaymentText = view.findViewById(R.id.total_price);
        totalKWText = view.findViewById(R.id.total_kw);
        resetButton = view.findViewById(R.id.resetButton);

        initializeDeviceWidgets();
        calculateTotalKW();
        calculateTotalPayment();
        readValuesFromDatabase();
        resetDatabaseWhenClicked();
    }

    private void initializeDeviceWidgets()
    {
        for (Device device: MainActivity.devices)
        {
            int iconID = getResources().getIdentifier("icon_" + device.name, "id", getActivity().getPackageName());
            int onoffID = getResources().getIdentifier("onoff_" + device.name, "id", getActivity().getPackageName());
            int timeID = getResources().getIdentifier("time_" + device.name, "id", getActivity().getPackageName());
            int kwID = getResources().getIdentifier("kw_" + device.name, "id", getActivity().getPackageName());
            int totalID = getResources().getIdentifier("total_" + device.name, "id", getActivity().getPackageName());

            device.setWidgets((ImageView)getActivity().findViewById(iconID),
                    (ImageView)getActivity().findViewById(onoffID),
                    (TextView)getActivity().findViewById(timeID),
                    (TextView)getActivity().findViewById(kwID),
                    (TextView)getActivity().findViewById(totalID));

            device.initializeWidgets();
        }
    }

    public void calculateTotalPayment()
    {
        float totalPayment = CarbonFragment.calculateTotalPayment();
        totalPaymentText.setText(String.format("%.2f", totalPayment) + " ₺");
    }

    public void calculateTotalKW()
    {
        float totalKW = CarbonFragment.calculateTotalKW();
        totalKWText.setText(String.format("%.2f", totalKW) + " kW");
    }

    private void readValuesFromDatabase()
    {
        for (final Device device: MainActivity.devices)
        {
            DatabaseReference activityReference = MainActivity.database.getReference(device.name  + MainActivity.userID);
            DatabaseReference timeReference = MainActivity.database.getReference(device.name  + MainActivity.userID + "_time");
            readActivity(activityReference, device);
            readTime(timeReference, device);
        }
    }

    private void readActivity(DatabaseReference reference, final Device device)
    {
        reference.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                boolean value = (boolean)dataSnapshot.getValue();

                if (value)
                    device.onoff.setColorFilter(getResources().getColor(R.color.colorPrimaryDark));
                else
                    device.onoff.setColorFilter(getResources().getColor(R.color.colorAccent));
            }

            @Override
            public void onCancelled(DatabaseError error)
            {
                Log.w("Hata: ", "Failed to read value.", error.toException());
            }
        });
    }

    private void readTime(DatabaseReference reference, final Device device)
    {
        reference.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                Long value = (Long) dataSnapshot.getValue();
                device.minute = value;
                device.updateWidgets();
                calculateTotalKW();
                calculateTotalPayment();
            }

            @Override
            public void onCancelled(DatabaseError error)
            {
                Log.w("Hata: ", "Failed to read value.", error.toException());
            }
        });
    }

    public void resetDatabaseWhenClicked()
    {
        resetButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                for (final Device device: MainActivity.devices)
                {
                    device.resetTimer();
                    DatabaseReference activityReference = MainActivity.database.getReference(device.name  + MainActivity.userID);
                    DatabaseReference timeReference = MainActivity.database.getReference(device.name  + MainActivity.userID + "_time");
                    activityReference.setValue(false);
                    timeReference.setValue(0);
                }
            }
        });

    }
}
