package com.project.scanner.siva;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.project.scanner.R;

import java.util.List;

/**
 * Created by siva on 7/9/17.
 */

public class RVAdapter1 extends RecyclerView.Adapter<RVAdapter1.PersonViewHolder>{
    List<History_Data> history_data;


    RVAdapter1(List<History_Data> history_data){
        this.history_data = history_data;
    }

    @Override
    public int getItemCount() {
        return history_data.size();
    }

    @Override
    public PersonViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item, viewGroup, false);
        PersonViewHolder pvh = new PersonViewHolder(v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(PersonViewHolder personViewHolder, final int i) {
//        personViewHolder.order_no.setText(String.valueOf(friends_data.get(i).getFacebookId()));
//        personViewHolder.pick_up_time.setText(friends_data.get(i).getPickUpTime());
//        personViewHolder.pick_up_by.setText(friends_data.get(i).getPickUpBy());
//        personViewHolder.order_item_count.setText(String.valueOf(friends_data.get(i).getOrderItemCount()));
         personViewHolder.scnantext.setText(history_data.get(i).getScantext());
         personViewHolder.scanformat.setText(history_data.get(i).getScanformat());


    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public static class PersonViewHolder extends RecyclerView.ViewHolder {
        CardView cv;
        TextView scnantext;
        TextView scanformat;


        PersonViewHolder(View itemView) {
            super(itemView);
            cv = (CardView)itemView.findViewById(R.id.cv);
            scnantext=(TextView) itemView.findViewById(R.id.scantext);
            scanformat=(TextView)itemView.findViewById(R.id.scanformat);


//            confrim = (Button) itemView.findViewById(R.id.confirm);

        }
    }
}
