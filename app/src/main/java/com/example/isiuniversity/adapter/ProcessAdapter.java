package com.example.isiuniversity.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.appcompat.R;

import androidx.recyclerview.widget.RecyclerView;

public class ProcessAdapter extends RecyclerView.Adapter<ProcessAdapter.ViewHolder> {

   private String[] mDataset;
   public ProcessAdapter(String[] myDataset) {
      mDataset = myDataset;
   }

   public static class ViewHolder extends RecyclerView.ViewHolder {
      public TextView mTextView;
      public ViewHolder(TextView v) {
         super(v);
         mTextView = v;
      }
   }

   @Override
   public ProcessAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
      TextView v = (TextView) LayoutInflater.from(parent.getContext()).inflate(R.layout.support_simple_spinner_dropdown_item, parent, false);
      ViewHolder vh = new ViewHolder(v);
      return vh;
   }

   @Override
   public void onBindViewHolder(ViewHolder holder, int position) {
      holder.mTextView.setText(mDataset[position]);
      holder.mTextView.setTextColor(Color.BLACK);
   }

   @Override
   public int getItemCount() {
      return mDataset.length;
   }

}
