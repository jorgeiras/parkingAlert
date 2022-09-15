package com.example.parkingalert;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Fragmento para introducir un mapa de Google Maps en la actividad ParkingItemSpace
 */
public class MapsItemFragment extends Fragment implements OnMapReadyCallback {

    private double latitude;
    private double longitude;



        @Override
        public void onMapReady(GoogleMap googleMap) {
            LatLng parkPosition = new LatLng(latitude, longitude);
            googleMap.addMarker(new MarkerOptions().position(parkPosition).title("plaza de parking"));
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(parkPosition, 16.0f));

        }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        Bundle arguments = this.getArguments();
        latitude = arguments.getDouble("latitude");
        longitude = arguments.getDouble("longitude");
        return inflater.inflate(R.layout.fragment_maps_item, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapItem);



        mapFragment.getMapAsync(this);
    }
}