package com.example.camera;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;
import java.util.Date;
import java.util.Iterator;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.widget.Toast.LENGTH_LONG;

public class GetAgeGender extends AppCompatActivity {
Button button;
    private final OkHttpClient mOkHttpClient = new OkHttpClient();
    private String mAccessToken ="b9d1ed1f20f04446b190f7a54ffa578c";
    private Call mCall;
    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");
    private Call mcal;
TextView age;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_age_gender);
        age=findViewById(R.id.editText);
button=findViewById(R.id.gender);
button.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        Intent intent = new Intent(GetAgeGender.this, MainActivity.class);
        age=(TextView) intent.getSerializableExtra("qwer");

//        Intent intent=this.getIntent();
//        age= (TextView) intent.getSerializableExtra("qwer");
    }
});

    }



}

        
    


   
