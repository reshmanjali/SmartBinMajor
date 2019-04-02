package com.example.reshmanjali.smartbinmajor;

import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;

import java.net.MalformedURLException;
import java.net.URL;


public class HttpHandler {
    public HttpHandler(){

    }
    public String makeServiceCall(String reqUrl) throws MalformedURLException {

        URL url=new URL(reqUrl);
        Log.i("-->urlInHndlr",url.toString());
        InputStream in=null;
        StringBuilder builder=null;
        try {
            HttpURLConnection connection= (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            in=new BufferedInputStream(connection.getInputStream());
            BufferedReader reader=new BufferedReader(new InputStreamReader(in));
            builder=new StringBuilder();
            String line;
            while((line=reader.readLine())!=null){
                builder.append(line).append("\n");
                Log.i("-->lineHndlr",line.toString());
            }
            Log.i("-->builderInHndlr",builder.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            try {
                in.close();
            } catch (IOException e) {
                Log.i("-->ExptnHndlr", "");

                e.printStackTrace();
            }
        }
        Log.i("-->binderStringHndlr",builder.toString());
        return builder.toString();

    }
}
