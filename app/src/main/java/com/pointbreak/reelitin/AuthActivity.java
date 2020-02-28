package com.pointbreak.reelitin;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.net.URLEncoder;

public class AuthActivity extends AppCompatActivity {

    SQLiteDatabase mydatabase;
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        dialog = new ProgressDialog(AuthActivity.this, R.style.AppCompatAlertDialogStyle);
        dialog.setMessage("Verifying Login Request.");
        dialog.setCanceledOnTouchOutside(false);
        mydatabase = openOrCreateDatabase("REELITIN",MODE_PRIVATE,null);
        Cursor c = mydatabase.rawQuery("SELECT * FROM REELITIN;", null);
        c.moveToFirst();
        Log.e("AccessToken0", c.getString(0));
        Log.e("RefreshToken0", c.getString(1));
        mydatabase = openOrCreateDatabase("REELITIN",MODE_PRIVATE,null);
    }

    public void authBtn(View V) throws Exception {
        EditText editText = findViewById(R.id.email_text);
        try {
            emailVerify();
        } catch (Exception e) {
            Toast.makeText(AuthActivity.this, e.toString(), Toast.LENGTH_LONG).show();
        }
        dialog.show();
    }

    String server_response, refreshToken;
    public void emailVerify() throws Exception {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        refreshToken = "";
        Cursor c = mydatabase.rawQuery("SELECT * FROM REELITIN;", null);
        c.moveToFirst();
        refreshToken = c.getString(1);



        String url= "https://furtive-trail.glitch.me/token?token=" + URLEncoder.encode(refreshToken, "UTF-8");
        //String url = "http://sudeshna.gq/code/auth2.php?username=" + URLEncoder.encode(refreshToken, "UTF-8");
        RequestQueue queue = Volley.newRequestQueue(this);
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        server_response = response;
                        Log.e("Response2", server_response);
                        Toast.makeText(AuthActivity.this, server_response, Toast.LENGTH_LONG).show();
                        JSONParser parser = new JSONParser();
                        JSONObject auth_object = null;
                        try {
                            auth_object = (JSONObject) parser.parse(server_response);
                        } catch (ParseException e) {
                            e.printStackTrace();
                            Log.e("PArse Error",e.toString());
                        }
                        String accessToken = null;
                        accessToken = auth_object.get("accessToken").toString();

                        mydatabase.execSQL("UPDATE REELITIN SET ACCESS = '" + accessToken + "' WHERE REFRESH = '" + refreshToken + "';");
                        dialog.dismiss();
                        Intent i = new Intent(AuthActivity.this, MainActivity.class);
                        startActivity(i);
                        finish();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("Volley Error", error.toString());
            }
        });


        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }
}
