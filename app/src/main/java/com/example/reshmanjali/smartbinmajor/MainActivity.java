package com.example.reshmanjali.smartbinmajor;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    TextView bin0Status;
    TextView bin1Status;
    ImageView bin0img;
    ImageView bin1img;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bin0Status=findViewById(R.id.id_status_val_bin0);
        bin1Status=findViewById(R.id.id_status_val_bin1);
        bin0img=findViewById(R.id.id_bin_img_0);
        bin1img=findViewById(R.id.id_bin_img_1);
        if(isNetworkAvailable()) {
            new GetStatusLevel("664949", "4").execute(); // for first bin===bin0
            new GetStatusLevel("666427", "4").execute();// for second bin===bin1
        }


    }


    boolean isNetworkAvailable(){
        ConnectivityManager connectivityManager=(ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeInfo=connectivityManager.getActiveNetworkInfo();
        Boolean check=(activeInfo!=null && activeInfo.isConnected());
        if(!check){
            AlertDialog.Builder aD=new AlertDialog.Builder(this);
            aD.setTitle("Warning");
            aD.setMessage("Please connect to the internet to see the updated values");
            aD.setCancelable(true);
            aD.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
        }
        return check;
    }

    public void gotoaddress0(View view) {
        double lat=16.36;
        double lng=80.53;
        String latlng=""+lat+","+lng;
        Uri gmmIntentUri = Uri.parse("google.navigation:q="+latlng);
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);        //navigating well and showing the best route to the destination
        mapIntent.setPackage("com.google.android.apps.maps");
        startActivity(mapIntent);

    }

    public void gotoaddress1(View view) {
        double lat=16.36;
        double lng=80.53;
        String latlng=""+lat+","+lng;
        Uri gmmIntentUri = Uri.parse("google.navigation:q="+latlng);
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);        //navigating well and showing the best route to the destination
        mapIntent.setPackage("com.google.android.apps.maps");
        startActivity(mapIntent);
    }

    private class GetStatusLevel extends AsyncTask<Void,Void,Void>{
        String channel;
        String fieldNum;
        String statusObtainedFromCloud="";
        public GetStatusLevel(String channel, String fieldNum){
            this.channel=channel;
            this.fieldNum=fieldNum;
        }
        ProgressDialog progressDialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog=new ProgressDialog(MainActivity.this);
            progressDialog.setMessage("Please wait...");
            progressDialog.setCancelable(true);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
           //       "https://api.thingspeak.com/channels/666427/fields/4.json";
           String urlString="https://api.thingspeak.com/channels/"+channel+"/fields/4.json";
           Log.i("-->url string",urlString);
           progressDialog.dismiss();

            try {
                String responseString=new HttpHandler().makeServiceCall(urlString);

                if(responseString!=null){
                    Log.i("--->received",responseString);
                    //Toast.makeText(getApplicationContext(), responseString, Toast.LENGTH_SHORT).show();
                    JSONObject jsonObject=new JSONObject(responseString);
                    JSONArray feedsJsonArray=jsonObject.getJSONArray("feeds");
                    //JSONObject object=  feedsJsonArray.get(feedsJsonArray.length()-1);
                    JSONObject lastJsonObject=feedsJsonArray.getJSONObject(feedsJsonArray.length()-1);
                    Log.i("-->lastob : ",lastJsonObject.toString());
                    statusObtainedFromCloud= lastJsonObject.getString("field4");
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
           if(progressDialog.isShowing())
            progressDialog.dismiss();
           if(channel.equals("664949")) {
               bin0Status.setText(statusObtainedFromCloud);
               int temp=Integer.parseInt(statusObtainedFromCloud);
               if(temp<1)
                   bin0img.setImageResource(R.drawable.zeronull);
               else if(temp<=20){
                   bin0img.setImageResource(R.drawable.gr20);
               }
               else if(temp<=30){
                    bin0img.setImageResource(R.drawable.gr30);
               }
               else if(temp<=50){
                    bin0img.setImageResource(R.drawable.gr50);
               }
               else if(temp<=60)
                   bin0img.setImageResource(R.drawable.gr60);
               else if(temp<=80)
                   bin0img.setImageResource(R.drawable.re80);
               else if(temp<=90)
                   bin0img.setImageResource(R.drawable.re90);
               else if(temp<=100)
                   bin0img.setImageResource(R.drawable.ic_battery_full_black_24dp);
              /* else if(temp>=95) {
                   bin0img.setImageResource(R.drawable.ic_battery_full_black_24dp);

                   Intent i = new Intent(this, OnAlarmReceiver.class);
                   PendingIntent pi = PendingIntent.getBroadcast(this, 0, i,
                           PendingIntent.FLAG_ONE_SHOT);
                   Calendar calendar = Calendar.getInstance();
                   calendar.set(Calendar.SECOND, calendar.get(Calendar.SECOND) + 10);
                   AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                   alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pi);

               }*/


           }
           else if(channel.equals("666427")) {
               bin1Status.setText(statusObtainedFromCloud);
               int temp=Integer.parseInt(statusObtainedFromCloud);
               if(temp<1)
                   bin1img.setImageResource(R.drawable.zeronull);
               else if(temp<=20){
                   bin1img.setImageResource(R.drawable.gr20);
               }
               else if(temp<=30){
                   bin1img.setImageResource(R.drawable.gr30);
               }
               else if(temp<=50){
                   bin1img.setImageResource(R.drawable.gr50);
               }
               else if(temp<=60)
                   bin1img.setImageResource(R.drawable.gr60);
               else if(temp<=80)
                   bin1img.setImageResource(R.drawable.re80);
               else if(temp<=90)
                   bin1img.setImageResource(R.drawable.re90);
               else if(temp<=100)
                   bin1img.setImageResource(R.drawable.ic_battery_full_black_24dp);
           }
        }
    }
}
