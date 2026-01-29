package com.xoraano.deliveryboy;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.xoraano.deliveryboy.R;
import com.squareup.picasso.Picasso;
import com.xoraano.deliveryboy.Common.Common;
import com.xoraano.deliveryboy.Model.AssignedPerson;
import com.xoraano.deliveryboy.Model.OrderItemViaCall;
import com.xoraano.deliveryboy.Model.OrderRequestViaCall;
import com.xoraano.deliveryboy.ViewHolderAdapter.OrderDetailsAdapter_ViaCall;

import java.util.List;

public class OrderDetailsViaCall extends AppCompatActivity {

    public RecyclerView recyclerView;
    public RecyclerView.LayoutManager layoutManager;

    DatabaseReference app_parameters,delivery_persons;
    FirebaseAuth auth;

    TextView txtCompleteAddress,txtDeliveryFee,txtstatus,txtAssignedPersonName,txtAssignedPersonMobile,txtUserNotes;
    ImageView imgAssignedPerson;

    private TextView txtGrandTotal;

    String orderId;

    List<OrderItemViaCall> orderItems;

    OrderRequestViaCall orderRequest;

    OrderDetailsAdapter_ViaCall adapter;

    Button btncall_customer;
    String customer_phoneNo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details_via_call);

        //Firebase
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference orderRequests = database.getReference("OrderRequestsViaCall");

        app_parameters = database.getReference("AppParameters");
        auth = FirebaseAuth.getInstance();
        delivery_persons = database.getReference("deliveryPersons");

        btncall_customer =findViewById(R.id.call_customer);

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

        recyclerView = findViewById(R.id.recycler_order_details_via_call);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        txtGrandTotal =findViewById(R.id.txtGrandTotal_orderdetails_via_call);
        txtDeliveryFee =findViewById(R.id.txtDeliveryFee_orderdetails_via_call);

        txtstatus =findViewById(R.id.txt_orderStatus_orderdetails_via_call);

        txtAssignedPersonName=findViewById(R.id.txtAssignedPersonname_orderdetails_via_call);
        txtAssignedPersonMobile=findViewById(R.id.txtAssignedPersonMobile_orderdetails_via_call);
        imgAssignedPerson=findViewById(R.id.imgAssignedPerson_orderdetails_via_call);

        txtCompleteAddress =findViewById(R.id.txt_delivery_details_via_call);

        txtUserNotes =findViewById(R.id.txtUserNotes_orderdetails_via_call);

        if (getIntent() != null){
            orderId = getIntent().getStringExtra("OrderId");
        }
        if (!orderId.isEmpty()) {

            orderRequests.child(orderId).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if (dataSnapshot.getValue(OrderRequestViaCall.class)!=null) {

                        orderRequest = dataSnapshot.getValue(OrderRequestViaCall.class);

                        customer_phoneNo = orderRequest.getPhone();

                        txtGrandTotal.setText(orderRequest.getGrandTotal()+" Rs");

                        txtDeliveryFee.setText(orderRequest.getDeliveryFee()+" Rs");

                        txtUserNotes.setText(orderRequest.getSpecial_notes());

                        String completeAddress = orderRequest.getName() + '\n' + orderRequest.getAddress() + '\n' + orderRequest.getPhone()+ '\n' + orderRequest.getPincode();
                        txtCompleteAddress.setText(completeAddress);
                        txtstatus.setText(orderRequest.getStatus());

                        if(orderRequest.getAssignedPerson()!=null){
                            AssignedPerson assignedPerson = orderRequest.getAssignedPerson();
                            txtAssignedPersonName.setText(assignedPerson.getPersonName());
                            txtAssignedPersonMobile.setText(assignedPerson.getPersonMobile());
                            Picasso.get().load(assignedPerson.getPersonImage()).placeholder(R.drawable.ic_launcher_background).into(imgAssignedPerson);
                        }

                        orderItems = orderRequest.getOrderItems();

                        adapter= new OrderDetailsAdapter_ViaCall(orderItems,getApplicationContext());
                        recyclerView.setAdapter(adapter);
                        adapter.notifyDataSetChanged();

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        btncall_customer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Uri number = Uri.parse("tel:"+customer_phoneNo);
                Intent callIntent = new Intent(Intent.ACTION_DIAL, number);
                startActivity(callIntent);

            }
        });
    }

}


