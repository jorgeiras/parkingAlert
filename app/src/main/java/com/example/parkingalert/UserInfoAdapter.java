package com.example.parkingalert;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.auth.User;

import java.util.List;

/**
 * Clase adaptador para el RecyclerView de la pestaña de Ranking de usuarios
 */
public class UserInfoAdapter extends RecyclerView.Adapter<UserInfoAdapter.UserInfoViewHolder> {

    Context context;
    List<UserInfo> list;


    public UserInfoAdapter(Context context, List<UserInfo> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public UserInfoAdapter.UserInfoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new UserInfoViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.user_info,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull UserInfoAdapter.UserInfoViewHolder holder, int position) {

        holder.rankingPosition.setText(String.valueOf(position+1));
        holder.userName.setText(list.get(position).getName());
        holder.userEmail.setText(list.get(position).getEmail());
        holder.userScore.setText("Puntuacion: " + String.valueOf(list.get(position).getScore()));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    /**
     * Clase que rellena cada uno de los items del RecyclerView de usuarios
     */
    class UserInfoViewHolder extends RecyclerView.ViewHolder{

        TextView rankingPosition;
        TextView userName;
        TextView userEmail;
        TextView userScore;


        public UserInfoViewHolder(@NonNull View itemView) {
            super(itemView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent itemActivity = new Intent(context, UserItemActivity.class);
                    int i = getAbsoluteAdapterPosition();

                    itemActivity.putExtra("name",list.get(i).getName());
                    itemActivity.putExtra("email",list.get(i).getEmail());
                    itemActivity.putExtra("score",list.get(i).getScore());
                    context.startActivity(itemActivity);
                }
            });

            rankingPosition = itemView.findViewById(R.id.rankingPosition);
            userName = itemView.findViewById(R.id.userNameText);
            userEmail = itemView.findViewById(R.id.userEmailText);
            userScore = itemView.findViewById(R.id.userScoreText);
        }

    }
}
