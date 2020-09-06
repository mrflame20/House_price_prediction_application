package com.justm.ssip;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.util.List;

public class PlaceListAdapter extends RecyclerView.Adapter<PlaceListAdapter.PlaceListViewHOlder>
{
    List<String> name,address ;

    public PlaceListAdapter(List<String> name, List<String> address) {
        this.name = name;
        this.address = address;
    }

    @NonNull
    @Override
    public PlaceListViewHOlder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.place_list_inflater,parent,false);
        return new PlaceListViewHOlder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlaceListViewHOlder holder, int position) {

        holder.name.setText(name.get(position));
        holder.address.setText(address.get(position));

    }

    @Override
    public int getItemCount() {
        return name.size();
    }

    public class PlaceListViewHOlder extends RecyclerView.ViewHolder {
            TextView name,address ;


        public PlaceListViewHOlder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.place_text_view_title);
            address = itemView.findViewById(R.id.place_address) ;
        }
    }
}
