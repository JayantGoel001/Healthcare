package com.example.healthcare.WorkAnalysis;

import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.healthcare.R;


public class ViewHolder_horizontal extends RecyclerView.ViewHolder {

    ImageView home_image;

    ViewHolder_horizontal(@NonNull View itemView) {
        super(itemView);

        home_image = itemView.findViewById(R.id.home_image);
    }

}
