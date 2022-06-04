package com.nsg.boardingfinder.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;
import androidx.annotation.NonNull;
import com.nsg.boardingfinder.R;
import com.nsg.boardingfinder.Response.FacilitiesResponse.Facility;
import com.nsg.boardingfinder.animation.ItemAnimation;
import com.nsg.boardingfinder.fragments.FacilityListFragment;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class FacilitiesRecyclerviewAdapter  extends RecyclerView.Adapter<FacilitiesRecyclerviewAdapter.RecyclerviewHolder> {

    Context context;
    List<Facility> dataList;
    List<Facility> filteredDataList;

    public FacilitiesRecyclerviewAdapter(Context context, List<Facility> dataList) {
        this.context = context;
        this.dataList = dataList;
        this.filteredDataList = dataList;
    }

    @NonNull
    @Override
    public RecyclerviewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.facilities_recyclerview_row_item, parent, false);
        return new RecyclerviewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerviewHolder holder, @SuppressLint("RecyclerView") final int position) {

        holder.txtFacilityName.setText(filteredDataList.get(position).getFacility());

        ItemAnimation.animateFadeIn(holder.itemView, position);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FacilityListFragment.deleteItem(String.valueOf(filteredDataList.get(position).getId()));
            }
        });

        holder.btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FacilityListFragment.updateItem(String.valueOf(filteredDataList.get(position).getId()), filteredDataList.get(position).getFacility());
            }
        });
    }

    @Override
    public int getItemCount() {
        return filteredDataList.size();
    }

    public static final class RecyclerviewHolder extends RecyclerView.ViewHolder {

        TextView txtFacilityName;
        Button btnDelete,btnUpdate;

        public RecyclerviewHolder(@NonNull View itemView) {
            super(itemView);

            txtFacilityName = itemView.findViewById(R.id.txtFacilityName);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            btnUpdate = itemView.findViewById(R.id.btnUpdate);
        }
    }

    public Filter getFilter(){

        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {

                String Key = charSequence.toString();
                if(Key.isEmpty()){
                    filteredDataList = dataList;
                }
                else{

                    List<Facility> lstFiltered = new ArrayList<>();
                    for(Facility row: dataList){
                        if(row.getFacility().toLowerCase().contains(Key.toLowerCase())){
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
                filteredDataList = (List<Facility>)filterResults.values;
                notifyDataSetChanged();
            }
        };

    }


}

