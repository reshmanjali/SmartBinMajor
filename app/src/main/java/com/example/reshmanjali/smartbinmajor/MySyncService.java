package com.example.reshmanjali.smartbinmajor;


import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;

public class MySyncService extends Service {            //devlpmnt in progress

    private Handler mHandler;
    // default interval for syncing data
    public static final long DEFAULT_SYNC_INTERVAL = 30 * 1000;

    // task to be run here
    private Runnable runnableService = new Runnable() {
        @Override
        public void run() {
            syncData();
            // Repeat this runnable code block again every ... min
            mHandler.postDelayed(runnableService, DEFAULT_SYNC_INTERVAL);
        }
    };

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Create the Handler object
        mHandler = new Handler();
        // Execute a runnable task as soon as possible
        mHandler.post(runnableService);

        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private synchronized void syncData() {
        // call your rest service here
        new GetDataAsync("664949").execute();
        new GetDataAsync("666427").execute();
    }
}

class GetDataAsync extends AsyncTask<Void,Void,Void>{

    String statusObtainedFromCloud1;
    String channel;
    GetDataAsync(String ch){
        channel=ch;
    }
    @Override
    protected Void doInBackground(Void... voids) {

        String urlString="https://api.thingspeak.com/channels/"+channel+"/fields/4.json";
        //Log.i("-->url string",urlString);


        try {
            String responseString=new HttpHandler().makeServiceCall(urlString);

            if(responseString!=null){
               // Log.i("--->received",responseString);
                //Toast.makeText(getApplicationContext(), responseString, Toast.LENGTH_SHORT).show();
                JSONObject jsonObject=new JSONObject(responseString);
                JSONArray feedsJsonArray=jsonObject.getJSONArray("feeds");
                //JSONObject object=  feedsJsonArray.get(feedsJsonArray.length()-1);
                JSONObject lastJsonObject=feedsJsonArray.getJSONObject(feedsJsonArray.length()-1);
                //Log.i("-->lastob : ",lastJsonObject.toString());
                statusObtainedFromCloud1= lastJsonObject.getString("field4");
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
        int i=Integer.getInteger(statusObtainedFromCloud1);
        if(i>=95){


        }
    }
}
