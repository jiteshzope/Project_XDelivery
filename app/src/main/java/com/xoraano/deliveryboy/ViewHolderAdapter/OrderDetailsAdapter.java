package com.xoraano.deliveryboy.ViewHolderAdapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.xoraano.deliveryboy.R;
import com.squareup.picasso.Picasso;
import com.xoraano.deliveryboy.Interface.ItemClickListener;
import com.xoraano.deliveryboy.Model.OrderItem;

import java.util.List;

class OrderDetailsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    public TextView product_name, quantity, amount;
    public ImageView product_image;
    List<OrderItem> list;
    Context context;

    private ItemClickListener itemClickListener;

    public OrderDetailsViewHolder(View itemView,Context context,List<OrderItem> list) {
        super(itemView);
        product_name = itemView.findViewById(R.id.product_name);
        product_image=itemView.findViewById(R.id.product_image);
        quantity = itemView.findViewById(R.id.txtquantity);
        amount = itemView.findViewById(R.id.txtamount);
        this.context = context;
        this.list=list;

        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int position = getAdapterPosition();
        OrderItem orderItem = this.list.get(position);
    }

    public void setAmount(TextView amount) {
        this.amount = amount;
    }

    public void setQuantity(TextView quantity) {
        this.quantity = quantity;
    }

    public ImageView getProduct_image() {
        return product_image;
    }

    public TextView getAmount() {
        return amount;
    }

    public TextView getProduct_name() {
        return product_name;
    }

    public TextView getQuantity() {
        return quantity;
    }

    public void setProduct_image(ImageView product_image) {
        this.product_image = product_image;
    }

    public void setProduct_name(TextView product_name) {
        this.product_name = product_name;
    }

}

public class OrderDetailsAdapter extends RecyclerView.Adapter<OrderDetailsViewHolder> {

    private List<OrderItem> listData;
    private Context context;

    public OrderDetailsAdapter(List<OrderItem> listData, Context context){
        this.listData = listData;
        this.context = context;
    }

    public List<OrderItem> getListData(){
        return this.listData;
    }

    @NonNull
    @Override
    public OrderDetailsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View itemView = inflater.inflate(R.layout.layout_item_order_details_page,parent,false);
        return new OrderDetailsViewHolder(itemView, context,listData);
    }

    @Override
    public void onBindViewHolder(@NonNull final OrderDetailsViewHolder holder, final int position) {

        Picasso.get().load(listData.get(position).getImage()).placeholder(R.drawable.ic_launcher_background).into(holder.product_image);
        holder.product_name.setText(listData.get(position).getProductName());
        holder.amount.setText(listData.get(position).getAmount());

        String qty = listData.get(position).getQuantity();
        holder.quantity.setText(qty);

    }

    @Override
    public int getItemCount() {
        return listData.size();
    }


}

