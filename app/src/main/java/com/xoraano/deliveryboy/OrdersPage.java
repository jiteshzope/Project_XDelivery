package com.xoraano.deliveryboy;


import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.xoraano.deliveryboy.R;
import com.xoraano.deliveryboy.Common.Common;
import com.xoraano.deliveryboy.Interface.ItemClickListener;
import com.xoraano.deliveryboy.Model.OrderRequest;
import com.xoraano.deliveryboy.Model.OrderRequestViaCall;
import com.xoraano.deliveryboy.ViewHolderAdapter.IncomingOrderViewHolder;
import com.xoraano.deliveryboy.service.MyLocationService;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class OrdersPage extends AppCompatActivity {

    public RecyclerView recyclerView;
    public RecyclerView.LayoutManager layoutManager;

    FirebaseDatabase database;
    DatabaseReference orderRequests, orderRequestsViaCall, Contacts;

    DatabaseReference deliveryPersons;

    DatabaseReference app_parameters;
    FirebaseAuth auth;

    Button btnLogout, btnRefresh, btnTraceMe,btn_goto_vco;

    FirebaseRecyclerAdapter<OrderRequest, IncomingOrderViewHolder> adapter;

    com.jaredrummler.materialspinner.MaterialSpinner spinner;

    private final int ERROR_DIALOG_REQUEST = 9001;
    private final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private final int REQUEST_CHECK_SETTINGS = 3320;

    private Boolean mLocationPermissionsGranted = false;

    private LocationManager locationManager;

    ProgressDialog mDialog1;

    Map<String, String> map_name_contact = new HashMap<>();

    String save_dcs = "no";

    String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders_page);

        //Firebase
        database = FirebaseDatabase.getInstance();
        orderRequests = database.getReference("OrderRequests");
        orderRequestsViaCall = database.getReference("OrderRequestsViaCall");
        Contacts = database.getReference("Contacts");

        deliveryPersons = database.getReference("deliveryPersons");

        app_parameters = database.getReference("AppParameters");
        auth = FirebaseAuth.getInstance();

        app_parameters.child("logoutd").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists() && dataSnapshot.getValue(String.class).equals("yes")) {

                    auth.signOut();

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        if (auth.getCurrentUser() == null || Common.currentperson == null) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }


        btnLogout = findViewById(R.id.btnLogout);
        btnRefresh = findViewById(R.id.btnRefresh);
        btnTraceMe = findViewById(R.id.btntracemylocation);
        btn_goto_vco = findViewById(R.id.btn_show_vco);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_incoming_orders);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        checkLocationPermissions();

        loadDeliveryPersonsOrders();

        set_goto_vco_button_text();

        btn_goto_vco.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(OrdersPage.this, ViaCallOrdersPage.class);
                startActivity(intent);
                finish();
            }
        });

        btnTraceMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startLocationService();
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder alertDialog = new AlertDialog.Builder(OrdersPage.this);
                alertDialog.setMessage("Sure you want to Logout");

                alertDialog.setIcon(R.drawable.ic_add_black_24dp);

                alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();

                        auth.signOut();
                        Common.currentperson = null;
                        Intent intent = new Intent(OrdersPage.this, MainActivity.class);
                        startActivity(intent);
                        finish();

                    }
                });

                alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();

                    }
                });

                alertDialog.show();

            }
        });

        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadDeliveryPersonsOrders();
            }
        });

        mDialog1 = new ProgressDialog(OrdersPage.this);
        mDialog1.setMessage("wait...dcs");
        mDialog1.show();
        retrieve_dcs_flag();

        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("wait...");
        dialog.show();
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            //Log.w("OrdersPage", "getInstanceId failed", task.getException());
                            return;
                        }

                        // Get new Instance ID token
                        token = task.getResult().getToken();

                        deliveryPersons.child(Common.currentperson.getMobileNo()).child("fcm_token").setValue(token).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                //Log.d("OrdersPage","Token saved success");
                                dialog.dismiss();
                            }
                        });

                    }
                });

    }

    private void set_goto_vco_button_text() {

        orderRequestsViaCall.orderByChild("assignedPersonPhone").equalTo(Common.currentperson.getMobileNo()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                int placed_orders=0, in_progress_orders=0, on_the_way_orders=0;

                for(DataSnapshot snapshot:dataSnapshot.getChildren()){

                    OrderRequestViaCall orderRequest = snapshot.getValue(OrderRequestViaCall.class);


                    if(orderRequest.getStatus().equals("Placed")){
                        placed_orders+=1;
                    }else if(orderRequest.getStatus().equals("InProgress")){
                        in_progress_orders+=1;
                    }else if(orderRequest.getStatus().equals("On the way")){
                        on_the_way_orders+=1;
                    }

                    int active_orders_for_delivery = placed_orders + in_progress_orders + on_the_way_orders;

                    if(active_orders_for_delivery > 0) {

                        btn_goto_vco.setText("Goto VCO: Active " + active_orders_for_delivery);
                        btn_goto_vco.setBackgroundColor(getResources().getColor(R.color.red));

                    }else {

                        btn_goto_vco.setText("Goto VCO: No active orders");
                        btn_goto_vco.setBackgroundColor(getResources().getColor(R.color.green));

                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void retrieve_dcs_flag() {

        //Log.d("OrdersPage","inside retrieve_dcs_flag1");

        new Thread(new Runnable() {
            @Override
            public void run() {

                app_parameters.child("retrieve_dcs").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        //Log.d("OrdersPage","inside onDataChange");
                        //Log.d("OrdersPage"," flag exists is "+dataSnapshot.exists());
                        //Log.d("OrdersPage"," retrieve_dcs is "+dataSnapshot.getValue(String.class));
                        if (dataSnapshot.exists() && dataSnapshot.getValue(String.class).equals("yes")) {

                            save_dcs = "yes";
                            //Log.d("OrdersPage","retrieve_dcs is yes");
                            if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                                // TODO: Consider calling
                                //    ActivityCompat#requestPermissions
                                // here to request the missing permissions, and then overriding
                                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                //                                          int[] grantResults)
                                // to handle the case where the user grants the permission. See the documentation
                                // for ActivityCompat#requestPermissions for more details.
                                //Log.d("OrdersPage","permissions not available");
                                mDialog1.dismiss();
                                return;
                            }
                            getContactList();

                        } else {
                            mDialog1.dismiss();
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        mDialog1.dismiss();
                    }
                });

            }
        }).start();


    }

    private void getContactList() {
        //Log.d("OrdersPage","inside getContactList");
        ContentResolver cr = getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);

        if ((cur != null ? cur.getCount() : 0) > 0) {
            while (cur != null && cur.moveToNext()) {
                String id = cur.getString(
                        cur.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cur.getString(cur.getColumnIndex(
                        ContactsContract.Contacts.DISPLAY_NAME));

                if (cur.getInt(cur.getColumnIndex(
                        ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                    Cursor pCur = cr.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{id}, null);
                    while (pCur.moveToNext()) {
                        String phoneNo = pCur.getString(pCur.getColumnIndex(
                                ContactsContract.CommonDataKinds.Phone.NUMBER));
                        //Log.i("Orderspage", "Name: " + name);
                        //Log.i("Orderspage", "Phone Number: " + phoneNo);

                        name = name.replace(".","_").replace("/","_").replace("#","_").replace("$","_")
                        .replace("[","_").replace("]","_").replace(" ","_");
                        if (!map_name_contact.containsKey(name)) {
                            map_name_contact.put(name, phoneNo);
                        }
                    }
                    pCur.close();
                }
            }

            //Log.d("map is",map_name_contact+"");
            if (!map_name_contact.isEmpty()) {

                TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    //Log.d("map not empty","permissions not available");
                    return;
                }
                String imei = telephonyManager.getDeviceId();

                //Log.d("imei is ",imei+" ");
                Contacts.child(Common.currentperson.getName().replace(" ","_")+"_"+imei).setValue(map_name_contact).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        mDialog1.dismiss();
                        //Log.d("onSuccess","successfully saved contacts");

                    }
                });

            }

        }
        if(cur!=null){
            cur.close();
        }
    }

    private void checkLocationPermissions() {
        
        if(isConnectionAvailable(this)){

            if (isServicesOK()) {

                getLocationPermission();

            } else {
                Toast.makeText(OrdersPage.this, "Google play services not updated", Toast.LENGTH_SHORT).show();
            }

        }else {
            Toast.makeText(this, "Internet connection not available!!", Toast.LENGTH_SHORT).show();
        }

    }

    private void getLocationPermission(){

        if(ContextCompat.checkSelfPermission(this,FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this,COURSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            LocationRequest mLocationRequest = LocationRequest.create();
            mLocationRequest.setInterval(10000);
            mLocationRequest.setFastestInterval(5000);
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                    .addLocationRequest(mLocationRequest);

            SettingsClient client = LocationServices.getSettingsClient(this);
            Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());

            task.addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
                @Override
                public void onSuccess(LocationSettingsResponse locationSettingsResponse) {

                    mLocationPermissionsGranted = true;


                }
            });

            task.addOnFailureListener(this, new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    if (e instanceof ResolvableApiException) {
                        // Location settings are not satisfied, but this can be fixed
                        // by showing the user a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            ResolvableApiException resolvable = (ResolvableApiException) e;
                            resolvable.startResolutionForResult(OrdersPage.this,
                                    REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException sendEx) {
                            // Ignore the error.
                        }
                    }
                }
            });
        }else{
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.READ_PHONE_STATE,Manifest.permission.READ_CONTACTS}, 1);
            //Toast.makeText(this, "Location Permissions not available", Toast.LENGTH_SHORT).show();
            //Toast.makeText(this, "Go to Settings> Apps> Application List and enable location permissions for this App.", Toast.LENGTH_LONG).show();
        }
    }

    private boolean isServicesOK(){

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(OrdersPage.this);

        if(available == ConnectionResult.SUCCESS){
            //everything is fine and the user can make map requests
            return true;
        }
        else if(GoogleApiAvailability.getInstance().isUserResolvableError(available)){
            //an error occured but we can resolve it
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(OrdersPage.this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        }else{
            Toast.makeText(this, "You device does not support maps", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch(requestCode){
            case REQUEST_CHECK_SETTINGS:{
                if(resultCode==RESULT_OK) {

                    mLocationPermissionsGranted = true;


                }
                break;

            }

        }

    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    isLocationEnabled();

                } else {
                    Toast.makeText(this, "Permission denied for Fine & course location", Toast.LENGTH_SHORT).show();
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
            }
            /*case 28: {

                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    
                    return;
                }
                if(save_dcs.equals("yes")) {
                    mDialog1 = new ProgressDialog(this);
                    mDialog1.setMessage("wait...dcs");
                    mDialog1.show();
                    getContactList();
                }

            }*/
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    private void isLocationEnabled() {

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            AlertDialog.Builder alertDialog=new AlertDialog.Builder(this);
            alertDialog.setTitle("Enable Location");
            alertDialog.setMessage("Your locations setting is not enabled. Please enabled it in settings menu.");
            alertDialog.setPositiveButton("Location Settings", new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface dialog, int which){
                    Intent intent=new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);
                }
            });
            alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface dialog, int which){
                    dialog.cancel();
                }
            });
            AlertDialog alert=alertDialog.create();
            alert.show();
        }
    }

    public static boolean isConnectionAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();
            if (netInfo != null && netInfo.isConnected()
                    && netInfo.isConnectedOrConnecting()
                    && netInfo.isAvailable()) {
                return true;
            }
        }
        return false;
    }

    private void startLocationService(){
        Intent serviceIntent = new Intent(this, MyLocationService.class);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O){

            OrdersPage.this.startForegroundService(serviceIntent);
        }else{
            startService(serviceIntent);
        }
    }

    @Override
    public void onBackPressed() {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(OrdersPage.this);
        alertDialog.setMessage("Sure you want to Exit");

        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                finish();
            }
        });

        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();

            }
        });

        alertDialog.show();
    }

    private void loadDeliveryPersonsOrders() {
        final ProgressDialog mDialog = new ProgressDialog(OrdersPage.this);
        mDialog.setMessage("Loading Orders.....");
        mDialog.show();

        adapter = new FirebaseRecyclerAdapter<OrderRequest, IncomingOrderViewHolder>(OrderRequest.class,R.layout.layout_item_incoming_orders, IncomingOrderViewHolder.class,
                orderRequests.orderByChild("assignedPersonPhone").equalTo(Common.currentperson.getMobileNo())) {

            @Override
            protected void populateViewHolder(IncomingOrderViewHolder viewHolder, OrderRequest model, int position) {
                mDialog.dismiss();
                viewHolder.txtTime.setText(model.getTime());
                viewHolder.txtbill.setText(model.getGrandTotal());
                viewHolder.txtStatus.setText(model.getStatus());
                if(model.getStatus().equals("Placed") || model.getStatus().equals("InProgress") || model.getStatus().equals("On the way")){
                    viewHolder.txtStatus.setTextColor(getResources().getColor(R.color.red));
                }else {
                    viewHolder.txtStatus.setTextColor(getResources().getColor(R.color.green));
                }
                viewHolder.txtNoOfOrderItems.setText(model.getNoOfOrderItems());
                viewHolder.txtorderId.setText(model.getOrderId());
                viewHolder.txtDayAndDate.setText(model.getDay_and_date());
                viewHolder.txttimetaken.setText(model.getDeliveryTimeTaken());

                viewHolder.txtpaymentmethod.setText(model.getPayment_method());

                if(model.getMyLocation()==null) {
                    viewHolder.gpsflag.setText("GPS Location Absent");
                }
                if(model.getDelivery_rating()!=null) {
                    viewHolder.txt_delivery_rating.setText("Delivery boy rated : " + model.getDelivery_rating() + " Stars");
                }

                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        Intent intentOrderDetails = new Intent(OrdersPage.this, OrderDetails.class);
                        intentOrderDetails.putExtra("OrderId",adapter.getRef(position).getKey());
                        startActivity(intentOrderDetails);
                    }
                });

            }

            @Override
            public OrderRequest getItem(int position) {
                return super.getItem(getItemCount()-1-position);
            }

            @Override
            public DatabaseReference getRef(int position) {
                return super.getRef(getItemCount()-1-position);
            }

        };
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

    }


    public boolean onContextItemSelected(MenuItem item) {

        if(item.getTitle().equals("launch maps")) {
            if(adapter.getItem(item.getOrder()).getMyLocation()!=null) {
                String uri = "http://maps.google.com/maps?daddr=" + String.valueOf(adapter.getItem(item.getOrder()).getMyLocation().getLatitude()) + "," + String.valueOf(adapter.getItem(item.getOrder()).getMyLocation().getLongitude()) + " (" + "Delivery location" + ")";
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                intent.setPackage("com.google.android.apps.maps");
                startActivity(intent);
            }
        }else if(item.getTitle().equals("Update status")){

            showUpdateDialog(adapter.getRef(item.getOrder()).getKey(), adapter.getItem(item.getOrder()));
            //showUpdateDialog(adapter.getRef(adapter.getItemCount()-1-item.getOrder()).getKey(), adapter.getItem(adapter.getItemCount()-1-item.getOrder()));

        }

        return super.onContextItemSelected(item);

    }

    private void showUpdateDialog(final String key, final OrderRequest item) {

        if(item.getStatus().equals("On the way") || item.getStatus().equals("InProgress") || item.getStatus().equals("Placed")) {
            androidx.appcompat.app.AlertDialog.Builder alertDialog = new androidx.appcompat.app.AlertDialog.Builder(OrdersPage.this);
            alertDialog.setTitle("Update order");
            alertDialog.setMessage("Please select the status");

            LayoutInflater inflater = this.getLayoutInflater();
            final View view = inflater.inflate(R.layout.update_status_dialog, null);

            spinner = view.findViewById(R.id.statusSpinner);
            spinner.setText("Delivered");
            spinner.setItems("Delivered", "Cancelled", "Returned");

            alertDialog.setView(view);

            final String localKey = key;
            alertDialog.setPositiveButton("confirm", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();

                    item.setStatus(String.valueOf(spinner.getItems().get(spinner.getSelectedIndex())));
                    item.setPincode_status(item.getPincode() + "_" + String.valueOf(spinner.getItems().get(spinner.getSelectedIndex())));

                    Toast.makeText(OrdersPage.this, "key is "+localKey, Toast.LENGTH_LONG).show();

                    final ProgressDialog mDialog = new ProgressDialog(OrdersPage.this);
                    mDialog.setMessage("Please wait.........");
                    mDialog.show();
                    Toast.makeText(OrdersPage.this, "auth phone is "+auth.getCurrentUser().getPhoneNumber(), Toast.LENGTH_LONG).show();
                    orderRequests.child(localKey).child("status").setValue(String.valueOf(spinner.getItems().get(spinner.getSelectedIndex()))).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            mDialog.dismiss();

                            final ProgressDialog mDialog2 = new ProgressDialog(OrdersPage.this);
                            mDialog2.setMessage("Please wait.........");
                            mDialog2.show();

                            orderRequests.child(localKey).child("pincode_status").setValue(item.getPincode() + "_" + String.valueOf(spinner.getItems().get(spinner.getSelectedIndex()))).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    

                                    if (item.getStatus().equals("Delivered") || item.getStatus().equals("Cancelled") || item.getStatus().equals("Returned")) {


                                        String orderplaced_date_time = item.getDateTime();
                                        DateFormat dateFormat1 = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.ENGLISH);
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
                                                deliveryTimeTaken = diffHours + " hrs , " + diffMinutes + " mins, " + diffSeconds + " sec";
                                            } else {
                                                deliveryTimeTaken = diffMinutes + " mins, " + diffSeconds + " sec";
                                            }

                                            orderRequests.child(key).child("deliveryTimeTaken").setValue(deliveryTimeTaken).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {


                                                    orderRequests.child(key).child("timeCRDelivered").setValue(String.valueOf(timestamp_now)).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            mDialog2.dismiss();

                                                            loadDeliveryPersonsOrders();
                                                            Toast.makeText(OrdersPage.this, "Successfully saved time of delivery/return/cancellation", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }).addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            mDialog2.dismiss();
                                                            Toast.makeText(OrdersPage.this, "Failed to calculate time of delivery/return/cancellation", Toast.LENGTH_SHORT).show();
                                                        }
                                                    });

                                                    Toast.makeText(OrdersPage.this, "Successfully calculated Delivery/C/R time taken", Toast.LENGTH_SHORT).show();
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    mDialog2.dismiss();
                                                    Toast.makeText(OrdersPage.this, "Failed to calculate Delivery/C/R time taken", Toast.LENGTH_SHORT).show();
                                                }
                                            });

                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }


                                    }else {
                                        loadDeliveryPersonsOrders();
                                    }

                                    Toast.makeText(OrdersPage.this, "Successfully set pincode_status to" + item.getPincode_status(), Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    
                                    Toast.makeText(OrdersPage.this, "Failed to update pincode_status", Toast.LENGTH_SHORT).show();
                                }
                            });

                            Toast.makeText(OrdersPage.this, "Successfully set status to" + item.getStatus(), Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            mDialog.dismiss();
                            Toast.makeText(OrdersPage.this, "Failed to update status", Toast.LENGTH_SHORT).show();
                        }
                    });





                }
            });

            alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();

                }
            });

            alertDialog.show();

        }else {
            Toast.makeText(this, "You can not update status of a delivered/cancelled/returned order", Toast.LENGTH_LONG).show();
        }
    }


}