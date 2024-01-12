package com.example.qrscan;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class stdAdapter extends RecyclerView.Adapter<stdAdapter.MyViewHolder> {

    Context context;
    ArrayList<student> studentArrayList;

    public stdAdapter(Context context, ArrayList<student> studentArrayList) {
        this.context = context;
        this.studentArrayList = studentArrayList;
    }

    @NonNull
    @Override
    public stdAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.stditem,parent,false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull stdAdapter.MyViewHolder holder, int position) {
        student std = studentArrayList.get(position);
        String name;
        name = std.name+" "+std.surname;
        holder.stdname.setText(name);
        holder.stdID.setText(std.ID);
    }

    @Override
    public int getItemCount() {
        return studentArrayList.size();
    }
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView stdname,stdID;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            stdname = itemView.findViewById(R.id.txtstdname);
            stdID = itemView.findViewById(R.id.txtstdID);

        }
    }
}
