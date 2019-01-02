package com.whoami.litlteinstagram;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.Checkable;
import android.widget.EditText;
import android.widget.ImageView;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.whoami.litlteinstagram.lib.FormData;
import com.whoami.litlteinstagram.lib.InternetTask;
import com.whoami.litlteinstagram.lib.OnInternetTaskFinishedListerner;
import com.whoami.litlteinstagram.lib.UtilityHelper;
import org.json.JSONException;
import org.json.JSONObject;

public class Post extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener, GoogleMap.OnMyLocationChangeListener, OnInternetTaskFinishedListerner{
    private GoogleMap googleMap;
    private MapFragment mapFragment;
    private Marker marker;
    private LatLng myLocation;
    private Boolean useCurrentLocation = false, useLocation=false;
    public static final int USE_CAMERA = 10;
    public static final int USE_GALLERY = 11;
    private Bitmap bitmap;
    private ImageView image;
    private EditText desc;
    private static final String API_POST = "upload_photo";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        initComponents();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    private void initComponents() {
        mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        if(!useLocation){
            mapFragment.getView().setVisibility(View.GONE);
        }else{
            mapFragment.getView().setVisibility(View.VISIBLE);
        }
        Intent intent = getIntent();
        byte[] byteArray = intent.getByteArrayExtra("image");
        bitmap = BitmapFactory.decodeByteArray(byteArray,0,byteArray.length);
        image = (ImageView) findViewById(R.id.image);
        desc = (EditText) findViewById(R.id.inputComment);
        image.setImageBitmap(bitmap);
    }

    public void buttonClicked(View view) {
        switch (view.getId()) {
            case R.id.buttonUpload:
                postPhoto();
                break;
        }
    }

    public void checkboxClicked(View view) {
        boolean b = ((Checkable) view).isChecked();
        if (b) {
            switch (view.getId()){
                case R.id.checkbox_set_location:
                    mapFragment.getView().setVisibility(View.VISIBLE);
                    break;
                case R.id.checkbox_use_current_location:
                    CheckBox checkBox = (CheckBox) findViewById(R.id.checkbox_set_location);
                    checkBox.setChecked(true);
                    mapFragment.getView().setVisibility(View.VISIBLE);
                    useCurrentLocation = true;
            }
            useLocation=true;
        } else {
            switch (view.getId()){
                case R.id.checkbox_set_location:
                    mapFragment.getView().setVisibility(View.GONE);
                    useLocation=false;
                    break;
                case R.id.checkbox_use_current_location:
                    useCurrentLocation = false;
                    break;
            }
        }
    }

    private void postPhoto() {
        if(bitmap!=null){
            FormData formData = new FormData();
            formData.add("m",Post.API_POST);
            if(useLocation) {
                if(useCurrentLocation){
                    formData.add("location", "1");
                    formData.add("lat", String.valueOf(myLocation.latitude));
                    formData.add("lang", String.valueOf(myLocation.longitude));
                }else{
                    formData.add("location", "1");
                    formData.add("lat", String.valueOf(marker.getPosition().latitude));
                    formData.add("lang", String.valueOf(marker.getPosition().longitude));
                }
            }else{
                formData.add("location","0");
            }
            formData.add("desc",desc.getText().toString());
            formData.addImage("photo",bitmap,FormData.FILE_FORMAT_JPEG);
            connectApi(Post.API_POST,formData);
        }else{
            showAlert("No Photo Selected","You didn't select a photo");
        }
    }

    private void connectApi(String tag, FormData formData){
        InternetTask internetTask = new InternetTask(InternetTask.URI,formData);
        internetTask.setTag(tag);
        internetTask.setOnInternetTaskFinishedListerner(this);
        internetTask.execute();
    }

    private Marker addMarker(LatLng latLng) {
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.title("Set Place Here");
        markerOptions.position(latLng);
        return googleMap.addMarker(markerOptions);
    }

    private void showAlert(String title, String message) {
        UtilityHelper.showAlert(this, title, message);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        this.googleMap.setOnMapClickListener(this);
        this.googleMap.getUiSettings().setScrollGesturesEnabled(true);
        this.googleMap.getUiSettings().setCompassEnabled(true);
        this.googleMap.getUiSettings().setZoomControlsEnabled(true);
        this.googleMap.getUiSettings().setZoomGesturesEnabled(true);
        this.googleMap.getUiSettings().setAllGesturesEnabled(true);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            showAlert("Permission Error","Setting location permission in setting");
            return;
        }
        this.googleMap.setMyLocationEnabled(true);
        this.googleMap.setOnMyLocationChangeListener(this);
    }

    @Override
    public void onMapClick(LatLng latLng) {
        if(marker!=null){
            marker.remove();
        }
        marker = addMarker(latLng);
    }

    @Override
    public void onMyLocationChange(Location location) {
        myLocation = new LatLng(location.getLatitude(), location.getLongitude());
    }

    @Override
    public void OnInternetTaskFinished(InternetTask internetTask) throws JSONException {
        switch (internetTask.getTag()){
            case Post.API_POST:
                    try{
                        JSONObject jsonObject = new JSONObject(internetTask.getResponseString());
                        if(jsonObject.getString("code").equals("200")){
                            setResult(Activity.RESULT_OK);
                            finish();
                        }else{
                            showAlert("Error Post",jsonObject.getString("data"));
                        }
                    }catch (Exception e){
                        showAlert("Error Parsing JSON",e.getMessage());
                    }
                break;
        }
    }

    @Override
    public void OnInternetTaskFailed(InternetTask internetTask) {
        showAlert("Error Connection",internetTask.getException().getMessage());
    }
}
