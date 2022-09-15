package com.example.parkingalert;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * Fragmento para mostrar el ranking de usuarios
 */
public class UserRankingFragment extends Fragment {

    private RecyclerView recyclerView;
    FirebaseFirestore db;
    private SwipeRefreshLayout swipeRefreshLayout;
    private List<UserInfo> userInfoList;
    private UserInfoAdapter userInfoAdapter;


    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public UserRankingFragment() {
        // Required empty public constructor
    }


    public static UserRankingFragment newInstance(String param1, String param2) {
        UserRankingFragment fragment = new UserRankingFragment();
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

        View v = inflater.inflate(R.layout.fragment_popular_parkings, container, false);

        swipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipeRefreshRankingUsers);
        db = FirebaseFirestore.getInstance();
        recyclerView = v.findViewById(R.id.recyclerRankingUsers);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        userInfoList = new ArrayList<>();
        userInfoAdapter = new UserInfoAdapter(getActivity(),userInfoList);


        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener(){

            @Override
            public void onRefresh() {
                filterUserRanking();
            }
        });

        return v;

    }


    @Override
    public void onResume() {
        super.onResume();
        filterUserRanking();
    }

    /**
     * Funcion para ordenar las usuarios por puntuacion de parking de manera decreciente
     */
    private void filterUserRanking(){
        recyclerView.setAdapter(userInfoAdapter);
        userInfoList.clear();
        db.collection("users").orderBy("score", Query.Direction.DESCENDING).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(QueryDocumentSnapshot document : task.getResult()){
                        UserInfo us = document.toObject(UserInfo.class);
                        userInfoList.add(us);
                    }
                    userInfoAdapter.notifyDataSetChanged();
                }else{
                    Toast.makeText(getActivity(), "error" + task.getException(), Toast.LENGTH_SHORT).show();
                }
            }
        });
        swipeRefreshLayout.setRefreshing(false);

    }


}