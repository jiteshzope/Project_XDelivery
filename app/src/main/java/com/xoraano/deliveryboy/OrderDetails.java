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
import com.xoraano.deliveryboy.Model.OrderItem;
import com.xoraano.deliveryboy.Model.OrderRequest;
import com.xoraano.deliveryboy.ViewHolderAdapter.OrderDetailsAdapter;

import java.util.List;

public class OrderDetails extends AppCompatActivity {

    public RecyclerView recyclerView;
    public RecyclerView.LayoutManager layoutManager;

    DatabaseReference app_parameters;
    FirebaseAuth auth;

    TextView txtCompleteAddress,txtDeliveryFee,txtstatus,txtAssignedPersonName,txtAssignedPersonMobile,txtusernotes;
    ImageView imgAssignedPerson;
    private TextView txtGrandTotal,txtSubTotal,txtTaxRate,txtPaymentMethod;

    String orderId;

    List<OrderItem> orderItems;

    OrderDetailsAdapter adapter;

    TextView txt_discount_amount, txt_total_with_discount;

    Button btncall_customer;
    String customer_phoneNo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);

        //Firebase
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference orderRequests = database.getReference("OrderRequests");

        recyclerView = findViewById(R.id.recycler_order_details);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        btncall_customer =findViewById(R.id.call_customer);

        txtGrandTotal =findViewById(R.id.txtGrandTotal_orderdetails);
        txtDeliveryFee =findViewById(R.id.txtDeliveryFee_orderdetails);
        txtSubTotal =findViewById(R.id.txtsubtotal_orderdetails);
        txtTaxRate =findViewById(R.id.txtTaxRate_orderdetails);
        txtstatus =findViewById(R.id.orderStatus);
        txt_total_with_discount =findViewById(R.id.txtTotalwithDiscount_orderdetails);
        txt_discount_amount =findViewById(R.id.txtDiscountAmount_orderdetails);

        txtAssignedPersonName=findViewById(R.id.txtAssignedPersonname);
        txtAssignedPersonMobile=findViewById(R.id.txtAssignedPersonMobile);
        imgAssignedPerson=findViewById(R.id.imgAssignedPerson);

        txtCompleteAddress =findViewById(R.id.deliveryAddress);

        txtusernotes = findViewById(R.id.txtUserNotes);

        txtPaymentMethod = findViewById(R.id.txtPaymentMethod_orderdetails);

        app_parameters = database.getReference("AppParameters");
        auth = FirebaseAuth.getInstance();

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

        if(auth.getCurrentUser()==null || Common.currentperson==null){
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }

        if (getIntent() != null){
            orderId = getIntent().getStringExtra("OrderId");
        }
        if (!orderId.isEmpty()) {

            orderRequests.child(orderId).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    OrderRequest orderRequest = dataSnapshot.getValue(OrderRequest.class);

                    customer_phoneNo = orderRequest.getPhone();

                    txtGrandTotal.setText(orderRequest.getGrandTotal()+" Rs");
                    txtSubTotal.setText(orderRequest.getSubTotal()+" Rs");
                    txtDeliveryFee.setText(orderRequest.getDeliveryFee()+" Rs");
                    txtTaxRate.setText(orderRequest.getTaxRate()+" %");

                    txt_discount_amount.setText("-"+orderRequest.getApplied_discount_amount()+" Rs");
                    txt_total_with_discount.setText(String.valueOf(Double.parseDouble(orderRequest.getGrandTotal())-Double.parseDouble(orderRequest.getDeliveryFee()))+" Rs");

                    String completeAddress = orderRequest.getName() + '\n' + orderRequest.getAddress() + '\n' + orderRequest.getPhone()+ '\n' + orderRequest.getPincode();
                    txtCompleteAddress.setText(completeAddress);
                    txtstatus.setText(orderRequest.getStatus());

                    txtusernotes.setText(orderRequest.getUserNotes());

                    txtPaymentMethod.setText(orderRequest.getPayment_method());

                    if(orderRequest.getAssignedPerson()!=null){
                        AssignedPerson assignedPerson = orderRequest.getAssignedPerson();
                        txtAssignedPersonName.setText(assignedPerson.getPersonName());
                        txtAssignedPersonMobile.setText(assignedPerson.getPersonMobile());
                        Picasso.get().load(assignedPerson.getPersonImage()).placeholder(R.drawable.unknownperson).into(imgAssignedPerson);
                    }

                    orderItems = orderRequest.getOrderItems();

                    adapter= new OrderDetailsAdapter(orderItems,getApplicationContext());
                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
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
