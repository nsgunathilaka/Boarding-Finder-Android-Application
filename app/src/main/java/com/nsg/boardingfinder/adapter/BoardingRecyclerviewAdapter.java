package com.nsg.boardingfinder.adapter;

import com.nsg.boardingfinder.R;
import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nsg.boardingfinder.animation.ListItemAnimation;
import com.nsg.boardingfinder.fragments.SeekerHomeFragment;
import com.nsg.boardingfinder.model.Boarding;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class BoardingRecyclerviewAdapter extends RecyclerView.Adapter<BoardingRecyclerviewAdapter.UsersRecyclerviewHolder> {

    Context context;
    List<Boarding> dataList;
    List<Boarding> filteredDataList;

    public BoardingRecyclerviewAdapter(Context context, List<Boarding> userList) {
        this.context = context;
        this.dataList = userList;
        this.filteredDataList = userList;
    }

    @NonNull
    @Override
    public UsersRecyclerviewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.boarding_recyclerview_item, parent, false);
        return new UsersRecyclerviewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UsersRecyclerviewHolder holder, @SuppressLint("RecyclerView") final int position) {

        holder.title.setText(filteredDataList.get(position).getTitle());
        holder.secondaryText.setText(filteredDataList.get(position).getPrice());
        holder.bottomLText.setText(filteredDataList.get(position).getLocation());
        holder.bottomRText.setText(filteredDataList.get(position).getTimeElapsed());
        holder.txtGender.setText(filteredDataList.get(position).getGender());
        Picasso.get().load(filteredDataList.get(position).getImage()).into(holder.cardimage);

        ListItemAnimation.animateFadeIn(holder.itemView, position);

        // TODO: Item onClick
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SeekerHomeFragment.listItemOnClick(String.valueOf(filteredDataList.get(position).getId()), filteredDataList.get(position).getAccommodaterId());
            }
        });

    }

    @Override
    public int getItemCount() {
        return filteredDataList.size();
    }

    public static final class UsersRecyclerviewHolder extends RecyclerView.ViewHolder {

        TextView title,secondaryText,bottomLText,bottomRText,txtGender;
        ImageView cardimage;

        //        CircleImageView imageDisaster;
        public UsersRecyclerviewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.cardTitle);
            secondaryText = itemView.findViewById(R.id.cardSecondText);
            bottomLText = itemView.findViewById(R.id.bottomLText);
            bottomRText = itemView.findViewById(R.id.bottomRText);
            txtGender = itemView.findViewById(R.id.txtGender);
            cardimage = itemView.findViewById(R.id.item_image);
        }
    }

    public Filter getFilter() {

        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {

                String Key = charSequence.toString();
                if (Key.isEmpty()) {
                    filteredDataList = dataList;
                } else {

                    List<Boarding> lstFiltered = new ArrayList<>();
                    for (Boarding row : dataList) {
                        if (row.getTitle().toLowerCase().contains(Key.toLowerCase()) || row.getLocation().toLowerCase().contains(Key.toLowerCase()) ) {
                            lstFiltered.add(row);
                        }
                    }

                    filteredDataList = lstFiltered;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = filteredDataList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                filteredDataList = (List<Boarding>) filterResults.values;
                notifyDataSetChanged();
            }
        };

    }

}
