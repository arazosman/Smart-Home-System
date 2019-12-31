package com.example.tasarmprojesi;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity
{
    EditText et_userID, et_userPassword;
    String userName, userPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        et_userID = findViewById(R.id.editText_userID);
        et_userPassword = findViewById(R.id.editText_userPassword);
    }

    public void CheckUser(View view)
    {
        userName = et_userID.getText().toString();
        userPassword = et_userPassword.getText().toString();

        if ((userName.equals("u0") && userPassword.equals("p0")) || (userName.equals("u1") && userPassword.equals("p1")))
        {
            String userID = userName.substring(1);
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("userID", userID);
            startActivity(intent);
        }
        else
            Toast.makeText(LoginActivity.this, "Kullanıcı adınız veya şifreniz hatalı. Lütfen tekrar deneyin.", Toast.LENGTH_SHORT).show();
    }
}
