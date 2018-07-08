package com.uber.sample.Http;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Handler;

import com.uber.sample.R;

import org.json.JSONObject;


/**
 * Created by Phani on 06/07/2018.
 */
public class AsyncRequest extends AsyncTask<String, Integer, String> {

    OnAsyncRequestComplete caller;
    Activity context;
    String method = "GET";
    JSONObject postJsonObject;
    ProgressDialog pDialog = null;
    String response = "";
    String boomerangURL;
    String flagTypeValue;

    // Three Constructors
    public AsyncRequest(OnAsyncRequestComplete requestComplete, Activity activity, String method, String url, JSONObject jsonObject, String flagType) {
        caller = requestComplete;
        this.context = activity;
        this.method = method;
        this.boomerangURL = url;
        this.postJsonObject = jsonObject;
        this.flagTypeValue = flagType;
    }

    /*** GET CALL Constructor ***/
    public AsyncRequest(OnAsyncRequestComplete requestComplete, Activity activity, String method, String url, String flagType) {
        //caller = (OnAsyncRequestComplete) activity;
        this.caller = requestComplete;
        this.context = activity;
        this.method = method;
        this.boomerangURL = url;
        this.flagTypeValue = flagType;
    }

    public AsyncRequest(Activity activity, String url, String flagType) {
        caller = (OnAsyncRequestComplete) activity;
        this.context = activity;
        this.boomerangURL = url;
        this.flagTypeValue = flagType;
    }

    // Interface to be implemented by calling activity
    public interface OnAsyncRequestComplete {
        void asyncResponse(String response, String flagType);
    }

    public String doInBackground(String... urls) {
        // get url pointing to entry point of API

        //String address = urls[0].toString();
        if (method == "POST") {
            try {
                System.out.println("POST_URL = "+boomerangURL);
                response = ServiceHandler.processPostRequest(boomerangURL,postJsonObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return response;
        }

        if (method == "GET") {
            System.out.println("GET_URL = "+boomerangURL);
            response = ServiceHandler.processGetRequest(boomerangURL,30000);
            return response;
        }

        return null;
    }

    public void onPreExecute() {
        pDialog = ProgressDialog.show(context, "", "", true);
        pDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        pDialog.setContentView(R.layout.progress_dialog);
        pDialog.setCancelable(false);

    }

    public void onProgressUpdate(Integer... progress) {
        // you can implement some progressBar and update it in this record
        // setProgressPercent(progress[0]);
    }

    public void onPostExecute(String response) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (pDialog != null && pDialog.isShowing()) {
                    pDialog.dismiss();
                }
            }
        },1000);

        caller.asyncResponse(response,flagTypeValue);
    }

    protected void onCancelled(String response) {
        if (pDialog != null && pDialog.isShowing()) {
            pDialog.dismiss();
        }
        caller.asyncResponse(response,flagTypeValue);
    }

}

//Usage Steps Calling
//AsyncRequest getPosts = new AsyncRequest(this, "GET", params);
//getPosts.execute(apiURL);

//implements AsyncRequest.OnAsyncRequestComplete

//http://stackoverflow.com/questions/9767952/how-to-add-parameters-to-httpurlconnection-using-post


/*
try{
        if(response!=null&&response.length()>0){
        switch(flagType){

        case"GET_DASHBOARD_COUNT":
        JSONObject jsonObject=new JSONObject(response);
        break;

default:
        break;

        }

        }else{
        GeneralUtil.showToast(getApplicationContext(),"Something is not quite right!");
        }
        }catch(JSONException e){
        e.printStackTrace();
        }*/
