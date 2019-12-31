package com.example.tasarmprojesi;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class ControlFragment extends Fragment
{
    Button exitButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.control_tab, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        exitButton = view.findViewById(R.id.exitButton);

        initializeSwitches();
        exitWhenClicked();
    }

    public void initializeSwitches()
    {
        for (Device device: MainActivity.devices)
        {
            int switchID = getResources().getIdentifier("switch_" + device.name, "id", getActivity().getPackageName());
            int buttonID = getResources().getIdentifier("timer_" + device.name, "id", getActivity().getPackageName());
            device.setControlWidgets((Switch)getActivity().findViewById(switchID), (ImageButton)getActivity().findViewById(buttonID));
            device.readFromUser();
            device.readFromDatabase();
            device.timerTrigger();
        }
    }

    public void exitWhenClicked()
    {
        exitButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                ((MainActivity)getActivity()).onBackPressed();
            }
        });
    }
}