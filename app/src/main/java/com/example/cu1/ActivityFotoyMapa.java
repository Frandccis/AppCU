package com.example.cu1;

import android.os.Bundle;

import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


// Esta activity viene dada por Google y se encarga de mostrar en el mapa el lugar
// en el que se tomo la foto originalmente, asi de como permitir su apertura en
// Google Maps para dirigirse a la ubicacion, mirar sitios alrededor, etc.

public class ActivityFotoyMapa extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private double Lat, Long;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_foto_y_mapa);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Lat = getIntent().getDoubleExtra(Principal.LAT, 0);
        Long = getIntent().getDoubleExtra(Principal.LONG, 0);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Aniade una marca en el lugar de la foto dada las coordenadas y la enfoca
        LatLng foto = new LatLng(Lat, Long);
        mMap.addMarker(new MarkerOptions().position(foto).title("Lugar de la foto"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(foto));
    }
}