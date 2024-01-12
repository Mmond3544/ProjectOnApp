package com.example.qrscan;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

    Context context;
    ArrayList<subject> subjectArrayList;

    public String data;

    public MyAdapter(Context context, ArrayList<subject> subjectArrayList, String data) {
        this.context = context;
        this.subjectArrayList = subjectArrayList;
        this.data = data;
    }

    @NonNull
    @Override
    public MyAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(context).inflate(R.layout.item,parent,false);

        return new MyViewHolder(v);
    }
    public String sendtest;
    @Override
    public void onBindViewHolder(@NonNull MyAdapter.MyViewHolder holder, int position) {
        subject sj = subjectArrayList.get(position);
        holder.subjectname.setText(sj.name);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), MainActivity.class);
                sendtest = sj.ID +" "+ data;
                intent.putExtra("KEY_SENDER",sendtest);
                view.getContext().startActivity(intent);
                Toast.makeText(view.getContext(), sendtest,Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void open(){

    }
    @Override
    public int getItemCount() {
        return subjectArrayList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView subjectname;

        public MyViewHolder(@NonNull View itemView){
            super(itemView);
            subjectname = itemView.findViewById(R.id.subjectname);
        }
    }
}
