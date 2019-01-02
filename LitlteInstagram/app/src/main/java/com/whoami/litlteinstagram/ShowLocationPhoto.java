package com.whoami.litlteinstagram;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class ShowLocationPhoto extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap googleMap;
    private MapFragment mapFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_location_photo);
        initComponents();
    }

    private void initComponents() {
        mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        Intent intent = getIntent();
        LatLng latLng = new LatLng(Double.parseDouble(intent.getStringExtra("lat")),Double.parseDouble(intent.getStringExtra("lang")));
        this.googleMap.addMarker(new MarkerOptions().title(intent.getStringExtra("name")).position(latLng).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
        float v = (float) 15.0;
        this.googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,v));
    }
}
