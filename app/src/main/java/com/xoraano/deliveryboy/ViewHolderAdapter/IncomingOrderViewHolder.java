package com.xoraano.deliveryboy.ViewHolderAdapter;

import android.view.ContextMenu;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.xoraano.deliveryboy.R;
import com.xoraano.deliveryboy.Interface.ItemClickListener;


public class IncomingOrderViewHolder  extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnCreateContextMenuListener{

    public TextView txtorderId,txtbill,txtStatus,txtNoOfOrderItems,txtDayAndDate,txtTime,txttimetaken,txt_delivery_rating,gpsflag,txtpaymentmethod;

    private ItemClickListener itemClickListener;

    public IncomingOrderViewHolder(View itemView){
        super(itemView);

        txtDayAndDate = itemView.findViewById(R.id.orderday_and_date);
        txtbill = itemView.findViewById(R.id.orderbill);
        txtStatus = itemView.findViewById(R.id.orderStatus);
        txtNoOfOrderItems = itemView.findViewById(R.id.noOforderItems);
        txtorderId = itemView.findViewById(R.id.orderid);
        txtTime = itemView.findViewById(R.id.ordertime);
        txttimetaken = itemView.findViewById(R.id.txttimetaken);
        txt_delivery_rating = itemView.findViewById(R.id.txt_delivery_rating);
        gpsflag = itemView.findViewById(R.id.gpsflag);
        txtpaymentmethod = itemView.findViewById(R.id.txtpayment_method_myorders);


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

        //contextMenu.add(0,0,getAdapterPosition(), "Update status");
        contextMenu.add(0,0,getAdapterPosition(), "launch maps");
        contextMenu.add(0,1,getAdapterPosition(), "Update status");
        //contextMenu.add(0,3,getAdapterPosition(), "Assign Delivery person");



    }
}
