package com.example.parkingalert;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RecentParkingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RecentParkingsFragment extends Fragment {


    private RecyclerView recyclerView;
    ParkingSpaceAdapter parkingSpaceAdapter;
    FirebaseFirestore db;
    private double latitude;
    private double longitude;
    FusedLocationProviderClient fusedLoc;
    List<ParkingSpace> parkingSpaceList;
    parkAdapter parkAdapterObject;
    private SwipeRefreshLayout swipeRefreshLayout;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public RecentParkingsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RecentParkingsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RecentParkingsFragment newInstance(String param1, String param2) {
        RecentParkingsFragment fragment = new RecentParkingsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View v = inflater.inflate(R.layout.fragment_recent_parkings, container, false);

        swipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipeRefreshRecent);



        db = FirebaseFirestore.getInstance();
        recyclerView = v.findViewById(R.id.recyclerRecent);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        parkingSpaceList = new ArrayList<>();
        parkAdapterObject = new parkAdapter(getActivity(), parkingSpaceList);
        recyclerView.setAdapter(parkAdapterObject);

        getDeviceLocation();


        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                recyclerView.setAdapter(parkAdapterObject);
                parkingSpaceList.clear();
                db.collection("Parkings").orderBy("timeStamp", Query.Direction.DESCENDING).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                ParkingSpace p = document.toObject(ParkingSpace.class);
                                p.setDocID(document.getId());
                                double lat = 0.0144927536231884;
                                double lon = 0.0181818181818182;
                                double distance = 2;
                                double lowLat = latitude- (distance * lat);
                                double highLat = latitude + (distance * lat);
                                double lowLong = longitude - (distance * lon);
                                double highLong = longitude + (distance * lon);

                                if( p.getLatitude() > lowLat && p.getLatitude() < highLat && p.getLongitude() > lowLong && p.getLongitude() < highLong ){

                                    parkingSpaceList.add(p);
                                }

                            }
                            parkAdapterObject.notifyDataSetChanged();
                        } else {
                            Toast.makeText(getActivity(), "error" + task.getException(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                swipeRefreshLayout.setRefreshing(false);
            }
        });


        return v;
    }


    private void getDeviceLocation() {

        try {
            fusedLoc = LocationServices.getFusedLocationProviderClient(getContext());
            Task<Location> locationResult = fusedLoc.getLastLocation();
            locationResult.addOnSuccessListener( new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    Location lastKnownLocation = location;
                    if (lastKnownLocation != null) {

                        latitude = lastKnownLocation.getLatitude();
                        longitude = lastKnownLocation.getLongitude();

                    }
                }


            });

        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage(), e);
        }
    }


    @Override
    public void onResume() {

        super.onResume();
        recyclerView.setAdapter(parkAdapterObject);
        parkingSpaceList.clear();
        db.collection("Parkings").orderBy("timeStamp", Query.Direction.DESCENDING).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {

                        ParkingSpace p = document.toObject(ParkingSpace.class);
                        p.setDocID(document.getId());
                        double lat = 0.0144927536231884;
                        double lon = 0.0181818181818182;
                        double distance = 2;
                        double lowLat = latitude- (distance * lat);
                        double highLat = latitude + (distance * lat);
                        double lowLong = longitude - (distance * lon);
                        double highLong = longitude + (distance * lon);

                        if( p.getLatitude() > lowLat && p.getLatitude() < highLat && p.getLongitude() > lowLong && p.getLongitude() < highLong ){

                            parkingSpaceList.add(p);
                        }

                    }
                    parkAdapterObject.notifyDataSetChanged();
                } else {
                    Toast.makeText(getActivity(), "error" + task.getException(), Toast.LENGTH_SHORT).show();
                }
            }
        });
        swipeRefreshLayout.setRefreshing(false);

    }
}