package com.example.tasarmprojesi;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class CarbonFragment extends Fragment
{
    TextView firstText, secondText;
    private float carbonPerKW = 0.43f;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.carbon_tab, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        firstText = view.findViewById(R.id.savingText);
        secondText = view.findViewById(R.id.carbonText);

        readFromDB();
    }

    public static float calculateTotalKW()
    {
        float totalKW = 0;

        for (Device device: MainActivity.devices)
            totalKW += device.usedKW;

        return totalKW;
    }

    public static float calculateTotalPayment()
    {
        float totalPayment = 0;

        for (Device device: MainActivity.devices)
            totalPayment += device.payment;

        return totalPayment;
    }

    private void calculateCarbonFootprint()
    {
        float totalKW = calculateTotalKW();
        float totalPayment = calculateTotalPayment();

        String text = "Kullanmış olduğunuz toplam " + String.format("%.2f", totalKW) + " kW'lık enerjinin " +
                String.format("%.2f", totalKW/3.33) +  " kW'ını yenilenebilir enerjiden sağladığınız için " +
                String.format("%.2f", totalPayment/3.33) + " ₺ tasarruf ettiniz.";

        firstText.setText(text);

        float totalCarbon = totalKW * carbonPerKW;

        text = "Yenilenebilir enerji kullanımınızla birlikte " + String.format("%.2f", totalCarbon/3.33) +
                " kg'lık karbondioksit gazının doğaya salınımına engel oldunuz.";

        secondText.setText(text);
    }

    private void readFromDB()
    {
        for (final Device device: MainActivity.devices)
        {
            DatabaseReference reference = MainActivity.database.getReference(device.name + MainActivity.userID + "_time");

            reference.addValueEventListener(new ValueEventListener()
            {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot)
                {
                    Long value = (Long) dataSnapshot.getValue();
                    device.minute = (float)value;
                    calculateCarbonFootprint();
                }

                @Override
                public void onCancelled(DatabaseError error)
                {
                    Log.w("Hata: ", "Failed to read value.", error.toException());
                }
            });
        }
    }
}
