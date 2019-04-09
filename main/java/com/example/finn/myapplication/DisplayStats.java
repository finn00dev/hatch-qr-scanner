package com.example.finn.myapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;


import org.json.JSONArray;
import org.w3c.dom.Text;

import java.util.Timer;
import java.util.TimerTask;

public class DisplayStats extends AppCompatActivity {

    private TextView name1, name2;
    private TextView value1, value2;
    private ProgressBar bar;
    private ImageView image;

    private String Name;
    private String nameUrl, valueUrl;

    private RequestQueue queue;

    private Timer timer;
    private RefreshTask task;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_stats);

        //brings value of scan result over

        Intent intent = getIntent();
        Name = intent.getStringExtra(MainActivity.SCAN_RESULT);

        TextView title = findViewById(R.id.title);
        title.setText(Name);

        //http request

        queue = Volley.newRequestQueue(getApplicationContext());
        nameUrl = "**API URL**";
        valueUrl = "**API URL**";

        //screen items

        name1 = findViewById(R.id.cat1);
        name2 = findViewById(R.id.cat2);
        value1 = findViewById(R.id.value1);
        value2 = findViewById(R.id.value2);
        bar = findViewById(R.id.progressBar);
        image = findViewById(R.id.imageView);


        GetValues(queue, name1, name2, nameUrl, bar);
        GetValues(queue, value1, value2, valueUrl, bar);
        SetValues(queue, Name);

        int id = getResources().getIdentifier(Name.toLowerCase(), "drawable", getPackageName());
        image.setImageResource(id);

        if(timer != null){
            timer.cancel();
        }
        timer = new Timer();
        RefreshTask task = new RefreshTask();

        timer.scheduleAtFixedRate(task, 1000, 1000);

    }


    public class RefreshTask extends TimerTask{

        @Override
        public void run() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    GetValues(queue, name1, name2, nameUrl, bar);
                    GetValues(queue, value1, value2, valueUrl, bar);
                    SetValues(queue, Name);
                }
            });
        }

    }

    void GetValues(RequestQueue x, final TextView view1, final TextView view2, String url, final ProgressBar load){

        if(url == nameUrl) {
            JsonArrayRequest InfoRequest = new JsonArrayRequest(url,

                    new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray response) {
                            view1.setText(response.opt(0).toString() + ":");
                            view2.setText(response.opt(1).toString() + ":");
                            load.setVisibility(View.GONE);
                        }
                    },

                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            view1.setText("QR CODE NOT RECOGNIZED");
                            load.setVisibility(View.GONE);
                        }
                    });

            x.add(InfoRequest);

        } else {
            JsonArrayRequest InfoRequest = new JsonArrayRequest( url,

                    new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray response) {
                                view1.setText(response.opt(0).toString());
                                view2.setText(response.opt(1).toString());
                                load.setVisibility(View.GONE);
                        }
                    },

                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            view1.setText("QR CODE NOT RECOGNIZED");
                            load.setVisibility(View.GONE);
                        }
                    });

            x.add(InfoRequest);

        }


    }

    void SetValues(RequestQueue x, String name){
        String url = "**API URL**";

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.PUT, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });

        x.add(request);
    }
}
