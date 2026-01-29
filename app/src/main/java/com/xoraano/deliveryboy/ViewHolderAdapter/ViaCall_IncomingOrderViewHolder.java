package com.xoraano.deliveryboy.ViewHolderAdapter;

import android.view.ContextMenu;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.xoraano.deliveryboy.R;
import com.xoraano.deliveryboy.Interface.ItemClickListener;


public class ViaCall_IncomingOrderViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnCreateContextMenuListener{

    public TextView txtorderId,txtbill,txtStatus,txtDayAndDate,txtTime,txt_delivery_rating,txtDeliveryTimeTaken;

    private ItemClickListener itemClickListener;

    public ViaCall_IncomingOrderViewHolder(View itemView){
        super(itemView);

        txtDayAndDate = itemView.findViewById(R.id.orderday_and_date_viacall);
        txtbill = itemView.findViewById(R.id.orderbill_viacall);
        txtStatus = itemView.findViewById(R.id.orderStatus_viacall);
        txtorderId = itemView.findViewById(R.id.orderid_viacall);
        txtTime = itemView.findViewById(R.id.ordertime_viacall);
        txt_delivery_rating = itemView.findViewById(R.id.txt_delivery_rating_viacall);
        txtDeliveryTimeTaken = itemView.findViewById(R.id.deliverytimetaken_viacall);

        itemView.setOnClickListener(this);
        itemView.setOnCreateContextMenuListener(this);

    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public void onClick(View view){
        itemClickListener.onClick(view,getAdapterPosition(),false);
    }

    @Override
    public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {

        contextMenu.setHeaderTitle("Select the action to perform!");

        contextMenu.add(0,0,getAdapterPosition(), "Update status");
        contextMenu.add(0,1,getAdapterPosition(), "Save Invoice");
        contextMenu.add(0,2,getAdapterPosition(), "Assign Delivery person");



    }
}
