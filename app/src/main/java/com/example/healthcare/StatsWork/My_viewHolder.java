package com.example.healthcare.StatsWork;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.healthcare.R;


public class My_viewHolder extends RecyclerView.ViewHolder {

    public TextView title, totalInfected, date,firstLetter;

    public My_viewHolder(@NonNull View itemView) {
        super(itemView);

        title = itemView.findViewById(R.id.title);
        firstLetter = itemView.findViewById(R.id.firstLetter);
        date = itemView.findViewById(R.id.date);
        totalInfected = itemView.findViewById(R.id.totalInfected);

    }

}
