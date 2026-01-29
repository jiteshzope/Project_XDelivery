package com.xoraano.deliveryboy;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.xoraano.deliveryboy.R;
import com.xoraano.deliveryboy.Common.Common;
import com.xoraano.deliveryboy.Interface.ItemClickListener;
import com.xoraano.deliveryboy.Model.OrderRequest;
import com.xoraano.deliveryboy.Model.OrderRequestViaCall;
import com.xoraano.deliveryboy.ViewHolderAdapter.ViaCall_IncomingOrderViewHolder;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ViaCallOrdersPage extends AppCompatActivity {

    public RecyclerView recyclerView;
    public RecyclerView.LayoutManager layoutManager;

    FirebaseDatabase database;
    DatabaseReference orderRequestsViaCall, orderRequests;

    DatabaseReference app_parameters;
    FirebaseAuth auth;

    FirebaseRecyclerAdapter<OrderRequestViaCall, ViaCall_IncomingOrderViewHolder> adapter1;

    com.jaredrummler.materialspinner.MaterialSpinner spinner;

    Button btn_goto_oo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_via_call_orders_page);

        //Firebase
        database = FirebaseDatabase.getInstance();
        orderRequestsViaCall = database.getReference("OrderRequestsViaCall");
        orderRequests = database.getReference("OrderRequests");

        app_parameters = database.getReference("AppParameters");
        auth = FirebaseAuth.getInstance();

        btn_goto_oo = findViewById(R.id.btn_show_oo);

        app_parameters.child("logoutd").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists() && dataSnapshot.getValue(String.class).equals("yes")){

                    auth.signOut();

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        if(auth.getCurrentUser()==null || Common.currentperson == null){
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }

        btn_goto_oo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ViaCallOrdersPage.this, OrdersPage.class);
                startActivity(intent);
                finish();
            }
        });

        set_goto_oo_button_text();
        
        

        recyclerView = findViewById(R.id.recycler_incoming_orders_via_call);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);


        loadIncomingOrders();
    }

    private void set_goto_oo_button_text() {

        orderRequests.orderByChild("assignedPersonPhone").equalTo(Common.currentperson.getMobileNo()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                int placed_orders=0, in_progress_orders=0, on_the_way_orders=0;

                for(DataSnapshot snapshot:dataSnapshot.getChildren()){

                    OrderRequest orderRequest = snapshot.getValue(OrderRequest.class);


                    if(orderRequest.getStatus().equals("Placed")){
                        placed_orders+=1;
                    }else if(orderRequest.getStatus().equals("InProgress")){
                        in_progress_orders+=1;
                    }else if(orderRequest.getStatus().equals("On the way")){
                        on_the_way_orders+=1;
                    }

                    int active_orders_for_delivery = placed_orders + in_progress_orders + on_the_way_orders;

                    if(active_orders_for_delivery > 0) {

                        btn_goto_oo.setText("Goto OO: Active " + active_orders_for_delivery);
                        btn_goto_oo.setBackgroundColor(getResources().getColor(R.color.red));

                    }else {

                        btn_goto_oo.setText("Goto OO: No active orders");
                        btn_goto_oo.setBackgroundColor(getResources().getColor(R.color.green));

                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void loadIncomingOrders() {
        final ProgressDialog mDialog = new ProgressDialog(ViaCallOrdersPage.this);
        mDialog.setMessage("Loading Orders.....");
        mDialog.show();

        adapter1 = new FirebaseRecyclerAdapter<OrderRequestViaCall, ViaCall_IncomingOrderViewHolder>(OrderRequestViaCall.class,R.layout.layout_item_incoming_orders_via_call, ViaCall_IncomingOrderViewHolder.class,
                orderRequestsViaCall.orderByChild("assignedPersonPhone").equalTo(Common.currentperson.getMobileNo())) {

            @Override
            protected void populateViewHolder(ViaCall_IncomingOrderViewHolder viewHolder, OrderRequestViaCall model, int position) {
                mDialog.dismiss();
                viewHolder.txtTime.setText(model.getTime());
                viewHolder.txtbill.setText(model.getGrandTotal());
                viewHolder.txtStatus.setText(model.getStatus());
                if(model.getStatus().equals("Placed") || model.getStatus().equals("InProgress") || model.getStatus().equals("On the way")){
                    viewHolder.txtStatus.setTextColor(getResources().getColor(R.color.red));
                }else {
                    viewHolder.txtStatus.setTextColor(getResources().getColor(R.color.green));
                }
                viewHolder.txtorderId.setText(model.getOrderId());
                viewHolder.txtDayAndDate.setText(model.getDay_and_date());
                viewHolder.txtDeliveryTimeTaken.setText(model.getDeliveryTimeTaken());

                if(model.getDelivery_rating()!=null) {
                    viewHolder.txt_delivery_rating.setText("Delivery boy rated :" + model.getDelivery_rating() + " Stars");
                }

                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        Intent intentOrderDetails = new Intent(ViaCallOrdersPage.this, OrderDetailsViaCall.class);
                        intentOrderDetails.putExtra("OrderId",adapter1.getRef(position).getKey());
                        startActivity(intentOrderDetails);
                    }
                });

            }

            @Override
            public OrderRequestViaCall getItem(int position) {
                return super.getItem(getItemCount()-1-position);
            }

            @Override
            public DatabaseReference getRef(int position) {
                return super.getRef(getItemCount()-1-position);
            }

        };
        adapter1.notifyDataSetChanged();
        recyclerView.setAdapter(adapter1);
    }

    public boolean onContextItemSelected(final MenuItem item) {

        if(item.getTitle().equals("Update status")) {
            String key = adapter1.getRef(item.getOrder()).getKey();
            OrderRequestViaCall clickitem = adapter1.getItem(item.getOrder());

            if ((clickitem.getStatus().equals("Delivered") || clickitem.getStatus().equals("Cancelled") || clickitem.getStatus().equals("Returned"))) {

                Toast.makeText(this, "You cannot update status of a delivered/cancelled/returned order. Contact Manager !!", Toast.LENGTH_LONG).show();

            }else{
                showUpdateDialog(key, clickitem);
            }
        }

        return super.onContextItemSelected(item);

    }

    private void showUpdateDialog(final String key, final OrderRequestViaCall item) {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(ViaCallOrdersPage.this);
        alertDialog.setTitle("Update order");
        alertDialog.setMessage("Please select the status");

        LayoutInflater inflater = this.getLayoutInflater();
        final View view = inflater.inflate(R.layout.update_status_dialog,null);

        spinner = view.findViewById(R.id.statusSpinner);
        spinner.setText(item.getStatus());
        spinner.setItems("Delivered","Cancelled","Returned");

        alertDialog.setView(view);

        final String localKey = key;
        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();

                item.setStatus(String.valueOf(spinner.getItems().get(spinner.getSelectedIndex())));
                item.setPincode_status(item.getPincode()+"_"+ String.valueOf(spinner.getItems().get(spinner.getSelectedIndex())));

                final ProgressDialog mDialog = new ProgressDialog(ViaCallOrdersPage.this);
                mDialog.setMessage("Please wait.........");
                mDialog.show();
                orderRequestsViaCall.child(localKey).child("status").setValue(item.getStatus()).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        mDialog.dismiss();
                        Toast.makeText(ViaCallOrdersPage.this, "Successfully set status to"+item.getStatus(), Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        mDialog.dismiss();
                        Toast.makeText(ViaCallOrdersPage.this, "Failed to update status", Toast.LENGTH_SHORT).show();
                    }
                });

                final ProgressDialog mDialog1 = new ProgressDialog(ViaCallOrdersPage.this);
                mDialog1.setMessage("Please wait.........");
                mDialog1.show();
                orderRequestsViaCall.child(localKey).child("pincode_status").setValue(item.getPincode_status()).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        mDialog1.dismiss();
                        Toast.makeText(ViaCallOrdersPage.this, "Successfully set pincode_status status to"+item.getPincode_status(), Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        mDialog1.dismiss();
                        Toast.makeText(ViaCallOrdersPage.this, "Failed to update pincode_status", Toast.LENGTH_SHORT).show();
                    }
                });

                if(item.getStatus().equals("Delivered") || item.getStatus().equals("Cancelled") || item.getStatus().equals("Returned")) {
                    final ProgressDialog mDialog2 = new ProgressDialog(ViaCallOrdersPage.this);
                    mDialog2.setMessage("Please wait.........");
                    mDialog2.show();

                    String orderplaced_date_time = item.getDateTime();
                    DateFormat dateFormat1 = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", java.util.Locale.ENGLISH);
                    Date date1 = new Date();
                    final Long timestamp_now = date1.getTime();
                    String current_date_time = dateFormat1.format(date1);
                    String deliveryTimeTaken = "";

                    //HH converts hour in 24 hours format (0-23), day calculation
                    SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");

                    Date d1 = null;
                    Date d2 = null;

                    try {
                        d1 = format.parse(orderplaced_date_time);
                        d2 = format.parse(current_date_time);

                        //in milliseconds
                        long diff = d2.getTime() - d1.getTime();

                        long diffSeconds = diff / 1000 % 60;
                        long diffMinutes = diff / (60 * 1000) % 60;
                        long diffHours = diff / (60 * 60 * 1000) % 24;
                        long diffDays = diff / (24 * 60 * 60 * 1000);

                        if (diffHours > 0) {
                            deliveryTimeTaken = diffHours + " hrs , " +diffMinutes + " mins, " + diffSeconds+" sec";
                        } else {
                            deliveryTimeTaken = diffMinutes + " mins, " + diffSeconds+" sec";
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    orderRequestsViaCall.child(localKey).child("deliveryTimeTaken").setValue(deliveryTimeTaken).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                            orderRequestsViaCall.child(key).child("timeCRDelivered").setValue(String.valueOf(timestamp_now)).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    mDialog2.dismiss();

                                    loadIncomingOrders();
                                    Toast.makeText(ViaCallOrdersPage.this, "Successfully saved time of delivery/return/cancellation", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    mDialog2.dismiss();
                                    Toast.makeText(ViaCallOrdersPage.this, "Failed to save time of delivery/return/cancellation . You might have updated the DCR order again", Toast.LENGTH_SHORT).show();
                                }
                            });

                            Toast.makeText(ViaCallOrdersPage.this, "Successfully calculated Delivery/C/R time taken", Toast.LENGTH_LONG).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            mDialog2.dismiss();
                            Toast.makeText(ViaCallOrdersPage.this, "Failed to calculate Delivery/C/R time taken", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

            }
        });

        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();

            }
        });

        alertDialog.show();
    }
}

