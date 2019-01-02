package com.whoami.litlteinstagram.lib;

import android.os.AsyncTask;
import org.json.JSONException;
import java.util.ArrayList;


public class InternetTask extends AsyncTask<String, Void, Void>
{
    private OnInternetTaskFinishedListerner onInternetTaskFinishedListerner;
    private String tag,responseString,urlString,methode;
    private ArrayList<KeyValue> requestData;
    private Exception exception;
    private boolean usesForm;
    private FormData formData;
    public static final String URI = "http://192.168.230.205/android/v4/api";

    public InternetTask(String methode, String urlString, ArrayList<KeyValue> requestData)
    {
        this.methode=methode;
        this.urlString=urlString;
        this.requestData=requestData;
        this.onInternetTaskFinishedListerner=null;
        this.responseString="";
        this.tag="";
        this.exception=null;
    }

    public InternetTask(String urlString, FormData formData)
    {
        this.methode = InternetHelper.REQUEST_METHOD_POST;
        this.urlString = urlString;
        this.formData = formData;
        this.onInternetTaskFinishedListerner = null;
        this.responseString = "";
        this.tag = "";
        this.usesForm = true;
        this.exception=null;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public void setOnInternetTaskFinishedListerner(OnInternetTaskFinishedListerner onInternetTaskFinishedListerner)
    {
        this.onInternetTaskFinishedListerner = onInternetTaskFinishedListerner;
    }

    @Override
    protected Void doInBackground(String... strings)
    {
        try{
            if(this.usesForm)
            {
                this.responseString=InternetHelper.uploadFiles(this.urlString,this.formData);
            }else{
                this.responseString = InternetHelper.sendHTTPRequest(this.methode,this.urlString,this.requestData);
            }
        }
        catch (Exception e)
        {
            this.exception = e;
        }
        return null;
    }

    public String getResponseString() {
        return responseString;
    }


    public Exception getException() {
        return exception;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(Void aVoid)
    {
        super.onPostExecute(aVoid);
        if(this.onInternetTaskFinishedListerner != null)
        {
            if(this.exception==null)
            {
                try
                {
                    this.onInternetTaskFinishedListerner.OnInternetTaskFinished(this);
                } catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }else{
                this.onInternetTaskFinishedListerner.OnInternetTaskFailed(this);
            }
        }
    }
}
