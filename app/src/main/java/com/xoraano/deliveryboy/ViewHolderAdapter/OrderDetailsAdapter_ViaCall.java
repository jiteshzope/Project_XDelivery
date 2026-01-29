package com.xoraano.deliveryboy.ViewHolderAdapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.xoraano.deliveryboy.R;
import com.xoraano.deliveryboy.Interface.ItemClickListener;
import com.xoraano.deliveryboy.Model.OrderItemViaCall;

import java.util.List;

class OrderDetailsViewHolderViaCall extends RecyclerView.ViewHolder implements View.OnClickListener{

    public TextView product_name, quantity, amount;
    List<OrderItemViaCall> list;
    Context context;

    private ItemClickListener itemClickListener;

    public OrderDetailsViewHolderViaCall(View itemView, Context context, List<OrderItemViaCall> list) {
        super(itemView);
        product_name = itemView.findViewById(R.id.txt_item_name_list_order_items_order_detailspage_via_call);
        quantity = itemView.findViewById(R.id.txt_item_quantity_list_order_items_order_detailspage_via_call);
        amount = itemView.findViewById(R.id.txt_item_amount_list_order_items_order_detailspage_via_call);
        this.context = context;
        this.list=list;

        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int position = getAdapterPosition();
        OrderItemViaCall orderItem = this.list.get(position);
    }

}

public class OrderDetailsAdapter_ViaCall extends RecyclerView.Adapter<OrderDetailsViewHolderViaCall> {

    private List<OrderItemViaCall> listData;
    private Context context;

    public OrderDetailsAdapter_ViaCall(List<OrderItemViaCall> listData, Context context){
        this.listData = listData;
        this.context = context;
    }

    public List<OrderItemViaCall> getListData(){
        return this.listData;
    }

    @NonNull
    @Override
    public OrderDetailsViewHolderViaCall onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View itemView = inflater.inflate(R.layout.layout_item_order_details_page_via_call,parent,false);
        return new OrderDetailsViewHolderViaCall(itemView, context,listData);
    }

    @Override
    public void onBindViewHolder(@NonNull final OrderDetailsViewHolderViaCall holder, final int position) {

        holder.product_name.setText(listData.get(position).getName());
        holder.amount.setText(listData.get(position).getAmount());

        String qty = listData.get(position).getQuantity();
        holder.quantity.setText(qty);

    }

    @Override
    public int getItemCount() {
        return listData.size();
    }


}


