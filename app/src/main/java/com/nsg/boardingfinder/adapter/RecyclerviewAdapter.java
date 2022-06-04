package com.nsg.boardingfinder.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;
import androidx.annotation.NonNull;
import com.nsg.boardingfinder.R;
import com.nsg.boardingfinder.animation.ItemAnimation;
import com.nsg.boardingfinder.fragments.PostListFragment;
import com.nsg.boardingfinder.model.PostData;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class RecyclerviewAdapter extends RecyclerView.Adapter<RecyclerviewAdapter.RecyclerviewHolder> {

    Context context;
    List<PostData> postDataList;
    List<PostData> filteredPostDataList;

    public RecyclerviewAdapter(Context context, List<PostData> postDataList) {
        this.context = context;
        this.postDataList = postDataList;
        this.filteredPostDataList = postDataList;
    }

    @NonNull
    @Override
    public RecyclerviewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.recyclerview_row_item, parent, false);
        return new RecyclerviewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerviewHolder holder, @SuppressLint("RecyclerView") final int position) {

        holder.txtPostTitle.setText(filteredPostDataList.get(position).getTitle());
        holder.txtPostPrice.setText(filteredPostDataList.get(position).getPrice());
        holder.txtPostStatus.setText(filteredPostDataList.get(position).getStatus());
        ///TODO: Set Status Text Color
        switch (filteredPostDataList.get(position).getStatus()){
            case "ACTIVE":
                holder.txtPostStatus.setTextColor(Color.parseColor("#18A558"));
                break;
            case "PENDING":
                holder.txtPostStatus.setTextColor(Color.parseColor("#FAD02C"));
                break;
            case "DENIED":
                holder.txtPostStatus.setTextColor(Color.parseColor("#FF2E2E"));
                break;
        }
        Picasso.get().load(filteredPostDataList.get(position).getImageUrl()).into(holder.userImage);

        ItemAnimation.animateFadeIn(holder.itemView, position);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 PostListFragment.listItemOnClick(filteredPostDataList.get(position).getBoardingId(), filteredPostDataList.get(position).getOwnerId());
            }
        });

        holder.userImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "Post Image", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return filteredPostDataList.size();
    }

    public static final class RecyclerviewHolder extends RecyclerView.ViewHolder {

        CircleImageView userImage;
        TextView txtPostTitle, txtPostPrice, txtPostStatus;

        public RecyclerviewHolder(@NonNull View itemView) {
            super(itemView);

            userImage = itemView.findViewById(R.id.userImage);
            txtPostTitle = itemView.findViewById(R.id.txtPostTitle);
            txtPostPrice = itemView.findViewById(R.id.txtPostPrice);
            txtPostStatus = itemView.findViewById(R.id.txtPostStatus);
        }
    }

    public Filter getFilter(){

        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {

                String Key = charSequence.toString();
                if(Key.isEmpty()){
                    filteredPostDataList = postDataList;
                }
                else{

                    List<PostData> lstFiltered = new ArrayList<>();
                    for(PostData row: postDataList){
                        if(row.getTitle().toLowerCase().contains(Key.toLowerCase())){
                            lstFiltered.add(row);
                        }
                    }

                    filteredPostDataList = lstFiltered;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = filteredPostDataList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                filteredPostDataList = (List<PostData>)filterResults.values;
                notifyDataSetChanged();
            }
        };

    }


}
