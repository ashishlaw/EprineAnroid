package com.eprine.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.eprine.R;
import com.eprine.propertyClasses.UserDetails;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class PopupRecyclerViewAdapter extends RecyclerView.Adapter<PopupRecyclerViewAdapter.MyViewHolder> {

    private Context mContext = null;
    private List<UserDetails> data = new ArrayList<>();
    private final PopupRecyclerViewAdapter.Listener listener;

    public PopupRecyclerViewAdapter(Context mContext, Listener listener, List<UserDetails> data) {
        this.mContext = mContext;
        this.listener = listener;
        this.data = data;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.recycler_popup_card_item, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        holder.mTextView.setText(data.get(position).getRole_name());
        holder.mTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClicked(data.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }


    //View Holder
    public static class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView mTextView;

        public MyViewHolder(View itemView) {
            super(itemView);

            mTextView = (TextView) itemView.findViewById(R.id.tvUserName);
        }
    }

    public interface Listener {
        void onItemClicked(UserDetails userDetails);
    }
}
