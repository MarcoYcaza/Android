package com.example.my_library;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class projectAdapter extends RecyclerView.Adapter<projectAdapter.btsViewHolder> {


    private ArrayList<btsItem> mbtsList;
    private ArrayList<btsItem> copyOfList;


    public void setCopyOfList(ArrayList<btsItem> copyOfList) {
        this.copyOfList = new ArrayList<>(copyOfList);
    }


    public projectAdapter(final ArrayList<btsItem> mbtsList) {

        this.mbtsList = mbtsList;
        this.copyOfList = new ArrayList<>(mbtsList);

    }





    public static class btsViewHolder extends RecyclerView.ViewHolder {
        public TextView mbtsName;
        public TextView muniqueCode;
        public TextView mdescription;

        public btsViewHolder(@NonNull View itemView) {
            super(itemView);

            mbtsName = itemView.findViewById(R.id.textView);
            muniqueCode = itemView.findViewById(R.id.textView2);
            mdescription = itemView.findViewById(R.id.textView3);

        }


    }

    @NonNull
    @Override
    public btsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.bts_item, parent, false);
        btsViewHolder evh = new btsViewHolder(v);

        // acá lo estoy ingresando como argumento del oncreate.
        return evh;
    }

    @Override
    public void onBindViewHolder(@NonNull btsViewHolder holder, int position) {

        btsItem currentItem = mbtsList.get(position);

        holder.mbtsName.setText(currentItem.getBtsName());
        holder.muniqueCode.setText(currentItem.getUniqueCode());
        holder.mdescription.setText(currentItem.getDescription());

    }

    @Override
    public int getItemCount() {
        return mbtsList.size();
    }
    /**/
    private Filter btsStationFilter = new Filter(){
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            ArrayList<btsItem> filteredList = new ArrayList<>();


            FilterResults results = new FilterResults();


            if (constraint == null || constraint.length() == 0) {

                filteredList.addAll(copyOfList);


            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                //Este es un copy of list dinamico
                for (btsItem item : copyOfList) {
                    if (item.getBtsName().toLowerCase().contains(filterPattern)) {
                        filteredList.add(item);
                    }
                }
            }

            results.values = filteredList;


            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            // Vemos que al final el valor de la lista es
            //modificado por el filtered list final.
            mbtsList.clear();
            mbtsList.addAll((ArrayList) results.values);
            notifyDataSetChanged();
        }
    };

    public Filter getFilter(){
        return btsStationFilter;
    }


}
