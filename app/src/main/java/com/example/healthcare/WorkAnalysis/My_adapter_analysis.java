package com.example.healthcare.WorkAnalysis;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.healthcare.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class My_adapter_analysis extends RecyclerView.Adapter<My_viewHolder_analysis> {

    private static final String BOOK_MARK = "bookmarks";
    private static final String LINK = "link";
    private static final String TITLE = "title";
    private static final String BM_IMAGE = "bm_image";

    private Context myContext;
    private User data;
    private Boolean flag = false;

    public My_adapter_analysis(Context context, User data) {
        myContext = context;
        this.data = data;
        if (data.getTotalResults() <= 1) flag = true;

    }

    @NonNull
    @Override
    public My_viewHolder_analysis onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        LayoutInflater inflater = LayoutInflater.from(myContext);
        View view = inflater.inflate((R.layout.news_list), parent, false);

        return new My_viewHolder_analysis(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final My_viewHolder_analysis holder, final int position) {

        if (flag) {
            holder.title.setText("No Result Found...");
            holder.description.setText("Please Search Something Else");
            holder.author.setVisibility(View.GONE);
            holder.dateView.setVisibility(View.GONE);
            holder.animationView.setVisibility(View.GONE);
            holder.share.setVisibility(View.GONE);
        } else {
            Article[] articles = data.getArticles();
            final Article model = articles[position];

            holder.title.setText(model.getTitle());
            holder.description.setText(model.getDescription());
            holder.source.setText(model.getSource().getName());
            holder.author.setText(model.getAuthor());
            holder.date.setText(getDate(model.getPublishedAt()));
            Glide.with(myContext).load(model.getUrlToImage())
                    .placeholder(R.drawable.placeholder)
                    .centerCrop().into(holder.img);

            holder.share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    shareURL(model.getUrl());
                }
            });

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(model.getUrl().toString()));
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    myContext.startActivity(i);
                }
            });

        }

    }

    private void shareURL(String url) {

        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(Intent.EXTRA_TEXT, "Read this :\n" + url + "\n\nShared via com.example.healthcare.fragments.News Hunter");
        myContext.startActivity(Intent.createChooser(sharingIntent, "Share via"));

    }

    @Override
    public int getItemCount() {
        return data.getArticles().length;
    }

    @SuppressLint("SimpleDateFormat")
    private String getDate(String oldDate) {
        String newDate;
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy", new Locale("india"));
        try {
            Date date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").parse(oldDate);
            newDate = dateFormat.format(date);
        } catch (Exception e) {
            newDate = oldDate;
        }
        return newDate;
    }

}

