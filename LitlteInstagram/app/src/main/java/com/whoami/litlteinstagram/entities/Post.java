package com.whoami.litlteinstagram.entities;

import com.google.android.gms.maps.model.LatLng;
import org.json.JSONException;
import org.json.JSONObject;

public class Post
{
    private int id;
    private String photo, desc, lat, lang, location;

    public Post(JSONObject jsonObject) throws JSONException
    {
        id = jsonObject.getInt("id");
        photo = jsonObject.getString("photo");
        desc = jsonObject.getString("desc");
        lat = jsonObject.getString("lat");
        lang = jsonObject.getString("lang");
        location = jsonObject.getString("location");
    }

    public int getId() {
        return id;
    }

    public String getDesc() {
        return desc;
    }

    public String getLat() {
        return lat;
    }

    public String getLang() {
        return lang;
    }

    public String getImageLocation(){
        return "http://192.168.230.205/android/images/post/"+photo;
    }

    public boolean getLocation()
    {
        if(location.equals("1"))
        {
            return true;
        }else{
            return false;
        }
    }
}
