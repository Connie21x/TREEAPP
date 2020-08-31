package com.example.treeapp;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MyViewHolder extends RecyclerView.ViewHolder {

    ImageView imageView;
    TextView textView;
    TextView mTextView;
    TextView oTextView;
    View v;

    public MyViewHolder(@NonNull View itemView) {
        super(itemView);

        imageView = itemView.findViewById(R.id.image_single_view);
        textView = itemView.findViewById(R.id.textView_single_view);
        mTextView = itemView.findViewById(R.id.textView_single_view_common_name);
        oTextView = itemView.findViewById(R.id.textView_single_view_origin);
        v = itemView;
    }
}
